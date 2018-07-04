package com.dubatovka.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EncryptionServiceImplTest {
    @Autowired
    EncryptionService encryptionService;
    
    @Test
    public void encryptMD5Test() {
        String pass = "passTo5Encr";
        String actualEncr = encryptionService.encryptMD5(pass);
        String expectedEncr = encryptionService.encryptMD5(pass);
        Assert.assertEquals(expectedEncr, actualEncr);
    }
    
}