package com.imooc.mall.exception;

/**
 * 异常枚举类
 */
public enum ImoocMailExceptionEnum {
    Need_User_Name(10001, "用户名不为空"),
    Need_PASSWORD(10002, "密码不为空"),
    PASSWORD_TOO_SHORT(10003, "密码长度不能小于8位"),
    NAME_DUPLICATED(10004, "不允许重名"),
    INSERT_FAILED(10005, "插入失败, 请重试"),
    WRONG_PASSWORD(10006, "输入密码错误"),
    NEED_LOGIN(10007, "用户未登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009, "无管理员权限"),
    PARAM_NOT_NULL(10010, "参数不能为空"),
    CREATED_FAILED(10011, "新增失败"),
    REQUEST_PARAM_ERROR(10012, "参数错误"),
    DELETE_FAILED(10013, "删除失败"),
    MKDIR_FAILED(10013, "文件夹创建失败"),
    UPLOAD_FAILED(10013, "上传失败"),
    NOT_SALE(10014, "商品状态异常"),
    NOT_ENOUGH(10015, "商品库存不足"),
    CART_EMPTY(10016, "购物车勾选商品为空"),
    NO_ENUM(10017, "未找到对应的枚举类"),
    TEST_ERROR(10018, "测试模拟异常"),
    NO_ORDER(10019, "订单不存在"),
    NOT_YOUR_ORDER(10020, "订单归属错误"),
    WRONG_ORDER_STATUS(10021, "订单状态不符"),
    GENERATE_QRCODE_FAILED(10022, "生成二维码失败"),
    SYSTEM_ERROR(20000, "系统错误");
    /**
     * 异常码
     */
    Integer code;

    /**
     * 异常信息
     */
    String msg;

    ImoocMailExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
