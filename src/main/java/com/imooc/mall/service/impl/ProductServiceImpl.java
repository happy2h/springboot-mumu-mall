package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductListQuery;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 商品服务实现类
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryService categoryService;

    /**
     * 插入一条商品信息
     * @param addProductReq
     */
    @Override
    public void add(AddProductReq addProductReq){
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        // 插入之前判断是否有重名的
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if(productOld != null){
            throw new ImoocMallException(ImoocMailExceptionEnum.NAME_DUPLICATED);
        }
        int count = productMapper.insertSelective(product);
        if(count == 0){
            throw new ImoocMallException(ImoocMailExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 根据传入的file得包含uuid的新的文件名
     * @param file
     * @return
     */
    @Override
    public String getNewFileName(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        return uuid.toString() + suffixName;
    }

    /**
     * 更新商品
     * @param product
     */
    @Override
    public void update(Product product){
        Product productOld = productMapper.selectByName(product.getName());
        // 同名且不同id不能进行修改
        if(productOld != null && !product.getId().equals(productOld.getId())){
            throw new ImoocMallException(ImoocMailExceptionEnum.NAME_DUPLICATED);
        }
        int count = productMapper.updateByPrimaryKeySelective(product);
        if(count == 0){
            throw new ImoocMallException(ImoocMailExceptionEnum.UPLOAD_FAILED);
        }
    }

    /**
     * 删除商品
     * @param id
     */
    @Override
    public void delete(Integer id){
        Product productOld = productMapper.selectByPrimaryKey(id);
        // 查不到记录无法删除
        if(productOld == null){
            throw new ImoocMallException(ImoocMailExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new ImoocMallException(ImoocMailExceptionEnum.DELETE_FAILED);
        }
    }


    /**
     * 批量上下架
     * @param ids
     * @param sellStatus
     */
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus){
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    // 后台商品列表
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        return new PageInfo(products);
    }

    // 根据id查询商品
    @Override
    public Product detail(Integer id){
        return productMapper.selectByPrimaryKey(id);
    }

    // 前台展示商品列表
    @Override
    public PageInfo list(ProductListReq productListReq){
        // 构建query对象
        ProductListQuery productListQuery = new ProductListQuery();
        // 搜索处理
        if(!StringUtils.isEmpty(productListReq.getKeyword())){
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        // 目录处理,如果查某个目录下的商品,不仅需要查询该目录下的,还要把所有子目录的所有商品都查出来,所以需要拿到一个目录id的list
        if(productListReq.getCategoryId() != null){
            List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOS, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        // 排序处理
        String orderBy = productListReq.getOrderBy();
        if(Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(productListQuery);
        return new PageInfo(productList);
    }

    private void getCategoryIds(List<CategoryVO> categoryVOS, List<Integer> categoryIds){
        for(CategoryVO categoryVO : categoryVOS){
            if(categoryVO != null){
                categoryIds.add(categoryVO.getId());
                // 递归地添加
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }
}
