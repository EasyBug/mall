package com.mmall.service;

import com.mmall.common.ServerRespose;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerRespose addCategory(String categoryName, Integer parentId);

    ServerRespose updateCategoryName(Integer categoryId, String categoryName);

    ServerRespose<List<Category>> getChildrenParallelCategory(Integer catagoryId);

    ServerRespose<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
