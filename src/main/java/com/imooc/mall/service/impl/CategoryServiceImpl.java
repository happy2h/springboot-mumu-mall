package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类目录实现类
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryReq addCategoryReq){
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categgoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if(categgoryOld != null){ // 之前存在重名的目录
            throw new ImoocMallException(ImoocMailExceptionEnum.NAME_DUPLICATED);
        }
        int count = categoryMapper.insertSelective(category);
        if(count == 0){
            throw new ImoocMallException(ImoocMailExceptionEnum.CREATED_FAILED);
        }
    }

    /**
     * 更新目录
     * @param updateCategory
     */
    @Override
    public void update(Category updateCategory){
        // 检查是否有重名的
        if(updateCategory.getName() != null){
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            // 存在不一样的类别, 并且名字相同
            if(categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())){
                throw new ImoocMallException(ImoocMailExceptionEnum.NAME_DUPLICATED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if(count == 0){
            throw new ImoocMallException(ImoocMailExceptionEnum.UPDATE_FAILED);
        }
    }

    // 根据ID删除分类
    @Override
    public void delete(Integer id){
        // 先查找
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        // 查不到删除异常
        if(categoryOld == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new ImoocMallException(ImoocMailExceptionEnum.DELETE_FAILED);
        }
    }

    // 后台分页
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        // 先按照type进行排序, 相同type按照order_num进行排序
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categoryList = categoryMapper.selectList();
        return new PageInfo(categoryList);
    }

    // 前台分页
    @Override
    @Cacheable(value = "listCategoryForCustomer") // 用value指定key值
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList, parentId);
        return categoryVOList;
    }

    // 递归查找子类别, 组合成一个目录树
    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId){
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if(!CollectionUtils.isEmpty(categoryList)){ // 不为空
            for(Category category : categoryList){
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                // 递归地添加
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
