package com.stylefeng.guns.rest.modular.user;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(loadbalance ="roundrobin")
public class UserServiceImpl implements UserAPI {

    @Autowired
    private MoocUserTMapper moocUserTMapper;

    @Override
    public int login(String username, String password) {
        //根据登录账号获取数据库信息
        MoocUserT moocUserT=new MoocUserT();
        moocUserT.setUserName(username);

        MoocUserT result = moocUserTMapper.selectOne(moocUserT);
        if (result!=null&&result.getUuid()>0){
            String md5Password = MD5Util.encrypt(password);
            if (StringUtils.equals(md5Password,result.getUserPwd())){
                return result.getUuid();
            }
        }
        return 0;
        //获取到的结果，然后与加密以后的密码做匹配
    }

    @Override
    public boolean register(UserModel userModel) {
        //将注册信息实体转换为数据实体
        MoocUserT moocUserT=new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setAddress(userModel.getAddress());
        moocUserT.setUserPhone(userModel.getPhone());

        //数据加密【MD5混淆加密+盐值】
        String md5Password = MD5Util.encrypt(userModel.getPassword());
        moocUserT.setUserPwd(md5Password);
        //将数据实体存入数据库
        Integer insert = moocUserTMapper.insert(moocUserT);
        if (insert>0){
            return true;
        }
        return false;
    }

    /**
     * false表示有，true表示没有
     * @param username
     * @return
     */
    @Override
    public boolean checkUsername(String username) {
        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name",username);
        Integer result = moocUserTMapper.selectCount(entityWrapper);
        if (result!=null&&result>0){
            //有该用户
            return false;
        }
        return true;
    }

    private UserInfoModel do2UserInfo(MoocUserT moocUserT){
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setLifeState(moocUserT.getLifeState()+"");
        userInfoModel.setHeadAddress(moocUserT.getAddress());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setBiography(moocUserT.getBiography());
        userInfoModel.setBeginTime(moocUserT.getBeginTime().getTime());
        userInfoModel.setAddress(moocUserT.getAddress());
       return userInfoModel;
    }
    @Override
    public UserInfoModel getUserInfo(int uuid) {
        //根据主键查询用户信息
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        UserInfoModel userInfoModel = do2UserInfo(moocUserT);
        return userInfoModel;
    }


    @Override
    public UserInfoModel updateUserInfoModel(UserInfoModel userInfoModel) {
        //将传入的数据转换为MoocUserT
        MoocUserT moocUserT = new MoocUserT();
       moocUserT.setUserSex(userInfoModel.getSex());
       moocUserT.setUpdateTime(null);
       moocUserT.setNickName(userInfoModel.getNickname());
       moocUserT.setLifeState(Integer.parseInt(userInfoModel.getLifeState()));
       moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
       moocUserT.setBirthday(userInfoModel.getBirthday());
       moocUserT.setBiography(userInfoModel.getBiography());
       moocUserT.setBeginTime(null);
       moocUserT.setEmail(userInfoModel.getEmail());
       moocUserT.setAddress(userInfoModel.getAddress());
       moocUserT.setUserPhone(userInfoModel.getPhone());
       moocUserT.setUuid(userInfoModel.getUuid());

        Integer isSuccess = moocUserTMapper.updateById(moocUserT);
        if (isSuccess>0){
            UserInfoModel userInfo = getUserInfo(moocUserT.getUuid());
            return userInfo;
        }
        return userInfoModel;
    }
}
