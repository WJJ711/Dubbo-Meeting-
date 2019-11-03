package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wjj
 * @version 1.0
 * @date 2019/11/1 9:58
 */

@Data
public class AliPayInfoVO implements Serializable{
    private String orderId;
    private String QRCodeAddress;
}
