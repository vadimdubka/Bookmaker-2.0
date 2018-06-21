package com.dubatovka.app.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * The class represents information about 'Category' domain object.
 *
 * @author Dubatovka Vadim
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 5577191201787646890L;
    /**
     * Unique category id number
     */
    private int               id;
    /**
     * Category name
     */
    private String            name;
    /**
     * Id of parent category for this instance in hierarchical structure
     */
    private int               parentId;
    /**
     * Set of child categories for this instance in hierarchical structure
     */
    private Set<Category>     childCategorySet;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getParentId() {
        return parentId;
    }
    
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
    public Set<Category> getChildCategorySet() {
        return childCategorySet;
    }
    
    public void setChildCategorySet(Set<Category> childCategorySet) {
        this.childCategorySet = childCategorySet;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        Category that = (Category) o;
        
        return Objects.equals(childCategorySet, that.childCategorySet) &&
                   Objects.equals(id, that.id) &&
                   Objects.equals(name, that.name) &&
                   Objects.equals(parentId, that.parentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(childCategorySet, id, name, parentId);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("childCategorySet = " + childCategorySet)
                   .add("id = " + id)
                   .add("name = " + name)
                   .add("parentId = " + parentId)
                   .toString();
    }
}
