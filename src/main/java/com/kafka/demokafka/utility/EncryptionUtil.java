package com.kafka.demokafka.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private final SecretKey key;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptionUtil(@Value("${encryption.key}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        if (decodedKey.length != 16 &&
                decodedKey.length != 24 &&
                decodedKey.length != 32) {
            throw new IllegalArgumentException(
                    "AES key must be 16, 24, or 32 bytes"
            );
        }
        this.key = new SecretKeySpec(decodedKey,"AES");
    }

    public String encrypt(String plainText) throws Exception{
        byte[] iv = new byte[IV_LENGTH];

        //SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE,key,spec);

        byte[] encrypted = cipher.doFinal(
                plainText.getBytes(StandardCharsets.UTF_8)
        );

        ByteBuffer buffer = ByteBuffer.allocate(
                iv.length + encrypted.length
        );

        buffer.put(iv);
        buffer.put(encrypted);

        return Base64.getEncoder().encodeToString(buffer.array());
    }

    public String decrypt(String encryptedText) throws  Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        if (decoded.length <= IV_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid encrypted data"
            );
        }

        ByteBuffer buffer = ByteBuffer.wrap(decoded);
        byte[] iv = new byte[IV_LENGTH];
        buffer.get(iv);

        byte[] encrypted = new byte[buffer.remaining()];
        buffer.get(encrypted);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted,StandardCharsets.UTF_8);

    }
}
