var gid = null === getParameter("gid") ? -1 : getParameter("gid");

$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/dictinfo/list?gid='+gid,
        datatype: "json",
        colModel: [			
			{ label: '键',  name: 'label', index: 'label', width: 86 }, 			
			{ label: '值',  name: 'value', index: 'value', width: 86 }, 			
			{ label: '排序', name: 'sort', index: 'sort', width: 68 }, 			
			{ label: '描述', name: 'remarks', index: 'remarks', width: 136 },
			{ label: '时间', name: 'cs', sortable:false, width: 86, editable:false, formatter:"date", formatoptions: {srcformat:'U', newformat:'Y-m-d H:i:s'}},
			{ label: '操作', name: 'operation', sortable:false, width: 48, align:"center", formatter: function(value, options, row)
				{
					return "<a href='javascript:vm.delCol(" + row.id + ");'><span class='label label-warning'><i class='fa fa-trash-o'></i>删除</span></a>";
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
        opens : 'right', //日期选择框的弹出位置
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

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			key: null
		},
		showList: true,
		title: null,
		dto: {gid:gid}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		back: function () {
			history.go(-1);
		},
		trash: function () {
			$('#filterTime').val('');
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.dto = {gid:gid};
		},
		getInfo: function(id){
			$.get(baseURL + "sys/dictinfo/info/"+id, function(r){
                vm.dto = r.dto;
            });
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.dto.id == null ? baseURL + "sys/dictinfo/save" : baseURL + "sys/dictinfo/update";
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
				    url: baseURL + "sys/dictinfo/delete",
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
				    url: baseURL + "sys/dictinfo/delete",
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
		reload: function (event) {
			vm.showList = true;
			var _postData = $("#jqGrid").jqGrid('getGridParam','postData');
			delete _postData['stime'];
			delete _postData['etime'];
			if((vtime = $.trim($('#filterTime').val())) != '')
			{
				if(vtime.indexOf(" - ") > -1)
				{
					_postData['stime'] = getTime($.trim(vtime.split(" - ")[0]));
					_postData['etime'] = getTime($.trim(vtime.split(" - ")[1]));
				}
				else
				{
					_postData['stime'] = getTime(vtime);
				}
			}

			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});