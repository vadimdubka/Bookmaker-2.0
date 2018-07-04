package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.CategoryDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class provides {@link CategoryDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class CategoryDAOImpl extends DBConnectionHolder implements CategoryDAO {
    private static final String SQL_SELECT_CATEGORY_BY_ID = "SELECT id, name, parent_id " +
                                                                "FROM category WHERE id=?";
    
    private static final String SQL_SELECT_ALL_CATEGORIES = "SELECT id, name, parent_id " +
                                                                "FROM category ORDER BY id";
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    CategoryDAOImpl() {
    }
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    CategoryDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
    
    /**
     * Reads {@link Category} from database which correspond to given {@link
     * Category} id.
     *
     * @param id {@link Category} id
     * @return {@link Category}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Category readCategoryById(int id) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_CATEGORY_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            Category category = null;
            if (resultSet.next()) {
                category = buildCategory(resultSet);
            }
            return category;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while getting sport category. " + e);
        }
    }
    
    /**
     * Reads {@link Set} of all {@link Category} from database.
     *
     * @return {@link Set} of {@link Category}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Set<Category> readAllCategories() throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL_CATEGORIES)) {
            ResultSet resultSet = statement.executeQuery();
            return buildCategorySet(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while getting sport categories. " + e);
        }
    }
    
    /**
     * Build {@link Set} of {@link Category} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Set} of {@link Category}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Set<Category> buildCategorySet(ResultSet resultSet) throws SQLException {
        Set<Category> categorySet = new HashSet<>();
        while (resultSet.next()) {
            Category category = buildCategory(resultSet);
            categorySet.add(category);
        }
        return categorySet;
    }
    
    /**
     * Method build {@link Category} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Category}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Category buildCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getInt(ID));
        category.setName(resultSet.getString(NAME));
        category.setParentId(resultSet.getInt(PARENT_ID));
        return category;
    }
}