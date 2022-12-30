package org.stonexthree.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CryptoUtil {
    @Value("${app-config.security.salt}")
    private String salt;
    @Value("${app-config.security.password}")
    private String password;

    public TextEncryptor getTextEncryptor(){
        return Encryptors.text(this.password,this.salt);
    }

}
