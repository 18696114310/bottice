$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/oss/list',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'id', width: 20, key: true },
            { label: 'URL地址', name: 'url', width: 160, formatter: function(value, options, row){
            	if(null == value || "" == value) return "";
				return "<a target = '_blank' href='" + value + "'>" + value + "</a>";
			}},
			{ label: '原始文件名', name: 'nameOri', sortable:false, width: 48 },
			{ label: '扩展名', name: 'ext', sortable:false, width: 38 },
			{ label: '文件大小', name: 'lenByte', sortable:false, width: 38 },
			{ label: '状态', name: 'state', width: 38, formatter: function(value, options, row){
				return value === 1 ? '<span class="label label-success">成功</span>' : '<span class="label label-danger">失败</span>';
			}},
			{ label: '上传时间', name: 'createDate', sortable:false, width: 60, formatter:"date", formatoptions:{srcformat:'Y-m-d H:i:s', newformat:'Y-m-d H:i:s'}}
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
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
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

    new AjaxUpload('#upload', {
        action: baseURL + 'sys/oss/upload?token=' + token,
        name: 'file',
        autoSubmit:true,
        responseType:"json",
        onSubmit:function(file, extension){
            if(vm.config.type == null){
                alert("云存储配置未配置");
                return false;
            }
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))){
                alert('只支持jpg、png、gif格式的图片！');
                return false;
            }
        },
        onComplete : function(file, r){
            if(r.code == 0){
                alert(r.url);
                vm.reload();
            }else{
                alert(r.msg);
            }
        }
    });

});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
        config: {}
	},
    created: function(){
        this.getConfig();
    },
	methods: {
		query: function () {
			vm.reload();
		},
		getConfig: function () {
            $.getJSON(baseURL + "sys/oss/config", function(r){
				vm.config = r.config;
            });
        },
		addConfig: function(){
			vm.showList = false;
			vm.title = "云存储配置";
		},
		saveOrUpdate: function () {
			var url = baseURL + "sys/oss/saveConfig";
			$.ajax({
				type: "POST",
			    url: url,
                contentType: "application/json",
			    data: JSON.stringify(vm.config),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
        del: function () {
            var ossIds = getSelectedRows();
            if(ossIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/oss/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ossIds),
                    success: function(r){
                        if(r.code === 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
		reload: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});