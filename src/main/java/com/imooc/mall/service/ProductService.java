package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品service
 */
public interface ProductService {
    void add(AddProductReq addProductReq);

    String getNewFileName(MultipartFile file);

    void update(Product product);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);


    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
