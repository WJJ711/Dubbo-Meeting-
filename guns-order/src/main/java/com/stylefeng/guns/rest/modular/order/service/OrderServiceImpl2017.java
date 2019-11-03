package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2017TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@Service(group = "order2017")
@Slf4j
public class OrderServiceImpl2017 implements OrderServiceAPI {

    @Autowired
    private MoocOrder2017TMapper moocOrder2017TMapper;

    @Reference(check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FTPUtil ftpUtil;
    //验证是否为真实的座位编号
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        String seatPath = moocOrder2017TMapper.getSeatPathByFieldId(fieldId);

        //读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        //将fileStrByAddress转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        String ids = jsonObject.get("ids").toString();

        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue=0;
        for (String id : idArrs) {
            for (String seat : seatArrs) {
                if (StringUtils.equalsIgnoreCase(id,seat)){
                    //匹配上的个数+1
                    isTrue++;
                }
            }
        }
        return isTrue==seatArrs.length;
    }

    //判断是否为已售座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper<MoocOrder2017T> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("field_id",fieldId);
        List<MoocOrder2017T> MoocOrder2017TList = moocOrder2017TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");
        for (MoocOrder2017T MoocOrder2017T : MoocOrder2017TList) {
            String[] ids = MoocOrder2017T.getSeatsIds().split(",");
            for (String id : ids) {
                for (String seat : seatArrs) {
                    if (StringUtils.equalsIgnoreCase(id,seat)){
                        //如果有一个匹配上则为false
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //创建新订单
    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        //订单编号
        String uuid = UUIDUtil.getUuid();
        //影片信息
        FilmInfoVO filmInfoVO = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        int filmId = Integer.parseInt(filmInfoVO.getFilmId());

        //获取影院信息
        OrderQueryVO orderQueryVO = cinemaServiceAPI.getOrderNeeds(fieldId);

        //获取订单总金额
        int cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrder2017T MoocOrder2017T = new MoocOrder2017T();
        MoocOrder2017T.setSeatsName(seatsName);
        MoocOrder2017T.setSeatsIds(soldSeats);
        MoocOrder2017T.setOrderUser(userId);
        MoocOrder2017T.setOrderPrice(totalPrice);
        MoocOrder2017T.setFilmPrice(filmPrice);
        MoocOrder2017T.setFilmId(filmId);
        MoocOrder2017T.setFieldId(fieldId);
        MoocOrder2017T.setCinemaId(cinemaId);
        MoocOrder2017T.setUuid(uuid);

        Integer insert = moocOrder2017TMapper.insert(MoocOrder2017T);
        if (insert>0){
            //返回查询结果
            OrderVO orderVO = moocOrder2017TMapper.getOrderInfoById(uuid);
            if (orderVO==null||orderVO.getOrderId()==null){
                log.error("订单信息查询失败,订单编号为{}",uuid);
                return null;
            }else {
                return orderVO;
            }
        }else {
            log.error("订单插入失败,订单编号为{}",uuid);
            return null;
        }
    }

    private static double getTotalPrice(int solds,double filmPrice){
        BigDecimal soldNum = new BigDecimal(Integer.toString(solds));
        BigDecimal filmPriceDeci = new BigDecimal(Double.toString(filmPrice));
        BigDecimal result = soldNum.multiply(filmPriceDeci);
        //四舍五入，取小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();

    }

    public static void main(String[] args) {
        System.out.println(getTotalPrice(2,13.2222));
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        if (userId==null){
            log.error("订单查询业务失败,用户编号未传入");
            return null;
        }else {
            List<OrderVO> orderVOList = moocOrder2017TMapper.getOrderInfoByUserId(userId,page);
            if (orderVOList==null||orderVOList.size()==0){
                page.setTotal(0);
                page.setRecords(new ArrayList<>());
                return page;
            }else {
                EntityWrapper<MoocOrder2017T> entityWrapper=new EntityWrapper<>();
                entityWrapper.eq("order_user",userId);
                Integer count = moocOrder2017TMapper.selectCount(entityWrapper);
                page.setTotal(count);
                page.setRecords(orderVOList);
                return page;
            }
        }
    }

    /*
    根据放映查询，获得所有的已售座位
     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId==null){
            log.error("查询已售座位错误,未传入任何场次编号");
            return "";
        }else {

            return moocOrder2017TMapper.getSoldSeatsByFieldId(fieldId);
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {
        OrderVO orderInfoById = moocOrder2017TMapper.getOrderInfoById(orderId);
        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {
        MoocOrder2017T moocOrder2017T= new MoocOrder2017T();
        moocOrder2017T.setUuid(orderId);
        moocOrder2017T.setOrderStatus(1);

        Integer integer = moocOrder2017TMapper.updateById(moocOrder2017T);
        if (integer>=1){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrder2017T moocOrder2017T= new MoocOrder2017T();
        moocOrder2017T.setUuid(orderId);
        moocOrder2017T.setOrderStatus(2);

        Integer integer = moocOrder2017TMapper.updateById(moocOrder2017T);
        if (integer>=1){
            return true;
        }else {
            return false;
        }
    }
}
