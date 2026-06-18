package com.baseapplication.core.controller;

import com.baseapplication.core.dao.PushSubscriptionDao;
import com.baseapplication.core.dto.PushSubscriptionRequest;
import com.baseapplication.core.model.PushSubscription;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushSubscriptionDao pushSubscriptionDao;

    @Value("${vapid.public-key}")
    private String vapidPublicKey;

    @GetMapping("/vapid-public-key")
    public ResponseEntity<String> getVapidPublicKey() {
        return ResponseEntity.ok(vapidPublicKey);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody PushSubscriptionRequest request) {
        Usuario usuario = Context.getUsuarioLogado();
        if (usuario.getId() == null) return ResponseEntity.status(401).build();

        pushSubscriptionDao.findByEndpoint(request.endpoint()).ifPresentOrElse(
            existing -> {
                existing.setP256dh(request.p256dh());
                existing.setAuth(request.auth());
                pushSubscriptionDao.save(existing);
                log.debug("[WebPush] Subscription atualizada para usuário {}", usuario.getId());
            },
            () -> {
                PushSubscription sub = new PushSubscription();
                sub.setUsuarioId(usuario.getId());
                sub.setEndpoint(request.endpoint());
                sub.setP256dh(request.p256dh());
                sub.setAuth(request.auth());
                pushSubscriptionDao.save(sub);
                log.info("[WebPush] Nova subscription registrada para usuário {}", usuario.getId());
            }
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody Map<String, String> body) {
        String endpoint = body.get("endpoint");
        if (endpoint != null) {
            pushSubscriptionDao.deleteByEndpoint(endpoint);
            log.info("[WebPush] Subscription removida por logout");
        }
        return ResponseEntity.noContent().build();
    }
}
