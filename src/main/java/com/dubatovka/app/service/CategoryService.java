package com.dubatovka.app.service;

import com.dubatovka.app.entity.Category;

import java.util.Set;

/**
 * The class provides abstraction for Service layer actions with Categories.
 *
 * @author Dubatovka Vadim
 */
public interface CategoryService extends AutoCloseable {
    
    /**
     * Calls DAO layer to get {@link Set} of sport {@link Category}.
     *
     * @return {@link Set} of {@link Category}
     */
    Set<Category> getSportCategories();
    
    /**
     * Calls DAO layer to get {@link Category} from database which correspond to given {@link
     * Category} id.
     *
     * @param id {@link Category} id
     * @return {@link Category}
     */
    Category getCategoryById(int id);
    
    @Override
    void close();
}
