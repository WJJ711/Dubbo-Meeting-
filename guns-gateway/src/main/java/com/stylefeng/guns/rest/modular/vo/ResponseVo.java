package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;

@Data
public class ResponseVo<T> {
    //返回状态【0-成功,1-业务失败,999-系统异常】
    private int status;

    //返回信息
    private String msg;

    //返回数据实体
    private T data;

    private String imgPre;

    private ResponseVo(){
    }

    public static <T> ResponseVo success(String imgPre,T data){
        ResponseVo responseVo=new ResponseVo();
        responseVo.setStatus(0);
        responseVo.setData(data);
        responseVo.setImgPre(imgPre);
        return responseVo;
    }
    public static <T> ResponseVo success(T data){
        ResponseVo responseVo=new ResponseVo();
        responseVo.setStatus(0);
        responseVo.setData(data);
        return responseVo;
    }

    public static <T> ResponseVo success(String msg){
        ResponseVo responseVo=new ResponseVo();
        responseVo.setStatus(0);
        responseVo.setMsg(msg);
        return responseVo;
    }

    public static <T> ResponseVo serviceFail(String msg){
        ResponseVo responseVo=new ResponseVo();
        responseVo.setStatus(1);
        responseVo.setData(msg);
        return responseVo;
    }

    public static <T> ResponseVo appFail(String msg){
        ResponseVo responseVo=new ResponseVo();
        responseVo.setStatus(999);
        responseVo.setData(msg);
        return responseVo;
    }

}
