package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.CategoryDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.service.CategoryService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class provides implementation for Service layer actions with Categories.
 *
 * @author Dubatovka Vadim
 */
class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);
    private static final Lock lock = new ReentrantLock();
    
    /**
     * Field is used to define whether any category was modified. If it was then field turns into
     * true and new call of {@link #getSportCategories()} forwards to database to update {@link
     * #sportCategories} collection.
     */
    private static final AtomicBoolean isCategoriesModified = new AtomicBoolean(false);
    /**
     * Set of top level categories
     */
    private static Set<Category> sportCategories;
    
    /**
     * DAOProvider instance for this class instance use.
     */
    private final DAOProvider daoProvider;
    private final CategoryDAO categoryDAO;
    
    /**
     * Default constructor.
     */
    CategoryServiceImpl() {
        this.daoProvider = new DAOProvider();
        this.categoryDAO = daoProvider.getCategoryDAO();
    }
    
    /**
     * Calls DAO layer to get {@link Set} of sport {@link Category}.
     *
     * @return {@link Set} of {@link Category}
     */
    @Override
    public Set<Category> getSportCategories() {
        if ((sportCategories == null) || isCategoriesModified.get()) {
            lock.lock();
            try {
                if ((sportCategories == null) || isCategoriesModified.get()) {
                    Set<Category> categorySet = categoryDAO.readAllCategories();
                    sportCategories = buildCategoryHierarchy(categorySet);
                    isCategoriesModified.set(false);
                }
            } catch (DAOException e) {
                logger.log(Level.ERROR, e.getMessage());
            } finally {
                lock.unlock();
            }
        }
        return sportCategories;
    }
    
    /**
     * Calls DAO layer to get {@link Category} from database which correspond to given {@link
     * Category} id.
     *
     * @param id {@link Category} id
     * @return {@link Category}
     */
    @Override
    public Category getCategoryById(int id) {
        Category categoryResult = null;
        try {
            categoryResult = categoryDAO.readCategoryById(id);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        
        return categoryResult;
    }
    
    /**
     * Builds hierarchy of categories by dividing them on parent and child categories and assigning
     * child categories to parent.
     *
     * @param categorySet {@link Iterable< Category >} collection of categories.
     * @return {@link Set< Category >} if hierarchical order
     */
    private Set<Category> buildCategoryHierarchy(Iterable<Category> categorySet) {
        Map<Integer, Category> sportCategoriesMap = pickOutSportCategories(categorySet);
        fillSportWithCategories(sportCategoriesMap, categorySet);
        
        Set<Category> sportCategoriesSet = new HashSet<>(sportCategoriesMap.values());
        return sportCategoriesSet;
    }
    
    private static Map<Integer, Category> pickOutSportCategories(Iterable<Category> categorySet) {
        Map<Integer, Category> sportCategoriesMap = new HashMap<>();
        Iterator<Category> iterator = categorySet.iterator();
        while (iterator.hasNext()) {
            Category category = iterator.next();
            if (category.getParentId() == 0) {
                category.setChildCategorySet(new HashSet<>());
                sportCategoriesMap.put(category.getId(), category);
                iterator.remove();
            }
        }
        return sportCategoriesMap;
    }
    
    private static void fillSportWithCategories(Map<Integer, Category> sportCategoriesMap,
                                                Iterable<Category> categorySet) {
        categorySet.forEach(category -> {
            int parentId = category.getParentId();
            Category sport = sportCategoriesMap.get(parentId);
            sport.getChildCategorySet().add(category);
        });
    }
    
    /**
     * Returns {@link DAOProvider#connection} to {@link ConnectionPool}.
     */
    @Override
    public void close() {
        daoProvider.close();
    }
}