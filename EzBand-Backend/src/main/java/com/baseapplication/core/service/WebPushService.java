package com.baseapplication.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baseapplication.core.dao.PushSubscriptionDao;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.PushSubscription;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushService {

    private final PushSubscriptionDao pushSubscriptionDao;
    private final ObjectMapper objectMapper;

    @Value("${vapid.public-key}")
    private String vapidPublicKeyBase64;

    @Value("${vapid.private-key}")
    private String vapidPrivateKeyBase64;

    @Value("${vapid.subject}")
    private String vapidSubject;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void enviarDireto(Long usuarioId, String titulo, String mensagem) {
        List<PushSubscription> subscriptions = pushSubscriptionDao.findByUsuarioId(usuarioId);
        if (subscriptions.isEmpty()) return;
        for (PushSubscription sub : subscriptions) {
            try {
                String json = objectMapper.writeValueAsString(Map.of("titulo", titulo, "mensagem", mensagem));
                sendPush(sub, json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.warn("[WebPush] Falha ao enviar push direto para usuário {}: {}", usuarioId, e.getMessage());
            }
        }
    }

    public void enviar(Notificacao notificacao) {
        if (!TipoParticipante.USUARIO.equals(notificacao.getDestinatarioTipo())) return;

        Long usuarioId = notificacao.getDestinatarioId();
        List<PushSubscription> subscriptions = pushSubscriptionDao.findByUsuarioId(usuarioId);
        if (subscriptions.isEmpty()) return;

        for (PushSubscription sub : subscriptions) {
            try {
                String json = objectMapper.writeValueAsString(Map.of(
                        "titulo", notificacao.getTitulo() != null ? notificacao.getTitulo() : "EzBand",
                        "mensagem", notificacao.getMensagem() != null ? notificacao.getMensagem() : ""
                ));
                sendPush(sub, json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.warn("[WebPush] Falha ao enviar push para usuário {}: {}", usuarioId, e.getMessage());
            }
        }
    }

    private void sendPush(PushSubscription sub, byte[] plaintext) throws Exception {
        byte[] receiverPub = b64decode(sub.getP256dh()); // 65-byte uncompressed EC point
        byte[] authSecret  = b64decode(sub.getAuth());   // 16-byte auth secret

        // Ephemeral EC key pair (P-256)
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair ephemeral = kpg.generateKeyPair();
        byte[] senderPub = encodeEcPoint((ECPublicKey) ephemeral.getPublic());

        // ECDH shared secret
        KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
        ecdh.init(ephemeral.getPrivate());
        ecdh.doPhase(decodeEcPoint(receiverPub), true);
        byte[] ecdhSecret = ecdh.generateSecret();

        // Random salt
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        // Level 1 HKDF (RFC 8291 §3.1): derive IKM
        byte[] info1 = concat("WebPush: info\0".getBytes(), receiverPub, senderPub);
        byte[] ikm = hkdf(authSecret, ecdhSecret, info1, 32);

        // Level 2 HKDF (RFC 8188): derive content encryption key + nonce
        byte[] cek   = hkdf(salt, ikm, "Content-Encoding: aes128gcm\0".getBytes(), 16);
        byte[] nonce = hkdf(salt, ikm, "Content-Encoding: nonce\0".getBytes(), 12);

        // Encrypt: AES-128-GCM, plaintext + 0x02 delimiter
        byte[] padded = Arrays.copyOf(plaintext, plaintext.length + 1);
        padded[plaintext.length] = 0x02;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cek, "AES"), new GCMParameterSpec(128, nonce));
        byte[] ciphertext = cipher.doFinal(padded);

        // aes128gcm body: salt(16) || rs(4) || idlen(1) || senderPub(65) || ciphertext
        ByteBuffer body = ByteBuffer.allocate(16 + 4 + 1 + 65 + ciphertext.length);
        body.put(salt);
        body.putInt(4096);
        body.put((byte) 65);
        body.put(senderPub);
        body.put(ciphertext);

        // VAPID JWT (RFC 8292)
        URI endpointUri = URI.create(sub.getEndpoint());
        String origin = endpointUri.getScheme() + "://" + endpointUri.getHost();
        ECPrivateKey vapidPrivKey = decodePrivateKey(vapidPrivateKeyBase64);
        String jwt = JWT.create()
                .withAudience(origin)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(12))))
                .withSubject(vapidSubject)
                .sign(Algorithm.ECDSA256(null, vapidPrivKey));

        String vapidPubB64 = b64encode(b64decode(vapidPublicKeyBase64));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(endpointUri)
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/octet-stream")
                .header("Content-Encoding", "aes128gcm")
                .header("Authorization", "vapid t=" + jwt + ",k=" + vapidPubB64)
                .header("TTL", "86400")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.array()))
                .build();

        HttpResponse<Void> response = HTTP.send(request, HttpResponse.BodyHandlers.discarding());
        int status = response.statusCode();

        if (status == 410 || status == 404) {
            log.info("[WebPush] Subscription expirada, removendo");
            pushSubscriptionDao.delete(sub);
        } else if (status != 201 && status != 200 && status != 202) {
            log.warn("[WebPush] Resposta HTTP {} para push do usuário {}", status, sub.getUsuarioId());
        }
    }

    // HKDF-Extract + HKDF-Expand (RFC 5869)
    private byte[] hkdf(byte[] salt, byte[] ikm, byte[] info, int length) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        // Extract
        mac.init(new SecretKeySpec(salt.length == 0 ? new byte[32] : salt, "HmacSHA256"));
        byte[] prk = mac.doFinal(ikm);
        // Expand
        mac.init(new SecretKeySpec(prk, "HmacSHA256"));
        byte[] result = new byte[length];
        byte[] t = new byte[0];
        for (int i = 1, pos = 0; pos < length; i++) {
            mac.update(t);
            mac.update(info);
            mac.update((byte) i);
            t = mac.doFinal();
            int copy = Math.min(t.length, length - pos);
            System.arraycopy(t, 0, result, pos, copy);
            pos += copy;
        }
        return result;
    }

    private ECPublicKey decodeEcPoint(byte[] encoded) throws Exception {
        AlgorithmParameters p = AlgorithmParameters.getInstance("EC");
        p.init(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec spec = p.getParameterSpec(ECParameterSpec.class);
        BigInteger x = new BigInteger(1, Arrays.copyOfRange(encoded, 1, 33));
        BigInteger y = new BigInteger(1, Arrays.copyOfRange(encoded, 33, 65));
        return (ECPublicKey) KeyFactory.getInstance("EC")
                .generatePublic(new ECPublicKeySpec(new ECPoint(x, y), spec));
    }

    private byte[] encodeEcPoint(ECPublicKey key) {
        byte[] result = new byte[65];
        result[0] = 0x04;
        System.arraycopy(norm32(key.getW().getAffineX().toByteArray()), 0, result, 1, 32);
        System.arraycopy(norm32(key.getW().getAffineY().toByteArray()), 0, result, 33, 32);
        return result;
    }

    private ECPrivateKey decodePrivateKey(String base64) throws Exception {
        byte[] bytes = b64decode(base64);
        AlgorithmParameters p = AlgorithmParameters.getInstance("EC");
        p.init(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec spec = p.getParameterSpec(ECParameterSpec.class);
        return (ECPrivateKey) KeyFactory.getInstance("EC")
                .generatePrivate(new ECPrivateKeySpec(new BigInteger(1, bytes), spec));
    }

    private byte[] norm32(byte[] b) {
        byte[] r = new byte[32];
        if (b.length >= 32) {
            System.arraycopy(b, b.length - 32, r, 0, 32);
        } else {
            System.arraycopy(b, 0, r, 32 - b.length, b.length);
        }
        return r;
    }

    private byte[] b64decode(String s) {
        String padded = s + "=".repeat((4 - s.length() % 4) % 4);
        return Base64.getUrlDecoder().decode(padded);
    }

    private String b64encode(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private byte[] concat(byte[]... arrays) {
        int len = 0;
        for (byte[] a : arrays) len += a.length;
        byte[] result = new byte[len];
        int pos = 0;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }
}
