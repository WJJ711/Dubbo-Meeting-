package com.stylefeng.guns.rest.modular.cinema.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
@Service(executes = 10)
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {

    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;
    @Autowired
    private MoocFieldTMapper moocFieldTMapper;
    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;

    //1根据CinemaQueryVO,查询影院列表
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {
        //业务实体
        List<CinemaVO> cinemaVOList=new ArrayList<>();
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());
        //判断是否传入查询条件，->brandId,distId,hallType是否==99
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if (cinemaQueryVO.getBrandId()!=99){
            entityWrapper.eq("brand_id",cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId()!=99){
            entityWrapper.eq("area_id",cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType()!=99){//%#3#%
            entityWrapper.like("hall_ids","%"+cinemaQueryVO.getHallType()+"%");
        }
        //将数据实体转换为业务实体
        List<MoocCinemaT> moocCinemaTList = moocCinemaTMapper.selectPage(page, entityWrapper);
        for (MoocCinemaT moocCinemaT : moocCinemaTList) {
            CinemaVO cinemaVO = new CinemaVO();

            cinemaVO.setUuid(moocCinemaT.getUuid()+"");
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice()+"");
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());

            cinemaVOList.add(cinemaVO);
        }
        //根据条件，判断影院列表总数
        Integer counts = moocCinemaTMapper.selectCount(entityWrapper);

        Page<CinemaVO> result=new Page<>();
        result.setRecords(cinemaVOList);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);
        return result;
    }


    //2根据条件获取品牌列表
    @Override
    public List<BrandVO> getBrandList(int brandId) {
        boolean flag=false;
        List<BrandVO> brandVOList=new ArrayList<>();
        //判断brandId是否存在,不存在则为true
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
        if (brandId==99||moocBrandDictT==null||moocBrandDictT.getUuid()==null){
            flag=true;
        }
        //查询所有列表
        List<MoocBrandDictT> moocBrandDictTList = moocBrandDictTMapper.selectList(null);
        //如果flag为true，则将99设为Active
        for (MoocBrandDictT brand : moocBrandDictTList) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid()+"");
            //如果flag为true，则需要99，如果为flase，则匹配上的内容为active
            if (flag){
                if (brand.getUuid()==99){
                    brandVO.setActive(true);
                }
            }else {
                if (brand.getUuid()==brandId){
                    brandVO.setActive(true);
                }
            }
            brandVOList.add(brandVO);
        }
        return brandVOList;
    }

    //3获取行政区域列表
    @Override
    public List<AreaVO> getAreaList(int areaId) {
        boolean flag=false;
        List<AreaVO> areaVOList=new ArrayList<>();
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        if (areaId==99||moocAreaDictT==null||moocAreaDictT.getUuid()==null){
            flag=true;
        }
        List<MoocAreaDictT> moocAreaDictTList = moocAreaDictTMapper.selectList(null);
        //如果flag为true，则将99设为Active
        for (MoocAreaDictT area : moocAreaDictTList) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid()+"");
            //如果flag为true，则需要99，如果为flase，则匹配上的内容为active
            if (flag){
                if (area.getUuid()==99){
                    areaVO.setActive(true);
                }
            }else {
                if (area.getUuid()==areaId){
                    areaVO.setActive(true);
                }
            }
            areaVOList.add(areaVO);
        }
        return areaVOList;
    }

    //4获取影院类型列表
    @Override
    public List<HallTypeVO> getHallTypeList(int hallType) {
        boolean flag=false;
        List<HallTypeVO> hallTypeVOList=new ArrayList<>();
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        if (hallType==99||moocHallDictT==null||moocHallDictT.getUuid()==null){
            flag=true;
        }
        List<MoocHallDictT> moocHallDictTList = moocHallDictTMapper.selectList(null);
        //如果flag为true，则将99设为Active
        for (MoocHallDictT hall : moocHallDictTList) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeName(hall.getShowName());
            hallTypeVO.setHalltypeId(hall.getUuid()+"");
            //如果flag为true，则需要99，如果为flase，则匹配上的内容为active
            if (flag){
                if (hall.getUuid()==99){
                    hallTypeVO.setActive(true);
                }
            }else {
                if (hall.getUuid()==hallType){
                    hallTypeVO.setActive(true);
                }
            }
            hallTypeVOList.add(hallTypeVO);
        }
        return hallTypeVOList;
    }


    //5根据影院编号获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid()+"");
        cinemaInfoVO.setCinemaAdress(moocCinemaT.getCinemaAddress());
        return cinemaInfoVO;
    }

    //6获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfoList = moocFieldTMapper.getFilmInfoList(cinemaId);
        return filmInfoList;
    }

    //7根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFiledInfo(int fieldId) {

        return moocFieldTMapper.getHallInfo(fieldId);
    }

    //8根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {

        return moocFieldTMapper.getFilmInfoById(fieldId);
    }

    @Override
    public OrderQueryVO getOrderNeeds(int fieldId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);

        orderQueryVO.setCinemaId(moocFieldT.getCinemaId()+"");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice()+"");
        return orderQueryVO;
    }
}












