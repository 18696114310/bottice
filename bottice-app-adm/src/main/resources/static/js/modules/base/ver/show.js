$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'base/ver/list',
        datatype: "json",
        colModel: [
			{ label: 'ID', name: 'id', width: 0, key: true, hidden:true},
			{ label: '版本号', name: 'appVer', sortable:false, width: 38 },
			{ label: '内部编号', name: 'appNo', sortable:false, width: 38 },
			{ label: '系统类型', name: 'os', sortable:false, width: 38, formatter: function(value, options, row){
				return vm.getDict(value);
			}},
			{ label: '系统版本', name: 'osVer', sortable:false, width: 36 },
			{ label: '下载链接', name: 'url', sortable:false, width: 86, formatter: function(value, options, row){
				return "<a target = '_blank' href='" + value + "'>" + value + "</a>";
			}},
			{ label: '备注', name: 'remark', sortable:false, width: 86, formatter:cFmatter},
			{ label: '是否最新', name: 'nextId', width: 36, formatter: function(value, options, row){
				return value === 0 ? '<span class="label label-success">是</span>' : '<span class="label label-danger">否</span>';
			}},
			/*{ label: '强制更新', name: 'isForce', width: 36, formatter: function(value, options, row){
				return value === 1 ? '<span class="label label-success">是</span>' : '<span class="label label-danger">否</span>';
			}},*/
			{ label: '状态', name: 'status', width: 36, formatter: function(value, options, row){
				return value === 1 ? '<span class="label label-success">正常</span>' : '<span class="label label-danger">禁用</span>';
			}},
			{ label: '发布时间', name: 'timePublish', sortable:false, width: 48, formatter:"date", formatoptions:{srcformat:'Y-m-d H:i:s', newformat:'Y-m-d H:i:s'} },
			{ label: '操作', name: 'operation', sortable:false, width: 38, formatter: function(value, options, row)
				{
					return "<a href='javascript:vm.delCol(\"" + row.id + "\");'><span class='label label-warning'><i class='fa fa-trash-o'></i>删除</span></a>";
				}
			}
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
		trash: function () {
			$('#filterTime').val('');
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增应用版本";
            $("#package").val('');
			vm.dto = {os:'104001', isForce:0};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改应用版本";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.dto.id == null ? baseURL + "base/ver/save" : baseURL + "base/ver/update";
			vm.dto['url'] = $("#package").val();
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
				    url: baseURL + "base/ver/delete",
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
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "PUT",
				    url: baseURL + "base/ver/delete",
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
		getInfo: function(id){
			$.get(baseURL + "base/ver/info/"+id, function(r){
                vm.dto = r.dto;
                $("#package").val(vm.dto.url);
            });
		},
		getDict: function(did){
			if(vm.oss)
			{
				for (var i = 0; i < vm.oss.length; i++)
				{
					if(null === vm.oss[i]) continue;

					if(vm.oss[i].value == did)
					{
						return vm.oss[i].label;
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
			delete _postData['os'];
			
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
				_postData['os'] = vm.q.type;
			}

			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	},
	beforeCreate: function()
	{
		$.get(baseURL + "sys/dictinfo/infos/104", function(r) //获取字典系统类型
		{
            vm.oss = r.dtos;
        });
	}
});