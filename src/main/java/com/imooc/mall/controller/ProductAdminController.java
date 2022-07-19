package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 后台商品管理控制器
 */
@RestController
public class ProductAdminController {
    @Autowired
    private ProductService productService;

    @ApiOperation("添加商品")
    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("图片上传")
    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        String newFileName = productService.getNewFileName(file);
        // 文件夹
        File fileDir = new File(Constant.FILE_UPLOAD_DIR);
        // 文件
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if(!fileDir.exists()){
            if(!fileDir.mkdir()){
                throw new ImoocMallException(ImoocMailExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImoocMallException(ImoocMailExceptionEnum.UPLOAD_FAILED);
        }
        try {
            return ApiRestResponse.success(getHost(new URI(request.getRequestURL() +"")) + "/static/images/" + newFileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new ImoocMallException(ImoocMailExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            // 裁剪
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            effectiveURI = null;
        }
        return effectiveURI;
    }


    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@RequestBody @Valid UpdateProductReq updateProductReq){
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam("id") Integer id){
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam("ids") Integer[] ids, @RequestParam("sellStatus") Integer sellStatus){
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表接口")
    @GetMapping ("/admin/product/list")
    public ApiRestResponse list(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
}
