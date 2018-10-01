$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/carousel/list',
        datatype: "json",
        colModel: [
			{ label: 'ID', name: 'carId', width: 30, key: true, /*editable : true,edittype:"textarea",*/ hidden:true},
			{ label: '类型', name: 'type', width: 68, formatter: function(value, options, row){
				return vm.getDict(value);
			}},
			{ label: '地址', name: 'url', width: 96, formatter: function(value, options, row){
				return "<a target = '_blank' href='" + value + "'>" + value + "</a>";
			}},
			{ label: '跳转链接', name: 'ahref', width: 96, formatter: function(value, options, row){
				if(value) return "<a target = '_blank' href='" + value + "'>" + value + "</a>";
				return "";
			}},
			{ label: '描述', name: 'remark', width: 136 },
			{ label: '排序', name: 'sort',  width: 36 },
			{ label: '状态', name: 'status', width: 36, formatter: function(value, options, row){
				return value === 0 ? '<span class="label label-danger">禁用</span>' : '<span class="label label-success">正常</span>';
			}},
			{ label: '时间', name: 'timeCreate', sortable:false, width: 86, editable:false, formatter:"date", formatoptions:{srcformat:'Y-m-d H:i:s', newformat:'Y-m-d H:i:s'}},
			{ label: '操作', name: 'operation', sortable:false, width: 38, formatter: function(value, options, row)
				{
					return "<a href='javascript:vm.delCol(" + row.carId + ");'><span class='label label-warning'><i class='fa fa-trash-o'></i>删除</span></a>";
				}
			},
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
            root: "page.list",
            page: "page.pageNum",
            total: "page.pages",
            records: "page.total"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });
    jQuery("#jqGrid").jqGrid('navGrid','#jqGrid',{edit:false,add:false,del:false});
    
    //过滤时间
    var rangetime = $('#filterTime').daterangepicker({
        autoApply:false,
        singleDatePicker:false,
        showDropdowns:true,         // 是否显示年月选择条件
    	timePicker: true, 			// 是否显示小时和分钟选择条件
    	timePickerIncrement: 10, 	// 时间的增量，单位为分钟
        timePicker24Hour : true,
        opens : 'left', //日期选择框的弹出位置
        startDate: moment().startOf('year'), //startDate和endDate 的值如果跟 ranges 的两个相同则自动选择ranges中的行. 这里选中了清空行
        endDate: moment().endOf('year'),
    	ranges: {
    		'今日': [moment().startOf('day'), moment().endOf('day')],
    		'昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
    		'最近7日': [moment().subtract(6, 'days'), moment()],
    		'最近30日': [moment().subtract(29, 'days'), moment()],
    		'本月': [moment().startOf('month'), moment().endOf('month')],
    		'上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
    		'本年': [moment().startOf('year'), moment().endOf('year')],
    		'去年': [moment().subtract(1, 'year').startOf('year'), moment().subtract(1, 'year').endOf('year')]
    	},
        locale : {
            //format: 'YYYY-MM-DD HH:mm:ss',
            format: 'YYYY-MM-DD HH:mm',
            separator : ' - ',
        	customRangeLabel : '自定义',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1,
            startDate: moment().startOf('day'),
            endDate: moment().endOf('day')
        }
    },
    function(start, end, label) // 格式化日期显示框
    {
    	//选择时间 回调，暂时不做操作。。。
    });
    
    $('#filterTime').val('');
    setTimeout("$('#filterTime').val('')", 100);
});

window.onload = function () {
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
        validation: {
            allowedExtensions: ['jpeg', 'jpg', 'png', 'bmp'],
            sizeLimit: 20 * (1024 * 1024) // 200 kB = 200 * 1024 bytes
        },
        text: {
            uploadButton: '<i class="icon-plus icon-white"></i> 上传'
        },
        messages: {	//自定义提示*********  
            typeError: "上传文件类型错误",
            sizeError: "最大允许20M图片"  
        },
        template: "qq-template",
        callbacks: {
        	onComplete:  function(id,  fileName,  responseJSON)  {
        		vm.dto.url = responseJSON.url;
        	},
        	onCancel:  function(id,  fileName)  {
        		vm.dto.url = "";
        	}
        },
        debug: true
    });
}

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			type: ''
		},
		showList: true,
		title: null,
		dto: {},
		dtos: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		trash: function () {
			$('#filterTime').val('');
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.dto = {sort:0,type:''};
		},
		update: function (event) {
			var carId = getSelectedRow();
			if(carId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            vm.getInfo(carId)
		},
		saveOrUpdate: function (event) {
			var url = vm.dto.carId == null ? baseURL + "sys/carousel/save" : baseURL + "sys/carousel/update";
			console.log(JSON.stringify(vm.dto));
			$.ajax({
				type: "POST",
			    url: url,
			    data: JSON.stringify(vm.dto),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		delCol: function (id) {
			if(id == null){
				return ;
			}
			var ids = [id];
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "PUT",
				    url: baseURL + "sys/carousel/delete",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code === 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		del: function (event) {
			var carIds = getSelectedRows();
			if(carIds == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "PUT",
				    url: baseURL + "sys/carousel/delete",
				    data: JSON.stringify(carIds),
				    success: function(r){
						if(r.code === 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(carId){
			$.get(baseURL + "sys/carousel/info/"+carId, function(r){
                vm.dto = r.dto;
            });
		},
		getDict: function(did){
			if(vm.dtos)
			{
				for (var i = 0; i < vm.dtos.length; i++)
				{
					if(null === vm.dtos[i]) continue;

					if(vm.dtos[i].value == did)
					{
						return vm.dtos[i].label;
					}
				}
			}
			
			return did;
		},
		reload: function (event) {
			vm.showList = true;
			var _postData = $("#jqGrid").jqGrid('getGridParam','postData');
			delete _postData['sts'];
			delete _postData['ets'];
			delete _postData['type'];
			
			if((vtime = $.trim($('#filterTime').val())) != '')
			{
				if(vtime.indexOf(" - ") > -1)
				{
					_postData['sts'] = $.trim(vtime.split(" - ")[0]) + " 00";
					_postData['ets'] = $.trim(vtime.split(" - ")[1]) + " 59";
				}
				else
				{
					_postData['sts'] = vtime+ " 00";
				}
			}
			
			if(vm.q.type)
			{
				_postData['type'] = vm.q.type;
			}

			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	},
	beforeCreate: function()
	{
		$.get(baseURL + "sys/dictinfo/infos/1006", function(r) //获取字典轮播图类型
		{
            vm.dtos = r.dtos;
        });
	}
});