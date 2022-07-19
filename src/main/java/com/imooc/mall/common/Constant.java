package com.imooc.mall.common;

import com.google.common.collect.Sets;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * 常量值
 */
@Component
public class Constant {
    public static final String SALT = "dcjsioafhseikfhlsifvds";
    public static final String IMOOC_MALL_USER = "imooc_mall_user";

    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy{
        HashSet<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public interface SaleStatus{
        int NOT_SALE = 0; // 商品下架
        int SALE = 1; // 在售
    }

    public interface Cart{
        int UN_CHECKED = 0; // 未选中
        int CHECKED = 1; // 选中
    }

    public enum OrderStatusEnum{
        CANCELED("用户已取消", 0),
        NOT_PAY("未付款", 10),
        PAID("已付款", 20),
        DELIVERED("已发货", 30),
        FINISHED("交易完成", 40);

        private String value;
        private int code;

        OrderStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(code == orderStatusEnum.getCode()){
                    return orderStatusEnum;
                }
            }
            throw new ImoocMallException(ImoocMailExceptionEnum.NO_ENUM);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
