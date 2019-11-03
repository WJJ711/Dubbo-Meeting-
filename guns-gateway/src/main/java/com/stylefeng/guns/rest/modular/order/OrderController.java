package com.stylefeng.guns.rest.modular.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AliPayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    @Reference(check = false, group = "order2018")
    private OrderServiceAPI orderServiceAPI;

    @Reference(check = false, group = "order2017")
    private OrderServiceAPI orderServiceAPI2017;

    @Reference(check = false,mock ="com.stylefeng.guns.api.alipay.AliPayServiceMock")
    private AliPayServiceAPI aliPayServiceAPI;

    private static TokenBucket tokenBucket = new TokenBucket();
    private static final String IMG_GRE="http://img.wjjlucky.top/";

    //FailBack方法
    public ResponseVo error(Integer fieldId, String soldSeats, String seatsName) {
        return ResponseVo.serviceFail("抱歉,下单的人太多了，请稍后重试");
    }

    //购票
    //限流
    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            //超时时间4s
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            //请求出现10次意外
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
    }, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "1"),
            @HystrixProperty(name = "maxQueueSize", value = "10"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
    })
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVo buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        // int i = 5 / 0;
        //验证售出的票是否为真
        if (tokenBucket.getToken()) {
            boolean isTure = orderServiceAPI.isTrueSeats(fieldId + "", soldSeats);
            //已经销售的座位里，有没有这些座位
            boolean isNotSold = orderServiceAPI.isNotSoldSeats(fieldId + "", soldSeats);

            //创建订单信息,注意获取登录人
            String userId = CurrentUser.getCurrentUser();
            if (userId == null || userId.trim().length() == 0) {
                return ResponseVo.serviceFail("用户未登录");
            }
            if (isTure && isNotSold) {
                OrderVO orderVO = orderServiceAPI.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                if (orderVO == null) {
                    log.error("购票未成功");
                    return ResponseVo.serviceFail("购票业务异常");
                } else {
                    return ResponseVo.success(orderVO);
                }
            } else {
                log.error("购票未成功,订单中的座位编号有问题");
                return ResponseVo.serviceFail("购票业务异常，订单中的座位编号有问题");
            }
        } else {
            return ResponseVo.serviceFail("购票人数过多，请稍后再试");
        }

    }

    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVo getOrderInfo(
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize
    ) {
        //获取当前登录人的信息
        String userId = CurrentUser.getCurrentUser();

        //使用当前登录人获取已经购买的订单
        Page<OrderVO> page = new Page<>(nowPage, pageSize);
        if (userId != null || userId.trim().length() > 0) {
            Page<OrderVO> result = orderServiceAPI.getOrderByUserId(Integer.parseInt(userId), page);
            Page<OrderVO> result2017 = orderServiceAPI2017.getOrderByUserId(Integer.parseInt(userId), page);

            //合并结果
            int totalPages = (int) (result.getPages() + result2017.getPages());

            //2017和2018的订单总数合并
            List<OrderVO> orderVOList = new ArrayList<>();
            orderVOList.addAll(result.getRecords());
            orderVOList.addAll(result2017.getRecords());

            return ResponseVo.success(nowPage, totalPages, "", orderVOList);
        } else {
            return ResponseVo.serviceFail("用户未登录");
        }

    }

    @RequestMapping(value = "getPayInfo",method = RequestMethod.POST)
    public ResponseVo getPayInfo(@RequestParam("orderId") String orderId, HttpServletRequest request){
        //获取当前登录人的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId==null||userId.trim().length()==0){
            return ResponseVo.serviceFail("抱歉，用户未登录");
        }
        //订单二维码返回结果
        String tempPath=request.getServletContext().getRealPath("upload");
        AliPayInfoVO aliPayInfoVO = aliPayServiceAPI.getQRCode(orderId,tempPath);
        return ResponseVo.success(IMG_GRE,aliPayInfoVO);

    }

    @RequestMapping(value = "getPayResult",method = RequestMethod.POST)
    public ResponseVo getPayResult(@RequestParam("orderId") String orderId,
    @RequestParam(value = "tryNums",required = false,defaultValue = "1") Integer tryNums){
        //获取当前登录人的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId==null||userId.trim().length()==0){
            return ResponseVo.serviceFail("抱歉，用户未登录");
        }
        //将当前登录人的信息传递给后端
        RpcContext.getContext().setAttachment("userId",userId);
        if (tryNums>=4){
            return ResponseVo.serviceFail("订单支付失败，请稍后重试");
        }else {
            AliPayResultVO aliPayResultVO = aliPayServiceAPI.getOrderStatus(orderId);
            if(aliPayResultVO==null|| ToolUtil.isEmpty(aliPayResultVO.getOrderId())){
                AliPayResultVO aliPayFailVO = new AliPayResultVO();
                aliPayFailVO.setOrderId(orderId);
                aliPayFailVO.setOrderStatus(0);
                aliPayFailVO.setOrderMsg("支付不成功");
                return ResponseVo.success(aliPayFailVO);
            }
            return ResponseVo.success(aliPayResultVO);
        }

    }

}
