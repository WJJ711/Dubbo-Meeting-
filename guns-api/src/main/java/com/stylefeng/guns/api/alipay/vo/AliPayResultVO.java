package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

/**
 * @author wjj
 * @version 1.0
 * @date 2019/11/1 10:01
 */
@Data
public class AliPayResultVO {
    private String orderId;
    private Integer orderStatus;
    private String orderMsg;
}
