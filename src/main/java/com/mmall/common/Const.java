package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface ProductListOdorby {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Cart {
        /*购物车选中状态*/
        int CHECKED = 1;
        /*购物车未选中状态*/
        int UN_CHECKED = 0;
        /*产品数量失败*/
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        /*产品数量限制成功*/
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    /**
     * @Description: 产品状态码
     * @Param:
     * @return:
     * @Author: BoWei
     * @Date: 2018/3/29
     */
    public enum ProductEnum {
        ON_SALE(1, "在线");
        private String value;
        private int code;

        ProductEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
