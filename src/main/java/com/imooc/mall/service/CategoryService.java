package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类目录service
 */
public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    // 根据ID删除分类
    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
