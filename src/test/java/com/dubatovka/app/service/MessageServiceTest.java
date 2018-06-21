package com.dubatovka.app.service;

import com.dubatovka.app.service.impl.ServiceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static com.dubatovka.app.config.ConfigConstant.COUNTRY_RU;
import static com.dubatovka.app.config.ConfigConstant.COUNTRY_US;
import static com.dubatovka.app.config.ConfigConstant.LANG_EN;
import static com.dubatovka.app.config.ConfigConstant.LANG_RU;

public class MessageServiceTest {
    private static final String         PATH_TO_BUNDLE = "testcontent/messages";
    private static final Locale         LOCALE_EN_US = new Locale(LANG_EN, COUNTRY_US);
    private static final Locale         LOCALE_RU_RU = new Locale(LANG_RU, COUNTRY_RU);
    private static final String         TEST_LINE_EN = "Test line.";
    private static final String         TEST_LINE_RU = "Тестовая строка.";
    private static final String         KEY = "test.line";
    private              MessageService messageServiceEn;
    private              MessageService messageServiceRu;
    
    @Before
    public void setUp() {
        messageServiceEn = ServiceFactory.getMessageService(LOCALE_EN_US, PATH_TO_BUNDLE);
        messageServiceRu = ServiceFactory.getMessageService(LOCALE_RU_RU, PATH_TO_BUNDLE);
    }
    
    @Test
    public void appendErrMessByKeyTest() {
        messageServiceEn.appendErrMessByKey(KEY);
        String actualEn = messageServiceEn.getErrMessContent();
        Assert.assertEquals(TEST_LINE_EN, actualEn);
        
        messageServiceRu.appendErrMessByKey(KEY);
        String actualRu = messageServiceRu.getErrMessContent();
        Assert.assertEquals(TEST_LINE_RU, actualRu);
    }
    
    @Test
    public void appendInfMessByKeyTest() {
        messageServiceEn.appendInfMessByKey(KEY);
        String actualEn = messageServiceEn.getInfMessContent();
        Assert.assertEquals(TEST_LINE_EN, actualEn);
        
        messageServiceRu.appendInfMessByKey(KEY);
        String actualRu = messageServiceRu.getInfMessContent();
        Assert.assertEquals(TEST_LINE_RU, actualRu);
    }
    
    @Test
    public void appendErrMessTest() {
        messageServiceEn.appendErrMess(TEST_LINE_EN);
        String actualEn = messageServiceEn.getErrMessContent();
        Assert.assertEquals(TEST_LINE_EN, actualEn);
        
        messageServiceRu.appendErrMess(TEST_LINE_RU);
        String actualRu = messageServiceRu.getErrMessContent();
        Assert.assertEquals(TEST_LINE_RU, actualRu);
    }
    
    @Test
    public void appendInfMessTest() {
        messageServiceEn.appendInfMess(TEST_LINE_EN);
        String actualEn = messageServiceEn.getInfMessContent();
        Assert.assertEquals(TEST_LINE_EN, actualEn);
        
        messageServiceRu.appendInfMess(TEST_LINE_RU);
        String actualRu = messageServiceRu.getInfMessContent();
        Assert.assertEquals(TEST_LINE_RU, actualRu);
    }
    
    @Test
    public void isErrMessEmptyTest() {
        Assert.assertTrue(messageServiceEn.isErrMessEmpty());
        messageServiceEn.appendErrMessByKey(KEY);
        Assert.assertFalse(messageServiceEn.isErrMessEmpty());
    }
    
    @Test
    public void isInfMessEmptyTest() {
        Assert.assertTrue(messageServiceEn.isInfMessEmpty());
        messageServiceEn.appendInfMessByKey(KEY);
        Assert.assertFalse(messageServiceEn.isInfMessEmpty());
    }
    
    @Test
    public void getMessageByKeyTest() {
        String actualEn = messageServiceEn.getMessageByKey(KEY);
        String actualRu = messageServiceRu.getMessageByKey(KEY);
        Assert.assertEquals(TEST_LINE_EN, actualEn);
        Assert.assertEquals(TEST_LINE_RU, actualRu);
    }
    
    @Test
    public void getLocaleTest() {
        Assert.assertEquals(LOCALE_EN_US, messageServiceEn.getLocale());
        Assert.assertEquals(LOCALE_RU_RU, messageServiceRu.getLocale());
    }
}