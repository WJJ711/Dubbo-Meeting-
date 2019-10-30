package com.stylefeng.guns.rest.modular.film;

import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE="http://img.wjjlucky.top/";
    @Reference(check = false)
    private FilmServiceApi filmServiceApi;

    @Reference(check = false,async = true)
    private FilmAsyncServiceApi filmAsyncServiceApi;
    /*
        API网关
        1.功能聚合【API网关】
        好处：
            1.六个接口，一次请求，同一时刻节省了5次HTTP请求
            2.同一个接口对外暴露，降低了前后端分离开发的难度和复杂度
         坏处
            1.一次获取数据过多，容易出现问题
     */
    @RequestMapping(value = "getIndex",method = RequestMethod.GET)
    public ResponseVo<FilmIndexVO> getIndex(){
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        //获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        //获取正在热映的电影
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,99,99,99,99));
        //即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,99,99,99,99));
        //票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        //获取受欢迎榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        //获取前100
        filmIndexVO.setTop100(filmServiceApi.getTop());
        return ResponseVo.success(IMG_PRE,filmIndexVO);
    }

    @RequestMapping(value ="getConditionList",method = RequestMethod.GET)
    public ResponseVo getConditionList(@RequestParam(name="catId",required = false,defaultValue = "99")String catId,
                                       @RequestParam(name="sourceId",required = false,defaultValue = "99")String sourceId,
                                       @RequestParam(name="yearId",required = false,defaultValue = "99")String yearId){

        //如果传进来的参数无效，或者为99，则全部active

        //类型集合
        List<CatVO> catVOList = filmServiceApi.getCats();
        List<CatVO> catResult=new ArrayList<>();
        CatVO catTemp=null;
        boolean flag=false;
        for (CatVO catVO : catVOList) {
            //判断集合是否存在catId，如果存在，则将对应的实体变成active状态
            //1,2,3,99,4,5
            //必须要把99找到
            if (StringUtils.equals(catVO.getCatId(),"99")){
                //99代表全部
                //如果没有这个判断，则如果传入一个无效的catId，则无法将99设为active
                catTemp=catVO;
                //如果没有continue，则当catid无效时，99可能被传入2次
                continue;
            }
            //如果不是99，则在这里
            if (StringUtils.equals(catVO.getCatId(),catId)){
                flag=true;
                catVO.setActive(true);
            }else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }

        //如果传进来的参数无效，或者为99
        if (!flag){
            //不存在，则将"99"设置为Active状态
            //一定有99这个选项
            catTemp.setActive(true);
            catResult.add(catTemp);
        }else {
            catTemp.setActive(false);
            catResult.add(catTemp);
        }

        //片源集合
        List<SourceVO> sourceVOList = filmServiceApi.getSources();
        List<SourceVO> sourceResult=new ArrayList<>();
        SourceVO sourceTemp=null;
        flag=false;
        for (SourceVO sourceVO : sourceVOList) {
            if (StringUtils.equals(sourceVO.getSourceId(),"99")){
                sourceTemp=sourceVO;
                continue;
            }
            if (StringUtils.equals(sourceVO.getSourceId(),sourceId)){
                flag=true;
                sourceVO.setActive(true);
            }else {
                sourceVO.setActive(false);
            }
            sourceResult.add(sourceVO);
        }
        if (!flag){
            sourceTemp.setActive(true);
            sourceResult.add(sourceTemp);
        }else {
            sourceTemp.setActive(false);
            sourceResult.add(sourceTemp);
        }

        //年代集合

        List<YearVO> yearVOList = filmServiceApi.getYears();
        List<YearVO> yearResult=new ArrayList<>();
        YearVO yearTemp=null;
        flag=false;
        for (YearVO yearVO : yearVOList) {
            if (StringUtils.equals(yearVO.getYearId(),"99")){
                yearTemp=yearVO;
                continue;
            }
            if (StringUtils.equals(yearVO.getYearId(),yearId)){
                flag=true;
                yearVO.setActive(true);
            }else {
                yearVO.setActive(false);
            }
            yearResult.add(yearVO);
        }
        if (!flag){
            yearTemp.setActive(true);
            yearResult.add(yearTemp);
        }else {
            yearTemp.setActive(false);
            yearResult.add(yearTemp);
        }

        FilmConditionVO filmConditionVO = new FilmConditionVO();
        filmConditionVO.setCatVOList(catResult);
        filmConditionVO.setSourceVOList(sourceResult);
        filmConditionVO.setYearVOList(yearResult);


        return ResponseVo.success(filmConditionVO);
    }

    @RequestMapping(value = "getFilms",method = RequestMethod.GET)
    public ResponseVo getFilms(FilmRequestVO filmRequestVO){

        String img_pre="http://img.wjjlucky.top/";
        FilmVO filmVO=null;
        //根据showType判断影片查询类型
        switch (filmRequestVO.getShowType()){
            case 1:
                filmVO=filmServiceApi.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            case 2:
                filmVO=filmServiceApi.getSoonFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            case 3:
                filmVO=filmServiceApi.getClassicFilms(filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            default:
                filmVO=filmServiceApi.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
        }
        //根据sortId排序
        //添加各种条件查询
        //判断当前是第几页

        return ResponseVo.success(filmVO.getNowPage(),filmVO.getTotalPage(),img_pre,filmVO.getFilmInfoList());
    }

    @RequestMapping(value = "films/{searchParam}",method = RequestMethod.GET)
    public ResponseVo films(@PathVariable("searchParam")String searchParam,
                            @RequestParam("searchType") int searchType ) throws ExecutionException, InterruptedException {

        //根据searchType，判断查询类型
        FilmDetailVO filmDetail = filmServiceApi.getFilmDetail(searchType, searchParam);
        if (filmDetail==null||filmDetail.getFilmId()==null||filmDetail.getFilmId().trim().length()==0){
            return ResponseVo.serviceFail("没有可查询的影片");
        }
        String filmId = filmDetail.getFilmId();
        //查询影片的详细信息->Dubbo的异步获取
        //获取影片描述信息
       // FilmDescVO filmDesc = filmAsyncServiceApi.getFilmDesc(filmId);
        filmAsyncServiceApi.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();
        //获取图片信息
       // ImgVO imgVO = filmAsyncServiceApi.getImgs(filmId);
        filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVO> ImgVOFuture = RpcContext.getContext().getFuture();
        //获取导演信息
        //ActorVO directorVO = filmAsyncServiceApi.getDecInfo(filmId);
        filmAsyncServiceApi.getDecInfo(filmId);
        Future<ActorVO> directorVOFuture = RpcContext.getContext().getFuture();
        //获取演员信息
        //List<ActorVO> actorVOList = filmAsyncServiceApi.getActors(filmId);
        filmAsyncServiceApi.getActors(filmId);
        Future<List<ActorVO>> actorVOFuture = RpcContext.getContext().getFuture();

        //组织Actor属性
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setActorVOList(actorVOFuture.get());
        actorRequestVO.setDirector(directorVOFuture.get());

        //组织info对象
        InfoRequestVO infoRequestVO = new InfoRequestVO();
        infoRequestVO.setActorRequestVO(actorRequestVO);
        infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());
        infoRequestVO.setFilmId(filmId);
        infoRequestVO.setImgVO(ImgVOFuture.get());

        //组织成返回值
        filmDetail.setInfo04(infoRequestVO);

        return ResponseVo.success(IMG_PRE,filmDetail);
    }

}
