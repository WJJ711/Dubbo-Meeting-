package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;

/**
 * @author wjj
 * @version 1.0
 * @date 2019/11/1 21:11
 */
/*
业务降级方法
 */
public class AliPayServiceMock implements AliPayServiceAPI{
    @Override
    public AliPayInfoVO getQRCode(String orderId, String tempPath) {
        return null;
    }

    @Override
    public AliPayResultVO getOrderStatus(String orderId) {
        AliPayResultVO aliPayResultVO = new AliPayResultVO();
        aliPayResultVO.setOrderId(orderId);
        aliPayResultVO.setOrderStatus(0);
        aliPayResultVO.setOrderMsg("尚未支付成功,查询订单人数过多，请稍后再试");
        return aliPayResultVO;
    }
}
