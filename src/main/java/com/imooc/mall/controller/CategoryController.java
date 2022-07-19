package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.UpdateCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 目录controller
 */
@RestController
public class CategoryController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 管理员用户添加新的分类
     * @param session
     * @param addCategoryReq
     * @return
     */
    @ApiOperation("管理员用户添加新的分类")
    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq){
        // 参数校验 - 使用注解
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        // 当前用户是否登录
        if(currentUser == null){
            return ApiRestResponse.error(ImoocMailExceptionEnum.NEED_LOGIN);
        }
        boolean isAdmin = userService.checkAdminRole(currentUser);
        // 校验是否是管理员
        if(isAdmin){
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMailExceptionEnum.NEED_ADMIN);
        }
    }

    /**
     * 更新目录
     * @return
     */
    @ApiOperation("更新目录")
    @PostMapping("/admin/category/update")
    public ApiRestResponse updateCategory(HttpSession session, @Valid @RequestBody UpdateCategoryReq updateCategoryReq){
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        // 当前用户是否登录
        if(currentUser == null){
            return ApiRestResponse.error(ImoocMailExceptionEnum.NEED_LOGIN);
        }
        boolean isAdmin = userService.checkAdminRole(currentUser);
        // 校验是否是管理员
        if(isAdmin){ // 更新目录
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMailExceptionEnum.NEED_ADMIN);
        }
    }

    @PostMapping("/admin/category/delete")
    @ApiOperation("后台删除分类")
    public ApiRestResponse deleteCategory(@RequestParam("id") Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    // 后台分类页表请求
    @ApiOperation("后台分类页表请求")
    @GetMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSizes") Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台分页请求")
    @GetMapping("/category/list")
    public ApiRestResponse listCategoryForCustom(){
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOS);
    }
}
