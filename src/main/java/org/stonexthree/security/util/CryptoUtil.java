package org.stonexthree.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class CryptoUtil {
    private static String salt = "4831a5d7a60dc285";
    private static String password = "D3UUB2z2D4pGnXdw";

    public static TextEncryptor getTextEncryptor(){
        return Encryptors.text(password,salt);
    }

}
