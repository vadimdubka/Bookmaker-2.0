package com.dubatovka.app.service;

import org.junit.Assert;
import org.junit.Test;

public class EncryptionServiceTest {
    @Test
    public void encryptMD5Test() {
        String pass = "passTo5Encr";
        String actualEncr = EncryptionService.encryptMD5(pass);
        String expectedEncr = EncryptionService.encryptMD5(pass);
        Assert.assertEquals(expectedEncr, actualEncr);
    }
    
}