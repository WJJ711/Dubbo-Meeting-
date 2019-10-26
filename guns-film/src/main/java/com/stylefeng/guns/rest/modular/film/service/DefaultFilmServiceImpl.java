package com.stylefeng.guns.rest.modular.film.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;

    @Autowired
    private MoocFilmTMapper moocFilmTMapper;

    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;

    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;

    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;
    @Override
    public List<BannerVO> getBanners() {
        List<BannerVO> result=new ArrayList<>();
        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);
        for (MoocBannerT moocBanner : moocBanners) {
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerAddress(moocBanner.getBannerAddress());
            bannerVO.setBannerId(moocBanner.getUuid()+"");
            bannerVO.setBannerUrl(moocBanner.getBannerUrl());
            result.add(bannerVO);
        }
        return result;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilms){
        List<FilmInfo> filmInfos=new ArrayList<>();
        for (MoocFilmT moocFilm : moocFilms) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setShowTime(DateUtil.getTime(moocFilm.getFilmTime()));
            filmInfo.setScore(moocFilm.getFilmScore());
            filmInfo.setImgAddress(moocFilm.getImgAddress());
            filmInfo.setFilmType(moocFilm.getFilmType());
            filmInfo.setFilmScore(moocFilm.getFilmScore());
            filmInfo.setFilmName(moocFilm.getFilmName());
            filmInfo.setFilmId(moocFilm.getUuid()+"");
            filmInfo.setExpectNum(moocFilm.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilm.getFilmBoxOffice());
            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int num) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList = new ArrayList<>();
        //热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");
        //判断是否是首页需要的内容
        if (isLimit){
            //如果是，则限制条数，限制内容为热映影片
            //当前页和每页需要的条数
            Page<MoocFilmT> page = new Page<>(1, num);
            List<MoocFilmT> moocFilmList = moocFilmTMapper.selectPage(page, entityWrapper);
            //组织filmInfos
            filmInfoList=getFilmInfos(moocFilmList);
            filmVO.setFilmInfoList(filmInfoList);
            filmVO.setFilmNum(moocFilmList.size());
        }else {
            //如果不是，则是列表页，同样需要限制内容为热映影片
        }
        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int num) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList = new ArrayList<>();
        //即将上映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","2");
        //判断是否是首页需要的内容
        if (isLimit){
            //如果是，则限制条数，限制内容为即将上映影片
            //当前页和每页需要的条数
            Page<MoocFilmT> page = new Page<>(1, num);
            List<MoocFilmT> moocFilmList = moocFilmTMapper.selectPage(page, entityWrapper);
            //组织filmInfos
            filmInfoList=getFilmInfos(moocFilmList);
            filmVO.setFilmInfoList(filmInfoList);
            filmVO.setFilmNum(moocFilmList.size());
        }else {
            //如果不是，则是列表页，同样需要限制内容为即将上映影片
        }
        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        //条件->正在上映的，票房前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");
        Page<MoocFilmT> page = new Page<>(1, 10, "film_box_office");
        List<MoocFilmT> moocFilmTList = moocFilmTMapper.selectPage(page,entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilmTList);
        return filmInfos;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        //条件->即将上映的，预售前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","2");
        Page<MoocFilmT> page = new Page<>(1, 10, "film_preSaleNum");
        List<MoocFilmT> moocFilmTList = moocFilmTMapper.selectPage(page,entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilmTList);
        return filmInfos;
    }

    @Override
    public List<FilmInfo> getTop() {
        //条件->正在上映的，评分前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");
        Page<MoocFilmT> page = new Page<>(1, 10, "film_score");
        List<MoocFilmT> moocFilmTList = moocFilmTMapper.selectPage(page,entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilmTList);
        return filmInfos;
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> catVOList=new ArrayList<>();
        //查询实体对象->MoocCatDictT
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);
        //将实体对象转换为业务对象->CatVO
        for (MoocCatDictT moocCatDictT : moocCats) {
            CatVO catVO = new CatVO();
            catVO.setCatId(moocCatDictT.getUuid()+"");
            catVO.setCatName(moocCatDictT.getShowName());
            catVOList.add(catVO);
        }
        return catVOList;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sourceVOList=new ArrayList<>();
        List<MoocSourceDictT> moocSourceDictTList = moocSourceDictTMapper.selectList(null);
        for (MoocSourceDictT moocSourceDictT : moocSourceDictTList) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceId(moocSourceDictT.getUuid()+"");
            sourceVO.setSourceName(moocSourceDictT.getShowName());

            sourceVOList.add(sourceVO);
        }
        return sourceVOList;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> yearVOList = new ArrayList<>();
        List<MoocYearDictT> moocYearDictTList = moocYearDictTMapper.selectList(null);
        for (MoocYearDictT moocYearDictT : moocYearDictTList) {
            YearVO yearVO = new YearVO();
            yearVO.setYearId(moocYearDictT.getUuid()+"");
            yearVO.setYearName(moocYearDictT.getShowName());
            yearVOList.add(yearVO);
        }
        return yearVOList;
    }
}
