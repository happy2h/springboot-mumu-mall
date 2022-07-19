package com.imooc.mall.model.request;

import java.util.Date;

public class ProductListReq {
    private String Keyword;
    private String orderBy;
    private Integer categoryId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ProductListReq{" +
                "Keyword='" + Keyword + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", categoryId=" + categoryId +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}