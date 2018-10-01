/*页面配置信息处理*/
//ios下载地址
var iosDownUrl = 'https://itunes.apple.com/cn/app/id1234489514?mt=8';
//安卓下载地址
var addroidDownUrl = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.xiang.app';//'https://www.jshijian.com/apk/jtime-release.apk';
//用户是ios端还是安卓端
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端

if (isiOS) {
    try{
        window.webkit.messageHandlers.GetRequestInfo.postMessage(null);
    }catch(err) {
        new Error(err);
    }
}
function getDomainUrl(result){
    window.domainUrl = result;
}
if (typeof(domainUrl) == 'undefined') {
    var curWwwPath=window.document.location.href; 
    //获取主机地址之后的目录如：/Tmall/index.jsp 
    var pathName=window.document.location.pathname; 
    var pos=curWwwPath.indexOf(pathName); 
    //获取主机地址，如： http://localhost:8080 
    var localhostPaht=curWwwPath.substring(0,pos); 
    window.domainUrl = localhostPaht;
    console.log(domainUrl);
}

//判断微信
function isWeiXin(){ 
    var ua = window.navigator.userAgent.toLowerCase(); 
    if(ua.match(/MicroMessenger/i) == 'micromessenger'){ 
        return true; 
    }else{ 
        return false; 
    } 
}   
//判断qq内置浏览器
function isQQ(){ 
    var ua = window.navigator.userAgent.toLowerCase(); 
    if(ua.match(/qq\//g) == 'qq/'){ 
        return true; 
    }else{ 
        return false; 
    } 
}  

//小数点计算问题
// Math.prototype.formatFloat = function(f, digit) { 
//     var m = Math.pow(10, digit); 
//     return parseInt(f * m, 10) / m; 
// }  
//设置rem单位
var winWidth = window.screen.availWidth;//document.body.clientWidth;
var screenHeight = $(window).height();
if (parseInt(winWidth) < 641 && parseInt(winWidth) > 319) {
	document.documentElement.style.fontSize = parseInt(winWidth) / 7.5 + 'px';
}else if(parseInt(winWidth) < 320){
	document.documentElement.style.fontSize = 320 / 7.5 + 'px';
}else if(parseInt(winWidth) > 641){
	document.documentElement.style.fontSize = 640 / 7.5 + 'px';
}


$('.secondBack').on('click',function(){
	console.log('二级页面返回');
	window.history.go(-1);
});
//时间优化处理
function dateStr(date){
    //获取js 时间戳
    var time=new Date().getTime();
    //去掉 js 时间戳后三位，与php 时间戳保持一致
    time=parseInt((time-date*1000)/1000);

    //存储转换值 
    var s;
    if(time<60*1){//1分钟内
        return '刚刚';
    }else if((time<60*60)&&(time>=60*1)){
        //超过1分钟少于1小时
        s = Math.floor(time/60);
        return  s+"分钟前";
    }else if((time<60*60*24)&&(time>=60*60)){ 
        //超过1小时少于24小时
        var date= new Date(parseInt(date) * 1000);
        var dateday = date.getDate();
        var thisday = new Date().getDate();
        if (dateday == thisday) {
        	return  "今天 "+ date.getHours() + ':' + date.getMinutes();
        }else{
        	return (date.getMonth()+1)+"月"+date.getDate() + "日  " + date.getHours() + ':' + date.getMinutes();
        }
        
    }else if(time>=60*60*24){ 
        //超过1天
        var date= new Date(parseInt(date) * 1000);
        return (date.getMonth()+1)+"月"+date.getDate() + "日  " + date.getHours() + ':' + date.getMinutes();
    }
}
//时间戳转 成年月日时间 
function dataFormat(str){
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
}
function dataFormatYMDHM(str){
    var format = 'yyyy-MM-dd h:m';
    var newdate = new Date(str);
    var date = {
        "M+": newdate.getMonth() + 1,
        "d+": newdate.getDate(),
        "h+": newdate.getHours(),
        "m+": newdate.getMinutes(),
        //"s+": newdate.getSeconds(),
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
}
function dataFormatMDHM(str){
    var format = 'yyyy-MM-dd h:m';
    var newdate = new Date(str);
    var date = {
        "M+": newdate.getMonth() + 1,
        "d+": newdate.getDate(),
        "h+": newdate.getHours(),
        "m+": newdate.getMinutes(),
        //"s+": newdate.getSeconds(),
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
    return format.substring(5,format.length);
}
function dataFormatYMDAP(str){
    var format = 'yyyy-MM-dd';
    var newdate = new Date(str);
    var date = {
        "M+": newdate.getMonth() + 1,
        "d+": newdate.getDate(),
        "h+": newdate.getHours(),
        //"m+": newdate.getMinutes(),
        //"s+": newdate.getSeconds(),
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
    if(date["h+"] < 13){
        return format + ' 上午';
    }else{
        return format + ' 下午';
    }
    
}
function dataFormatMD_HM_BT(str,strEnd){
    var format = 'MM-dd h:m';
    var newdate = new Date(str);
    var date = {
        "M+": newdate.getMonth() + 1,
        "d+": newdate.getDate(),
        "h+": newdate.getHours(),
        "m+": newdate.getMinutes(),
        //"s+": newdate.getSeconds(),
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
    
    var enddate = new Date(strEnd);

    return format + '--' 
            + (enddate.getHours().toString().length == 1 ? '0' + enddate.getHours() : enddate.getHours()) 
            + ':' 
            + (enddate.getMinutes().toString().length == 1 ? '0' + enddate.getMinutes() : enddate.getMinutes());
    
    
}
//获取url的参数querys
function getQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg); 
	if (r != null) return unescape(r[2]); return null; 
} 
//对象转成url类型get参数
var parseParam=function(param, key){  
    var paramStr="";  
    if(param instanceof String||param instanceof Number||param instanceof Boolean){  
        paramStr+="&"+key+"="+encodeURIComponent(param);  
    }else{  
        $.each(param,function(i){  
            var k=key==null?i:key+(param instanceof Array?"["+i+"]":"."+i);  
            paramStr+='&'+parseParam(this, k);  
        });  
    }  
    return paramStr.substr(1);  
};
var urlEncode = function (param, key, encode) {  
  if(param==null) return '';  
  var paramStr = '';  
  var t = typeof (param);  
  if (t == 'string' || t == 'number' || t == 'boolean') {  
    paramStr += '&' + key + '=' + ((encode==null||encode) ? encodeURIComponent(param) : param);  
  } else {  
    for (var i in param) {  
      var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i);  
      paramStr += urlEncode(param[i], k, encode);  
    }  
  }  
  return paramStr;  
}; 

//姓名隐藏后几位
function nameDo(name){
    if (name) {
        var nameLength = name.length;
        if (nameLength <= 1) {
            return name;
        }else{
            var newname = name.substr(0,1);
            
            for (var i = 0; i < nameLength -1; i++) {
                newname += '*';
            }
            return newname;
        }
    }else{
        return '';
    }
}
//图片自适应展示
function imgCenter(obj,width,height){
    //...可能遇到重置
    obj.css('margin-left','0rem');
    obj.css('margin-top','0rem');
    //...正文
    var img = new Image();
    img.src = obj.attr('src');
    var hRatio;
    var wRatio;
    var Ratio = 1;
    var w = img.width; //图片实际宽度
    var h = img.height;
    var dyw = width * 100;
    var dyh = height * 100;
    wRatio = w / dyw; //宽度缩放比
    hRatio = h / dyh;
    if (wRatio == hRatio){
        obj.css('width',(dyw / 100) + 'rem');
        obj.css('height',(dyh / 100) + 'rem');
    }else if(wRatio > hRatio){
        var ml = (dyw - dyh * w / h) / 2;
        obj.width('auto');
        obj.css('height',(dyh / 100) + 'rem');
        obj.css('margin-left',(ml / 100) + 'rem');
    }else if(wRatio < hRatio){
        var mt = (dyh - dyw * h / w) / 2;
        obj.height('auto');
        obj.css('width',(dyw / 100) + 'rem');
        obj.css('margin-top',(mt / 100) + 'rem');
    }
}
