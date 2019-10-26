package com.stylefeng.guns.rest.modular.user;

import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/")
@RestController
public class UserController {


    @Reference(check = false)
    private UserAPI userAPI;

    @RequestMapping(value="register",method = RequestMethod.POST)
    public ResponseVo register(UserModel userModel){
        if (userModel.getUsername()==null||userModel.getUsername().trim().length()==0){
            return ResponseVo.serviceFail("用户名不能为空");
        }

        if (userModel.getPassword()==null||userModel.getPassword().trim().length()==0){
            return ResponseVo.serviceFail("密码不能为空");
        }

        boolean isSuccess = userAPI.register(userModel);
        if (isSuccess){
            return ResponseVo.success("注册成功");
        }else {
            return ResponseVo.serviceFail("注册失败");
        }
    }

    @RequestMapping(value="check",method = RequestMethod.POST)
    public ResponseVo check(String username){
        if (username!=null&&username.trim().length()>0){
            boolean notExists = userAPI.checkUsername(username);
            //返回true表示用户名可用
            if (notExists){
                return ResponseVo.success("用户名不存在");
            }else {
                return ResponseVo.serviceFail("用户名已存在");
            }
        }
        return ResponseVo.serviceFail("用户名不能为空");
    }

    @RequestMapping(value="logout",method = RequestMethod.GET)
    public ResponseVo logout (){
        /*
         *应用：
         * 1.前端JWT【七天】:JWT的刷新
         * 2.服务器会存储活动用户信息【30min】
         * 3.JWT里的userId为key，查阅活跃用户
         * 退出：
         * 1.前端删除掉JWT
         * 2.后端服务器删除活跃用户缓存
         * 现状:
         * 1.前端删除掉JWT
         */
        return ResponseVo.success("用户退出成功");
    }

    @RequestMapping(value="getUserInfo",method = RequestMethod.GET)
    public ResponseVo getUserInfo (){
        //获取当前登录用户
        String userId = CurrentUser.getCurrentUser();
        if (userId!=null&&userId.trim().length()>0) {
            //将用户ID传入后端进行查询
            int uuid = Integer.parseInt(userId);
            UserInfoModel userInfo = userAPI.getUserInfo(uuid);
            if (userInfo!=null){
                return ResponseVo.success(userInfo);
            }else {
                return ResponseVo.appFail("用户信息查询失败");
            }
        }else {
            return ResponseVo.serviceFail("用户未登录");
        }

    }

    @RequestMapping(value="updateUserInfo",method = RequestMethod.POST)
    public ResponseVo updateUserInfo(UserInfoModel userInfoModel){
        //获取当前登录用户
        String userId = CurrentUser.getCurrentUser();
        if (userId!=null&&userId.trim().length()>0) {
            //将用户ID传入后端进行查询
            int uuid = Integer.parseInt(userId);
            if (uuid!=userInfoModel.getUuid()){
                return ResponseVo.serviceFail("请修改您个人的信息");
            }
            UserInfoModel userInfo = userAPI.updateUserInfoModel(userInfoModel);
            if (userInfo!=null){
                return ResponseVo.success(userInfo);
            }else {
                return ResponseVo.appFail("用户信息修改失败");
            }
        }else {
            return ResponseVo.serviceFail("用户未登录");
        }

    }
}
