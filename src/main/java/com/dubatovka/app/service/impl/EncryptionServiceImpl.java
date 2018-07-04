package com.dubatovka.app.service.impl;

import com.dubatovka.app.service.EncryptionService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * The class provides Service layer actions for data encryption.
 *
 * @author Dubatovka Vadim
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {
    
    /**
     * Encrypts any {@link String} object according to MD5 algorithm.
     *
     * @param source encrypting source
     * @return encrypted {@link String} value
     */
    @Override
    public String encryptMD5(String source) {
        String result = null;
        if (source != null) {
            result = DigestUtils.md5Hex(source);
        }
        return result;
    }
}