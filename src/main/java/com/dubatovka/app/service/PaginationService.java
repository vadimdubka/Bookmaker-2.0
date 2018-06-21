package com.dubatovka.app.service;

/**
 * Interface for Service layer actions for pagination.
 *
 * @author Dubatovka Vadim
 */
public interface PaginationService {
    /**
     * Builds PaginationService objects while counts amount of pages and sets current page number
     * for given parameters.
     *
     * @param totalEntityAmount total entity amount (type int)
     * @param limitOnPage       limit on page (type int)
     * @param currentPage       current page number (type int)
     */
    void buildService(int totalEntityAmount, int limitOnPage, int currentPage);
    
    /**
     * Count and returns offset of this PaginationService object. Method must be invoked after
     * {@link #buildService} method. Otherwise method can cause {@link IllegalStateException}.
     *
     * @return offset (type int).
     */
    int getOffset();
    
    /**
     * Returns the limit of entities on a page of this PaginationService object.
     *
     * @return the limit of entities (type int).
     */
    int getLimitOnPage();
    
    /**
     * Returns the total entity amount of objects on a page of this PaginationService object.
     *
     * @return the total entity amount (type int).
     */
    int getTotalEntityAmount();
    
    /**
     * Returns the amount of pages of this PaginationService object.
     *
     * @return the amount of pages (type int).
     */
    int getAmountOfPages();
    
    /**
     * Returns current page number of this PaginationService object.
     *
     * @return current page number (type int).
     */
    int getCurrentPage();
}
