package com.stylefeng.guns.api.cinema;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.vo.*;

import java.util.List;

public interface CinemaServiceAPI {
    //1根据CinemaQueryVO,查询影院列表
    Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO);

    //2根据条件获取品牌列表
    List<BrandVO> getBrandList(int brandId);

    //3获取行政区域列表
    List<AreaVO> getAreaList(int areaId);

    //4获取影院类型列表
    List<HallTypeVO> getHallTypeList(int hallType);

    //5根据影院编号获取影院信息
    CinemaInfoVO getCinemaInfoById(int cinemaId);

    //6获取所有电影的信息和对应的放映场次信息，根据影院编号
    List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId);

    //7根据放映场次ID获取放映信息
    HallInfoVO getFilmFiledInfo(int fieldId);

    //8根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    FilmInfoVO getFilmInfoByFieldId(int fieldId);

    /*
    该部分是订单模块需要的部分
     */
    OrderQueryVO getOrderNeeds(int fieldId);
}
