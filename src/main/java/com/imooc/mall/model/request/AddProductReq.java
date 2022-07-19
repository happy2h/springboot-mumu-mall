package com.imooc.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AddProductReq {
    @NotNull(message = "商品名称不为空")
    private String name;
    @NotNull(message = "商品分类不为空")
    private Integer categoryId;
    @NotNull(message = "商品价格不为空")
    @Min(value = 1, message = "价格不能小于1")
    private Integer price;
    @NotNull(message = "商品库存不为空")
    @Max(value = 10000, message = "库存不能大于10000")
    private Integer stock;
    private String detail;
    @NotNull(message = "商品图片不为空")
    private String image;
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AddProductReq{" +
                "name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", stock=" + stock +
                ", detail='" + detail + '\'' +
                ", image='" + image + '\'' +
                ", status=" + status +
                '}';
    }
}
