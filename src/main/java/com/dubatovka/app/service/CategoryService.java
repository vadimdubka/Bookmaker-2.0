package com.dubatovka.app.service;

import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Category;

import java.util.Set;

/**
 * The class provides abstraction for Service layer actions with Categories.
 *
 * @author Dubatovka Vadim
 */
public abstract class CategoryService extends DAOProviderHolder {
    
    /**
     * Default constructor.
     */
    protected CategoryService() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    protected CategoryService(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get {@link Set} of sport {@link Category}.
     *
     * @return {@link Set} of {@link Category}
     */
    public abstract Set<Category> getSportCategories();
    
    /**
     * Calls DAO layer to get {@link Category} from database which correspond to given {@link
     * Category} id.
     *
     * @param id {@link Category} id
     * @return {@link Category}
     */
    public abstract Category getCategoryById(int id);
}
