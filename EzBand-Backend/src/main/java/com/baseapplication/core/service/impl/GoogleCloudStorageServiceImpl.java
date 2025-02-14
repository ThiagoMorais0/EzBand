package com.baseapplication.core.service.impl;

import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.UsuarioService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
public class GoogleCloudStorageServiceImpl {
    private final String bucketName = "ezband-images"; // Nome do bucket do Google Cloud Storage

    private final Storage storage;

    public GoogleCloudStorageServiceImpl() {
//        storage = StorageOptions.getDefaultInstance().getService();

        try {
            String s3Url = "https://ezband-bucket.s3.amazonaws.com/sunlit-virtue-382013-efe3e81b47ab.json";
            URL url = new URL(s3Url);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            // Carregar as credenciais de serviço a partir do arquivo
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
//                    new FileInputStream("D:/Dev/IntelliJ/api/src/main/java/com/ezband/api/key/sunlit-virtue-382013-efe3e81b47ab.json"));

            // Configurar as credenciais e criar a instância do Storage
            storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException e) {
            // Tratar o erro ao carregar as credenciais
            throw new RuntimeException("Erro ao carregar as credenciais do Google Cloud Storage.", e);
        }
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, file.getBytes());

        return blob.getMediaLink();
    }

}
