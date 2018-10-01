$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'cash/withdraw/list',
        datatype: "json",
        colModel: [
			{ label: 'ID', name: 'id',align : 'center', width: 0, key: true, hidden:true},
			{ label: '用户昵称', name: 'u.nickname',align : 'center', sortable:false, width: 38 },
            { label: '奖励心意', name: 'bcoin',align : 'center', sortable:false, width: 38 },
            { label: '充值心意', name: 'rcoin',align : 'center', sortable:false, width: 38 },
            { label: '用户手机号', name: 'u.phone',align : 'center', sortable:false, width: 38 },
			{ label: '转到类型 ', name: 't',align : 'center', sortable:false, width: 20 ,formatter: function(value, options, row)
            {
                var _str = " ";
                if(value == 1){
                    _str = '<a href="javascript:void(0);" ><span class="label label-info">支付宝</span></a>';

                }else if(value == 2){
                    _str = '<a href="javascript:void(0);" ><span class="label label-success">微信</span></a>';
                }else{
                    _str = '<a href="javascript:void(0);" ><span class="label label-danger">其他</span></a>';
				}
                return _str;
            }},


			{ label: '提交时间', name: 'cs',align : 'center', sortable:false, width: 48, formatter:"date", formatoptions: {srcformat:'U', newformat:'Y-m-d H:i:s'} },
            { label: '操作时间', name: 'ms',align : 'center', sortable:false, width: 48, formatter:"date", formatoptions: {srcformat:'U', newformat:'Y-m-d H:i:s'} },
            { label: '状态', name: 's',align : 'center', width: 20, formatter: function(value, options, row)
            {
                var _str = " ";
                if(value == 0){
                    _str = '<a href="javascript:void(0);" onclick="javascript:vm.doneFun(\''+ $.trim(row.id) + '\' )"><span class="label label-warning">审核中</span></a>';

                }else if(value == 1){
                    _str = '<a href="javascript:void(0);" ><span class="label label-success">通过</span></a>';
                }else if(value == -1){
                    _str = '<a href="javascript:void(0);" ><span class="label label-danger">失败</span></a>';
                }else if(value == 2){
                    _str = '<a href="javascript:void(0);" ><span class="label label-danger">处理中</span></a>';
                }else if(value == 5){
                    _str = '<a href="javascript:void(0);" ><span class="label label-danger">退款成功</span></a>';
                }else if(value == -5){
                    _str = '<a href="javascript:void(0);" ><span class="label label-danger">退款失败</span></a>';
                }
                return _str;
            }},
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
            page: "page.totalPages",
            total: "page.totalElements",
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
        doneFun: function (id) {

            var contents = '<div style="width: 100%;">' +
                '<label >提现审核 :</label> &nbsp;&nbsp;&nbsp;&nbsp;'+
                '<label for="tg">通过 <input type="radio" checked id="tg" value="1" name="done"></label> &nbsp;&nbsp;&nbsp;&nbsp; <label for="btg">不通过 <input type="radio" id="btg" value="0" name="done"></label><br>'+


                '</div>';
            $.confirm({
                closeIcon: true, // hides the close icon.
                title: '提现审核',
                content: contents,
                width:180,
                height:500,
                buttons: {
                    confirm: {
                        text: '确定',
                        btnClass: 'btn-success',
                        keys: ['enter', 'shift'],
                        action: function(){
                            var done = $("input[name='done']:checked").val();
                            if (done == 0){
                                var url = baseURL + 'cash/withdraw/outback';

                                var data = JSON.stringify({'id':id});
                                $.ajax({
                                    type: "POST",
                                    url: url,
                                    data: data,
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
                            }else if(done == 1){
                                var url = baseURL + 'cash/withdraw/cash/' + id;
                                $.ajax({
                                    type: "GET",
                                    url: url,
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
                            }else{
                                alert('选择操作',function (index) {
                                    return false;
                                });
                                return false;
                            }


                        }
                    },
                    cancel: {
                        text: '取消',
                        btnClass: 'btn-danger',
                        keys: ['enter', 'shift'],
                        action: function(){

                        }
                    }
                }
            });
        },
		trash: function () {
			$('#filterTime').val('');
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增礼物";
            $("#package").val('');
			vm.dto = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改礼物配置";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.dto.id == null ? baseURL + "cash/withdraw/save" : baseURL + "cash/withdraw/update";

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
				    url: baseURL + "cash/withdraw/delete",
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
				    url: baseURL + "cash/withdraw/delete",
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
			$.get(baseURL + "cash/withdraw/info/"+id, function(r){
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

            delete _postData['name'];
			

			
			if(vm.q.name)
			{
				_postData['name'] = vm.q.name;
			}

			var page = $("#jqGrid").jqGrid('getGridParam','page') || 1;
			$("#jqGrid").jqGrid('setGridParam',{
                page: 1//page
            }).trigger("reloadGrid");
		}
	},
	beforeCreate: function()
	{
		
	}
});