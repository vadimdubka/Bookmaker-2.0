package com.dubatovka.app.service.impl;

import com.dubatovka.app.service.PaginationService;

/**
 * Implementation of Service layer class for pagination actions.
 *
 * @author Dubatovka Vadim
 */
class PaginationServiceImpl implements PaginationService {
    private static final int DEFAULT_LIMIT_ON_PAGE       = 5;
    private static final int DEFAULT_TOTAL_ENTITY_AMOUNT = 0;
    private static final int DEFAULT_AMOUNT_OF_PAGES     = 0;
    private static final int DEFAULT_CURRENT_PAGE        = 1;
    
    private int     limitOnPage       = DEFAULT_LIMIT_ON_PAGE;
    private int     totalEntityAmount = DEFAULT_TOTAL_ENTITY_AMOUNT;
    private int     amountOfPages     = DEFAULT_AMOUNT_OF_PAGES;
    private int     currentPage       = DEFAULT_CURRENT_PAGE;
    private boolean isStateValid      = false;
    
    /**
     * Returns the limit of entities on a page of this PaginationService object.
     *
     * @return the limit of entities (type int).
     */
    @Override
    public int getLimitOnPage() {
        return limitOnPage;
    }
    
    /**
     * Returns the total entity amount of objects on a page of this PaginationService object.
     *
     * @return the total entity amount (type int).
     */
    @Override
    public int getTotalEntityAmount() {
        return totalEntityAmount;
    }
    
    /**
     * Returns the amount of pages of this PaginationService object.
     *
     * @return the amount of pages (type int).
     */
    @Override
    public int getAmountOfPages() {
        return amountOfPages;
    }
    
    /**
     * Returns current page number of this PaginationService object.
     *
     * @return current page number (type int).
     */
    @Override
    public int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * Builds PaginationService objects while counts amount of pages and sets current page number
     * for given parameters.
     *
     * @param totalEntityAmount total entity amount (type int)
     * @param limitOnPage       limit on page (type int)
     * @param currentPage       current page number (type int)
     */
    @Override
    public void buildService(int totalEntityAmount, int limitOnPage, int currentPage) {
        this.totalEntityAmount = totalEntityAmount;
        this.limitOnPage = limitOnPage;
        countAmountOfPages();
        setCurrentPage(currentPage);
        isStateValid = true;
    }
    
    /**
     * Count and returns offset of this PaginationService object. Method must be invoked after
     * {@link #buildService} method. Otherwise method can cause {@link IllegalStateException}.
     *
     * @return offset (type int).
     */
    @Override
    public int getOffset() {
        if (!isStateValid) {
            throw new IllegalStateException("Pagination service is not built.");
        }
        
        int amountOfPreviousPages = currentPage - 1;
        if (amountOfPreviousPages < 0) {
            amountOfPreviousPages = 0;
        }
        int offset = amountOfPreviousPages * limitOnPage;
        
        return offset;
    }
    
    
    private void countAmountOfPages() {
        if (totalEntityAmount > 0) {
            int integerPart = totalEntityAmount / limitOnPage;
            int remainder = totalEntityAmount % limitOnPage;
            if (remainder == 0) {
                amountOfPages = integerPart;
            } else {
                amountOfPages = integerPart + 1;
            }
        } else {
            amountOfPages = 0;
        }
    }
    
    private void setCurrentPage(int currentPage) {
        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > amountOfPages) {
            currentPage = amountOfPages;
        }
        this.currentPage = currentPage;
    }
}
