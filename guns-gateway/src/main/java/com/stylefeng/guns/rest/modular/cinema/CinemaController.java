package com.stylefeng.guns.rest.modular.cinema;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaListResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {
    private static final String IMG_PRE="http://img.wjjlucky.top";
    @Reference(check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @RequestMapping("getCinemas")
    public ResponseVo getCinema(CinemaQueryVO cinemaQueryVO){
        //按照五个条件进行筛选
        try {
            Page<CinemaVO> cinemas = cinemaServiceAPI.getCinemas(cinemaQueryVO);
            if (cinemas==null||cinemas.getRecords()==null||cinemas.getRecords().size()==0){
                return ResponseVo.success("没有影院可查");
            }else {
                CinemaListResponseVO cinemaListResponseVO = new CinemaListResponseVO();
                cinemaListResponseVO.setCinemas(cinemas.getRecords());
                return ResponseVo.success(cinemas.getCurrent(),(int)cinemas.getPages(),"",cinemaListResponseVO);
            }
        } catch (Exception e) {
            //如何处理异常
            log.error("获取影院列表异常",e);
            return ResponseVo.appFail("查询影院列表失败");
        }
    }

    @RequestMapping("getCondition")
    public ResponseVo getCondition(CinemaQueryVO cinemaQueryVO){
        try {
            List<BrandVO> brandList = cinemaServiceAPI.getBrandList(cinemaQueryVO.getBrandId());
            List<AreaVO> areaList = cinemaServiceAPI.getAreaList(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypeList = cinemaServiceAPI.getHallTypeList(cinemaQueryVO.getHallType());
            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setAreaList(areaList);
            cinemaConditionResponseVO.setBrandList(brandList);
            cinemaConditionResponseVO.setHalltypeList(hallTypeList);
            return ResponseVo.success(cinemaConditionResponseVO);
        } catch (Exception e) {
            log.error("获取条件列表失败",e);
           return ResponseVo.appFail("获取条件列表失败");
        }
    }

    @RequestMapping("getFields")
    public ResponseVo getFields(Integer cinemaId){
        try {
            CinemaInfoVO cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);
            List<FilmInfoVO> filmInfoByCinemaId = cinemaServiceAPI.getFilmInfoByCinemaId(cinemaId);
            CinemaFieldResponseVO cinemaFieldResponseVO = new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfoVO(cinemaInfoById);
            cinemaFieldResponseVO.setFilmList(filmInfoByCinemaId);
            return ResponseVo.success(IMG_PRE,cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取播放场次失败",e);
            return ResponseVo.appFail("获取播放场次失败");
        }
    }


    @RequestMapping(value = "getFieldInfo",method = RequestMethod.POST)
    public ResponseVo getFieldInfo(Integer cinemaId,Integer fieldId){

        return null;
    }

}
