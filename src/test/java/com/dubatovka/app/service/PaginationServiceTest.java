package com.dubatovka.app.service;

import com.dubatovka.app.service.impl.ServiceFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PaginationServiceTest {
    private static PaginationService paginationService;
    
    @BeforeClass
    public static void setUp() {
        paginationService = ServiceFactory.getPaginationService();
        paginationService.buildService(15, 5, 2);
    }
    
    @Test
    public void getLimitOnPageTest() {
        Assert.assertEquals(5, paginationService.getLimitOnPage());
        Assert.assertNotEquals(6, paginationService.getLimitOnPage());
    }
    
    @Test
    public void getTotalEntityAmountTest() {
        Assert.assertEquals(15, paginationService.getTotalEntityAmount());
        Assert.assertNotEquals(16, paginationService.getTotalEntityAmount());
    }
    
    @Test
    public void getAmountOfPagesTest() {
        Assert.assertEquals(3, paginationService.getAmountOfPages());
        Assert.assertNotEquals(4, paginationService.getTotalEntityAmount());
    }
    
    @Test
    public void getCurrentPageTest() {
        Assert.assertEquals(2, paginationService.getCurrentPage());
        Assert.assertNotEquals(1, paginationService.getCurrentPage());
    }
    
    @Test
    public void getOffsetTest() {
        Assert.assertEquals(5, paginationService.getOffset());
        Assert.assertNotEquals(4, paginationService.getOffset());
        paginationService.getOffset();
    }
    
    @Test(expected = IllegalStateException.class)
    public void getOffsetExceptionTest() {
        PaginationService paginationService = ServiceFactory.getPaginationService();
        paginationService.getOffset();
    }
}