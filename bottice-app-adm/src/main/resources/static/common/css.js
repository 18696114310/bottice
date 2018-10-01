/**
 * 引用JS和CSS头文件
 */
var rootPath = getRootPath(); //项目路径

/** 
 * 时间对象的格式化; 
 * eg:format="yyyy-MM-dd hh:mm:ss"; 
 */  
Date.prototype.format = function(format) 
{
    var o =
    {
        "M+" : this.getMonth() + 1, // month  
        "d+" : this.getDate(), // day  
        "h+" : this.getHours(), // hour  
        "m+" : this.getMinutes(), // minute  
        "s+" : this.getSeconds(), // second  
        "q+" : Math.floor((this.getMonth() + 3) / 3), // quarter  
        "S" : this.getMilliseconds()    // millisecond  
    }
  
    if (/(y+)/.test(format))
    {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o)
    {
        if (new RegExp("(" + k + ")").test(format)) 
        {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));  
        }
    }
    
    return format;
}

var config = 
{
    version : (null === localStorage.getItem("vknock") || 'null' === localStorage.getItem("vknock")) ? new Date().format("yyyyMMdd") : localStorage.getItem("vknock") //'0.0.1'
}

/**
 * 动态加载CSS和JS文件
 */
var dynamicLoading =
{
    meta : function()
    {
        document.write('<meta charset="utf-8">');
        document.write('<meta http-equiv="X-UA-Compatible" content="IE=edge">');
        document.write('<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">');
        document.write('<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">');
    },

    css: function(path)
    {
        if(!path || path.length === 0)
        {
            throw new Error('argument "path" is required!');
        }

        document.write('<link rel="stylesheet" type="text/css" href="' + path + '?v='+ config.version +'">');
    },
    
    js: function(path, charset)
    {
        if(!path || path.length === 0)
        {
            throw new Error('argument "path" is required!');
        }

        document.write('<script charset="' + (charset ? charset : "utf-8") + '" src="' + path + '?v='+ config.version +'"></script>');
    }
};

/**
 * 取得项目路径
 * @author wul
 */
function getRootPath()
{
    //取得当前URL
    var path = window.document.location.href;
    //取得主机地址后的目录
    var pathName = window.document.location.pathname;
    var post = path.indexOf(pathName);
    //取得主机地址
    var hostPath = path.substring(0, post);
    //取得项目名
    var name = pathName.substring(0, pathName.substr(1).indexOf("/") + 1);
    return hostPath  + "/";
}

//动态生成meta
dynamicLoading.meta();

//动态加载项目 CSS文件
dynamicLoading.css(rootPath + "css/bootstrap.min.css");
dynamicLoading.css(rootPath + "css/font-awesome.min.css");
dynamicLoading.css(rootPath + "css/AdminLTE.min.css");
dynamicLoading.css(rootPath + "css/all-skins.min.css");
dynamicLoading.css(rootPath + "css/main.css");
dynamicLoading.css(rootPath + "css/bootstrap-table.min.css", "utf-8");

//treegrid
dynamicLoading.css(rootPath + "plugins/treegrid/jquery.treegrid.css", "utf-8");

//jqgrid
dynamicLoading.css(rootPath + "plugins/jqgrid/ui.jqgrid-bootstrap.css", "utf-8");

//ztree
dynamicLoading.css(rootPath + "plugins/ztree/css/metroStyle/metroStyle.css", "utf-8");

// <!-- daterangepicker -->
dynamicLoading.css(rootPath + "plugins/date/rangepicker/daterangepicker.css", "utf-8");

//<!-- layui -->
//dynamicLoading.css(rootPath + "plugins/layui/css/layui.css", "utf-8");