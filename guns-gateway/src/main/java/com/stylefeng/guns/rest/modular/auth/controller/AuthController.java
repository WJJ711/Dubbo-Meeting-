package com.stylefeng.guns.rest.modular.auth.controller;


import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.auth.validator.IReqValidator;
import com.stylefeng.guns.rest.modular.vo.ResponseVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Reference(check = false)
    private UserAPI userAPI;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;



    @RequestMapping(value = "${jwt.auth-path}")
    public ResponseVo createAuthenticationToken(AuthRequest authRequest) {


        boolean validate = true;
        //去掉guns自身携带的用户名密码验证机制，使用我们自己的
        int userId = userAPI.login(authRequest.getUserName(), authRequest.getPassword());
        if (userId==0){
            validate=false;
        }

        if (validate) {
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken(userId+"", randomKey);
            return ResponseVo.success(new AuthResponse(token,randomKey));
        } else {
            return ResponseVo.serviceFail("用户名或密码错误");
        }
    }
}
