package com.mmall.util;

import java.math.BigDecimal;

/**
 * @program: mmall
 * @description: 用于价格计算
 * @author: BoWei
 * @create: 2018-04-02 15:21
 **/
public class BigDevimalUtil {
    private BigDevimalUtil() {

    }

    /*加法*/
    public static BigDecimal add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    /*减法*/
    public static BigDecimal sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    /*乘法*/
    public static BigDecimal mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    /*除法*/
    public static BigDecimal div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        /*除法要保留两位小数，四舍五入*/
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
