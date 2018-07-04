package com.dubatovka.app.service;

import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class ValidationServiceTest {
    @Autowired
    protected static ServiceFactory serviceFactory;
    private static ValidationService validationService;
    
    @BeforeClass
    public static void setUp() {
        validationService = serviceFactory.getValidationService();
    }
    
    @Test
    public void isValidEmailTest() {
        String validEmail = "any@valid.by";
        Assert.assertTrue(validationService.isValidEmail(validEmail));
        String inValidEmail = "anyInvalid@va_12lid.bbby";
        Assert.assertFalse(validationService.isValidEmail(inValidEmail));
    }
    
    @Test
    public void isValidPasswordTest() {
        String validPassword = "S-a100500";
        Assert.assertTrue(validationService.isValidPassword(validPassword));
        String inValidPassword = "abc8284771";
        Assert.assertFalse(validationService.isValidPassword(inValidPassword));
    }
    
    @Test
    public void isValidPasswordMatchTest() {
        String firstPassword = "S-a100500";
        String secondPassword = "S-a100500";
        Assert.assertTrue(validationService.isValidPassword(firstPassword, secondPassword));
        String secondInvalidPassword = "Sa100500";
        Assert.assertFalse(validationService.isValidPassword(firstPassword, secondInvalidPassword));
    }
    
    @Test
    public void isValidNameTest() {
        String validName = "AnyName";
        Assert.assertTrue(validationService.isValidName(validName));
        String invalidName = "любоеИмя";
        Assert.assertFalse(validationService.isValidName(invalidName));
    }
    
    @Test
    public void isValidBirthdateTest() {
        String validBirthdate = "1991-09-24";
        Assert.assertTrue(validationService.isValidBirthDate(validBirthdate));
        String invalidBirthdate = LocalDate.now().minusYears(17).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Assert.assertFalse(validationService.isValidBirthDate(invalidBirthdate));
    }
    
    @Test
    public void isValidBetAmountTest() {
        Assert.assertTrue(validationService.isValidBetAmount("20.1"));
        Assert.assertTrue(validationService.isValidBetAmount("20.19"));
        Assert.assertFalse(validationService.isValidBetAmount("1111.11"));
        Assert.assertFalse(validationService.isValidBetAmount("111.111"));
    }
    
    @Test
    public void isValidIdTest() {
        Assert.assertTrue(validationService.isValidId("1"));
        Assert.assertFalse(validationService.isValidId("0"));
    }
    
    @Test
    public void isValidOutcomeCoeffTest() {
        Assert.assertTrue(validationService.isValidOutcomeCoeff("1.25"));
        Assert.assertTrue(validationService.isValidOutcomeCoeff("99.99"));
        Assert.assertFalse(validationService.isValidOutcomeCoeff("1"));
        Assert.assertFalse(validationService.isValidOutcomeCoeff("100"));
    }
    
    @Test
    public void isValidBetTimeTest() {
        LocalDateTime betDT = LocalDateTime.now().minusSeconds(1);
        LocalDateTime eventDT = LocalDateTime.now();
        Assert.assertTrue(validationService.isValidBetTime(betDT, eventDT));
        LocalDateTime evenInvalidDT = LocalDateTime.now();
        LocalDateTime betInvalidDT = LocalDateTime.now();
        Assert.assertFalse(validationService.isValidBetTime(betInvalidDT, evenInvalidDT));
    }
    
    @Test
    public void isValidEventDateTimeTest() {
        String date = String.valueOf(LocalDateTime.now().plusDays(1));
        String dateInvalid = String.valueOf(LocalDateTime.now());
        Assert.assertTrue(validationService.isValidEventDateTime(date));
        Assert.assertFalse(validationService.isValidEventDateTime(dateInvalid));
    }
    
    @Test
    public void isValidEventResultTest() {
        Assert.assertTrue(validationService.isValidEventResult("0"));
        Assert.assertTrue(validationService.isValidEventResult("999"));
        Assert.assertFalse(validationService.isValidEventResult("1000"));
        Assert.assertFalse(validationService.isValidEventResult("-1"));
        Assert.assertFalse(validationService.isValidEventResult("1.5"));
        Assert.assertFalse(validationService.isValidEventResult("string"));
    }
    
    @Test
    public void isValidEventParticipantNameTest() {
        Assert.assertTrue(validationService.isValidEventParticipantName("AbstrController"));
        Assert.assertTrue(validationService.isValidEventParticipantName("Команда"));
        Assert.assertTrue(validationService.isValidEventParticipantName("Команда мечты."));
        Assert.assertTrue(validationService.isValidEventParticipantName("Команда-мечты. "));
        Assert.assertTrue(validationService.isValidEventParticipantName("654"));
        Assert.assertFalse(validationService.isValidEventParticipantName(" Команда"));
        Assert.assertFalse(validationService.isValidEventParticipantName("-Команда"));
    }
    
    @Test
    public void isValidRequestParamTest() {
        String valid1 = "str";
        String valid2 = "str2";
        String invalid = null;
        Assert.assertTrue(validationService.isValidRequestParam(valid1, valid2));
        Assert.assertFalse(validationService.isValidRequestParam(valid1, valid2, invalid));
    }
    
    @Test
    public void isValidOutcomeCoeffOnPageTest() {
        Event event = new Event();
        Outcome type1 = new Outcome();
        Outcome typeX = new Outcome();
        Outcome type2 = new Outcome();
        type1.setType(Outcome.Type.TYPE_1);
        typeX.setType(Outcome.Type.TYPE_X);
        type2.setType(Outcome.Type.TYPE_2);
        type1.setCoefficient(new BigDecimal("1.5"));
        typeX.setCoefficient(new BigDecimal("2.5"));
        type2.setCoefficient(new BigDecimal("3.5"));
        Set<Outcome> outcomeSet = new HashSet<>();
        outcomeSet.add(type1);
        outcomeSet.add(typeX);
        outcomeSet.add(type2);
        event.setOutcomeSet(outcomeSet);
        
        String outcomeCoeffOnPage = "1.5";
        String outcomeType = "1";
        Assert.assertTrue(validationService.isValidOutcomeCoeffOnPage(outcomeCoeffOnPage, event, outcomeType));
        String outcomeCoeffOnPageInvalid = "2.5";
        Assert.assertFalse(validationService.isValidOutcomeCoeffOnPage(outcomeCoeffOnPageInvalid, event, outcomeType));
        
    }
}
