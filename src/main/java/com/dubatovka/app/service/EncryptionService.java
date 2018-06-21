package com.dubatovka.app.service;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * The class provides Service layer actions for data encryption.
 *
 * @author Dubatovka Vadim
 */
public final class EncryptionService {
    /**
     * Private constructor to forbid create {@link EncryptionService} instances.
     */
    private EncryptionService() {
    }
    
    /**
     * Encrypts any {@link String} object according to MD5 algorithm.
     *
     * @param source encrypting source
     * @return encrypted {@link String} value
     */
    public static String encryptMD5(String source) {
        String result = null;
        if (source != null) {
            result = DigestUtils.md5Hex(source);
        }
        return result;
    }
}