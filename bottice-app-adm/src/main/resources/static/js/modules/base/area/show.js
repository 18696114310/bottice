$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'base/area/list?level=2',
        datatype: "json",
        colModel: [
            { label: '城市', name: 'name', width: 100 ,align : 'center',sortable :false},
            { label: '城市编码', name: 'id', width: 100 ,align : 'center',sortable :false},
            { label: '区域编码', name: 'coden', width: 100 ,align : 'center',sortable :false},
            { label: '设置热门', name: 'h', width: 100 ,align : 'center',sortable :false,formatter: function(value, options, row)
            {
                var _str = " ";
                if(value == 1){
                    _str = '<a href="javascript:void(0);" onclick="javascript:vm.cancelHotCity(\''+ $.trim(row.id) + '\' )"><span class="label label-default">关闭</span></a>';

                }else{
                    _str = '<a href="javascript:void(0);" onclick="javascript:vm.setHotCity(\''+ $.trim(row.id) + '\' )"><span class="label label-success">开启</span></a>';
                }
                return _str;
            }
            },
            { label: '邮编', name: 'zcode', width: 100 ,align : 'center',sortable :false},
            { label: '简称', name: 'sn', width: 100 ,align : 'center',sortable :false},
            { label: '英文', name: 'spell', width: 100,align : 'center',sortable :false },
        ],
		viewrecords: true,
        height: 465,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.content",
            // page: "page.totalPages",
            total: "page.totalPages",
            records: "page.numberOfElements"
        },
        prmNames : {
            page:"page", 
            rows:"rows",
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
    jQuery("#jqGrid").jqGrid('navGrid','#jqGrid',{edit:false,add:false,del:false});

});

/*window.onload = function () {
    var manualuploader = new qq.FineUploader({
        element: document.getElementById("fine-uploader"),
        request: {
            endpoint: baseURL + 'sys/oss/upload?token=' + token,
            inputName: "file"
        },
        autoUpload: true,
        multiple: false,
        editFilename: {                                            //编辑名字
            enable: false
        },
        deleteFile: {  
            enabled: true,  
            endpoint: ''  
        },  
        validation: {
            allowedExtensions: ['apk', 'ipa', 'pxl', 'deb', 'sis', 'sisx', 'xap', 'png'],
            sizeLimit: 100 * (1024 * 1024) // 200 kB = 200 * 1024 bytes
        },
        text: {
            uploadButton: '<i class="icon-plus icon-white"></i> 上传'
        },
        messages: {	//自定义提示*********  
            typeError: "文件类型支持(apk,ipa,pxl,deb,sis,sisx,xap)",
            sizeError: "最大允许100M软件包"  
        },
        template: "qq-template",
        callbacks: {
        	onComplete:  function(id,  fileName,  responseJSON)  {
        		vm.dto.url = responseJSON.url;
        	},
        	onCancel:  function(id,  fileName)  {
        		vm.dto.url = "";
        	}
        }
    });
}*/

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			type: ''
		},
		showList: true,
		title: null,
		dto: {},
		oss: {} //系统类型 字典
	},
	methods: {
        query: function () {
            vm.reload();
        },
        reload: function () {
            vm.showList = true;
            var _postData = $("#jqGrid").jqGrid('getGridParam','postData');

            delete _postData['name'];

            if($.trim(vm.q.name) != '')
            {
                _postData['name'] = $.trim(vm.q.name);
            }

            $("#jqGrid").jqGrid('setGridParam',
                {
                    postData: _postData
                }).trigger("reloadGrid");
        },
        setHotCity : function (code) {
            $.ajax({
                url: baseURL + 'base/area/update?id=' + code + '&h=1',
                type: 'post'
            }).then(function (data) {
                console.log(data);
                $("#jqGrid").jqGrid('setGridParam').trigger("reloadGrid");
            },function (data) {

            });
        },
        cancelHotCity: function (code) {
            $.ajax({
                url: baseURL + 'base/area/update?id=' + code + '&h=0',
                type: 'post'
            }).then(function (data) {
                console.log(data);
                $("#jqGrid").jqGrid('setGridParam').trigger("reloadGrid");
            },function (data) {

            });
        }
	},
	beforeCreate: function()
	{
		
	}
});