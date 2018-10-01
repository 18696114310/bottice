//jqGrid的配置信息
if($.jgrid){
    $.jgrid.defaults.width = 1000;
    $.jgrid.defaults.responsive = true;
    $.jgrid.defaults.styleUI = 'Bootstrap';
}

//工具集合Tools
window.T = {};

// 获取请求参数
// 使用示例
// location.href = http://localhost/index.html?id=123
// T.p('id') --> 123;
var url = function(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
};
T.p = url;

//请求前缀
var baseURL = "/";

//登录token
var token = localStorage.getItem("token");
if(token == 'null'){
    parent.location.href = baseURL + 'login.html';
}

//jquery全局配置
$.ajaxSetup({
	dataType: "json",
	cache: false,
	beforeSend: function (xhr) { //发送请求前触发 
　　　　	//可以设置自定义标头  
		xhr.setRequestHeader('token', token);   
		xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');  
	}, 
    xhrFields: {
	    withCredentials: true
    },
    complete: function(xhr) {
        //token过期，则跳转到登录页面
        if(xhr.responseJSON && xhr.responseJSON.code == 401){
            parent.location.href = baseURL + 'login.html';
        }
    }
});

//jqgrid全局配置
$.extend($.jgrid.defaults, {
    ajaxGridOptions : {
        headers: {
            "token": token
        }
    }
});

//权限判断
function hasPermission(permission) {
    if (window.parent.permissions.indexOf(permission) > -1) {
        return true;
    } else {
        return false;
    }
}

//重写alert
window.alert = function(msg, callback){
	parent.layer.alert(msg, function(index){
		parent.layer.close(index);
		if(typeof(callback) === "function"){
			callback("ok");
		}
	});
}

//重写confirm式样框
window.confirm = function(msg, callback){
	parent.layer.confirm(msg, {btn: ['确定','取消']},
	function(){//确定事件
		if(typeof(callback) === "function"){
			callback("ok");
		}
	});
}

//选择一条记录
function getSelectedRow() {
    var grid = $("#jqGrid");
    var rowKey = grid.getGridParam("selrow");
    if(!rowKey){
    	alert("请选择一条记录");
    	return ;
    }
    
    var selectedIDs = grid.getGridParam("selarrrow");
    if(selectedIDs.length > 1){
    	alert("只能选择一条记录");
    	return ;
    }
    
    return selectedIDs[0];
}

//选择多条记录
function getSelectedRows() {
    var grid = $("#jqGrid");
    var rowKey = grid.getGridParam("selrow");
    if(!rowKey){
    	alert("请选择一条记录");
    	return ;
    }
    
    return grid.getGridParam("selarrrow");
}

//判断是否为空
function isBlank(value) {
    return !value || !/\S/.test(value)
}

//获取时间戳
function getTime(tm)
{
	return Date.parse(new Date(tm));
}

//特殊字符替换
//cellvalue - 当前cell的值  
//options - 该cell的options设置，包括{rowId, colModel,pos,gid}  
//rowObject - 当前cell所在row的值，如{ id=1, name="name1", price=123.1, ...} 
function cFmatter(cellvalue, options, rowObject)
{
	if(null == cellvalue || "" == cellvalue) return '';
    return removeHTMLTag(cellvalue);  
}

//删除html标签
function removeHTMLTag(str) 
{
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str = str.replace(/[\r\n]/g,"");//去掉换号
    str = str.replace(/ /ig,'');//去掉
    str = str.replace(/↵/ig,'');//去掉
    console.log(str);
    return str;
}

/***
 * 获取请求参数信息
 * @param name
 * @returns
 */
function getParameter(name) 
{
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg);
	if (r != null) return unescape(r[2]); return null; 
}

/***
 * 获取请求参数信息
 * @param name
 * @returns
 */
function toUtf8(str) 
{   
    var out, i, len, c;   
    out = "";   
    len = str.length;
    
    for(i = 0; i < len; i++) 
    {
    	c = str.charCodeAt(i);
    	if ((c >= 0x0001) && (c <= 0x007F)) {
        	out += str.charAt(i);   
    	} else if (c > 0x07FF) {
        	out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));   
        	out += String.fromCharCode(0x80 | ((c >>  6) & 0x3F));   
        	out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));   
    	} else {
        	out += String.fromCharCode(0xC0 | ((c >>  6) & 0x1F));   
        	out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));   
    	}   
    }
    
    return out;   
}  

/** 
 * 时间对象的格式化; 
 */  
Date.prototype.format = function(format) 
{  
    /* 
     * eg:format="yyyy-MM-dd hh:mm:ss"; 
     */  
    var o = {  
        "M+" : this.getMonth() + 1, // month  
        "d+" : this.getDate(), // day  
        "h+" : this.getHours(), // hour  
        "m+" : this.getMinutes(), // minute  
        "s+" : this.getSeconds(), // second  
        "q+" : Math.floor((this.getMonth() + 3) / 3), // quarter  
        "S" : this.getMilliseconds()  
        // millisecond  
    }  
  
    if (/(y+)/.test(format))
    {  
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4  
                        - RegExp.$1.length));  
    }  
  
    for (var k in o) 
    {
        if (new RegExp("(" + k + ")").test(format)) {  
            format = format.replace(RegExp.$1, RegExp.$1.length == 1  
                            ? o[k]  
                            : ("00" + o[k]).substr(("" + o[k]).length));  
        }  
    }
    
    return format;  
}

function AddElement(type, name)
{  
	var TemO=document.getElementById("add");  
	var newInput = document.createElement("input");   
	newInput.type = type;
	newInput.name = name;
	newInput.className = "form-control";
	newInput.style.width = "78%";
	TemO.appendChild(newInput);  
	var newline= document.createElement("br"); 
	TemO.appendChild(newline); 
}

//时间戳转 时间过滤器
Vue.filter('format', function (str) {
    var format = 'yyyy-MM-dd h:m:s';
    var newdate = new Date(str);
    var date = {
        "M+": newdate.getMonth() + 1,
        "d+": newdate.getDate(),
        "h+": newdate.getHours(),
        "m+": newdate.getMinutes(),
        "s+": newdate.getSeconds(),
        "q+": Math.floor((newdate.getMonth() + 3) / 3),
        "S+": newdate.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (newdate.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1
                ? (date[k].toString().length == 1 ? "0" + date[k] : date[k]) : ("00" + date[k]).substr(("" + date[k]).length));
        }
    }
    return format;
});

//年龄处理过滤器
Vue.filter('ages', function (str) {
    if (typeof(str) == "string"){
        var   r = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
        if(r==null)return   false;
        var   d=   new   Date(r[1],   r[3]-1,   r[4]);
        if   (d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4])
        {
            var   Y   =   new   Date().getFullYear();
            return((Y-r[1])   +"岁");
        }
        return("0岁");
    }else{
        return("0岁");
    }
});