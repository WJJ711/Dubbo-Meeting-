package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;

/**
 * @author wjj
 * @version 1.0
 * @date 2019/11/1 9:48
 */
public interface AliPayServiceAPI {

    AliPayInfoVO getQRCode(String orderId,String tempPath);

    AliPayResultVO getOrderStatus(String orderId);
}
