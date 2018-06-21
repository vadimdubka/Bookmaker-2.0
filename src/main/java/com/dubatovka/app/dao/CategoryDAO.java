package com.dubatovka.app.dao;

import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Category;

import java.sql.SQLException;
import java.util.Set;

/**
 * DAO interface for {@link Category} objects.
 *
 * @author Dubatovka Vadim
 */
public interface CategoryDAO {
    /**
     * Column names of database table 'category'.
     */
    String ID        = "id";
    String NAME      = "name";
    String PARENT_ID = "parent_id";
    
    /**
     * Reads {@link Set} of all {@link Category} from database.
     *
     * @return {@link Set} of {@link Category}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Set<Category> readAllCategories() throws DAOException;
    
    /**
     * Reads {@link Category} from database which correspond to given {@link
     * Category} id.
     *
     * @param id {@link Category} id
     * @return {@link Category}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Category readCategoryById(int id) throws DAOException;
}
