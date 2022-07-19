package com.imooc.mall.model.request;

import javax.validation.constraints.NotNull;

public class CreateOrderReq {
    @NotNull(message = "收件人姓名不能为空")
    private String receiverName;
    @NotNull(message = "收件人电话不能为空")
    private String receiverMobile;
    @NotNull(message = "收件人地址不能为空")
    private String receiverAddress;
    private Integer postage = 0;
    private Integer paymentType = 1;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;

    }

    @Override
    public String toString() {
        return "CreateOrderReq{" +
                "receiverName='" + receiverName + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", postage=" + postage +
                ", paymentType=" + paymentType +
                '}';
    }
}
