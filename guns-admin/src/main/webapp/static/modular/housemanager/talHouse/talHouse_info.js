/**
 * 初始化房屋管理详情对话框
 */
var TalHouseInfoDlg = {
    talHouseInfoData : {}
};

/**
 * 清除数据
 */
TalHouseInfoDlg.clearData = function() {
    this.talHouseInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TalHouseInfoDlg.set = function(key, val) {
    this.talHouseInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TalHouseInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
TalHouseInfoDlg.close = function() {
    parent.layer.close(window.parent.TalHouse.layerIndex);
}

/**
 * 收集数据
 */
TalHouseInfoDlg.collectData = function() {
    this
    .set('id')
    .set('houseUser')
    .set('houseAddress')
    .set('houseDate')
    .set('houseDesc');
}

/**
 * 提交添加
 */
TalHouseInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/talHouse/add", function(data){
        Feng.success("添加成功!");
        window.parent.TalHouse.table.refresh();
        TalHouseInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.talHouseInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
TalHouseInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/talHouse/update", function(data){
        Feng.success("修改成功!");
        window.parent.TalHouse.table.refresh();
        TalHouseInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.talHouseInfoData);
    ajax.start();
}

$(function() {

});
