package com.stylefeng.guns.modular.housemanager.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.util.ToolUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import com.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import com.stylefeng.guns.modular.system.model.TalHouse;
import com.stylefeng.guns.modular.housemanager.service.ITalHouseService;

/**
 * 房屋管理控制器
 *
 * @author fengshuonan
 * @Date 2019-10-24 11:11:58
 */
@Controller
@RequestMapping("/talHouse")
public class TalHouseController extends BaseController {

    private String PREFIX = "/housemanager/talHouse/";

    @Autowired
    private ITalHouseService talHouseService;

    /**
     * 跳转到房屋管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "talHouse.html";
    }

    /**
     * 跳转到添加房屋管理
     */
    @RequestMapping("/talHouse_add")
    public String talHouseAdd() {
        return PREFIX + "talHouse_add.html";
    }

    /**
     * 跳转到修改房屋管理
     */
    @RequestMapping("/talHouse_update/{talHouseId}")
    public String talHouseUpdate(@PathVariable Integer talHouseId, Model model) {
        TalHouse talHouse = talHouseService.selectById(talHouseId);
        model.addAttribute("item",talHouse);
        LogObjectHolder.me().set(talHouse);
        return PREFIX + "talHouse_edit.html";
    }

    /**
     * 获取房屋管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        //判断condition是否有值
        //如果没有值，则表示查询全部
        if(ToolUtil.isEmpty(condition)){
            return talHouseService.selectList(null);
        }else {
            //如果有值，则认为是按业务名称进行模糊查询
            EntityWrapper<TalHouse> wrapper = new EntityWrapper<>();
            Wrapper<TalHouse> talHouseWrapper = wrapper.like("house_user", condition);
            return talHouseService.selectList(talHouseWrapper);
        }
    }

    /**
     * 新增房屋管理
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(TalHouse talHouse) {
        talHouseService.insert(talHouse);
        return SUCCESS_TIP;
    }

    /**
     * 删除房屋管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer talHouseId) {
        talHouseService.deleteById(talHouseId);
        return SUCCESS_TIP;
    }

    /**
     * 修改房屋管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(TalHouse talHouse) {
        talHouseService.updateById(talHouse);
        return SUCCESS_TIP;
    }

    /**
     * 房屋管理详情
     */
    @RequestMapping(value = "/detail/{talHouseId}")
    @ResponseBody
    public Object detail(@PathVariable("talHouseId") Integer talHouseId) {
        return talHouseService.selectById(talHouseId);
    }
}
