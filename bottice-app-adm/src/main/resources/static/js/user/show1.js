$(function () {
    /*$("#jqGrid").jqGrid({
        url: baseURL + 'app/user/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 0, key: true, hidden:true},
            { label: '心意号', name: 'no', sortable:false, width: 80 },
            { label: '邀请码', name: 'ucode', sortable:false, width: 80 },
            { label: '手机号', name: 'phone', sortable:false, width: 80 },
            { label: '点赞数', name: 'favor', sortable:false, width: 80 },
            { label: '位置信息', name: 'loc', sortable:false, width: 80 },
            { label: '开启直播', name: 'live', sortable:false, width: 80 ,formatter: function(value, options, row)
            {
                if (value == 0){
                    return "<a href='javascript:vm.delCol(" + row.id + ");'><span class='label label-success'>未直播</span></a>";
                }else if(value == 1){
                    return "<a href='javascript:vm.delCol(" + row.id + ");'><span class='label label-warning'>直播中</span></a>";
                }

            }},
            { label: 'token', name: 'token', sortable:false, width: 80 },
            { label: '登录时间', name: 'timeCreate', sortable:false, width: 80 },
            { label: '操作', name: 'operation', sortable:false, width: 38, formatter: function(value, options, row)
            {
                return "<a href='javascript:vm.delCol(" + row.id + ");'><span class='label label-warning'><i class='fa fa-trash-o'></i>删除</span></a>";
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
    });*/
    // jQuery("#jqGrid").jqGrid('navGrid','#jqGrid',{edit:false,add:false,del:false});

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

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            key: null
        },
        dataList:{},
        showList: true,
        title: null,
        dto: {}
    },
    mounted: function () {
        this.reload();
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
            vm.dto = {};
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
            var url = vm.dto.id == null ? baseURL + "app/user/save" : baseURL + "app/user/update";
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
                    url: baseURL + "app/user/delete",
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
                    url: baseURL + "app/user/delete",
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
            $.get(baseURL + "app/user/info/"+id, function(r){
                vm.dto = r.dto;
            });
        },
        reload: function (event) {
            var self = this;

            if((vtime = $.trim($('#filterTime').val())) != '')
            {
                if(vtime.indexOf(" - ") > -1)
                {
                    _postData['stimeStr'] = $.trim(vtime.split(" - ")[0]) + " 00";
                    _postData['etimeStr'] = $.trim(vtime.split(" - ")[1]) + " 59";
                }
                else
                {
                    _postData['stimeStr'] = vtime+ " 00";
                }
            }

            if(this.q.account)
            {
                _postData['account'] = vm.q.account;
            }

            //分页渲染
            $('.pagetionBox').createPage(function(n){
                var pagethis = this;
                var maplist = {};
                maplist.page = n;
                maplist.rows = 12;
                maplist.sidx = null;
                maplist.order = 'asc';
                $.ajax({
                    url: baseURL + "app/user/list",
                    type: 'get',
                    data: maplist,
                    datatype: "json"
                }).then(function(datares){
                    if (datares.code == 0){
                        var dataList = datares.page;
                        self.dataList = dataList.content;
                        console.log(datares);

                        pagethis.args.pageCount = dataList.totalPages;
                        pagethis.updateHtml(dataList.totalPages);
                    }else{
                        alert(datares.msg,function (index) {
                            window.location.reload();
                        })
                    }

                },function(){console.log('网络错误');});
            },{
                pageCount:1,//总页码,默认10
                showPrev:true,//是否显示上一页按钮
                showNext:true,//是否显示下一页按钮
                showTurn:true,//是否显示跳转,默认可以
                showNear:1,//显示当前页码前多少页和后多少页，默认2
                showSumNum:false//是否显示总页码
            },{
                "color":"#0f0",//字体颜色
                "borderColor":"#00f",//边线颜色
                "currentColor":"#ed601b"//当前页码的字体颜色
            });
        }
    }
});