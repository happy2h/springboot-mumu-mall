package com.imooc.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateCategoryReq {
    @NotNull(message = "id不为空")
    private Integer id;
    @Size(min = 2, max = 5) // 目录长度在2到5之间
    private String name;
    @Max(3) // 最大的层级数
    private Integer type;
    private Integer parentId;
    private Integer orderNum;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "UpdateCategoryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }
}
