/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private Integer categoryId;
    private String categoryName;
    private String categoryDesc;

    public Category(Integer categoryId, String categoryName, String categoryDesc) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDesc = categoryDesc;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
