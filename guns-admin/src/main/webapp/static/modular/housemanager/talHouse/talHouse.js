/**
 * 房屋管理管理初始化
 */
var TalHouse = {
    id: "TalHouseTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
TalHouse.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '房屋编号', field: 'id', visible: true, align: 'center', valign: 'middle'},
            {title: '业主名称', field: 'houseUser', visible: true, align: 'center', valign: 'middle'},
            {title: '房屋地址', field: 'houseAddress', visible: true, align: 'center', valign: 'middle'},
            {title: '房屋交付时间', field: 'houseDate', visible: true, align: 'center', valign: 'middle'},
            {title: '房屋描述', field: 'houseDesc', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
TalHouse.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        TalHouse.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加房屋管理
 */
TalHouse.openAddTalHouse = function () {
    var index = layer.open({
        type: 2,
        title: '添加房屋管理',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/talHouse/talHouse_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看房屋管理详情
 */
TalHouse.openTalHouseDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '房屋管理详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/talHouse/talHouse_update/' + TalHouse.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除房屋管理
 */
TalHouse.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/talHouse/delete", function (data) {
            Feng.success("删除成功!");
            TalHouse.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("talHouseId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询房屋管理列表
 */
TalHouse.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    TalHouse.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = TalHouse.initColumn();
    var table = new BSTable(TalHouse.id, "/talHouse/list", defaultColunms);
    table.setPaginationType("client");
    TalHouse.table = table.init();
});
