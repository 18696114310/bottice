/*
**
*/
(function(){
	//...

	window.config = {
		url: 'http://knockit.knockit.cn:8080/',
		dateStr: function (date){	//时间优化处理
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
	};
	//....
	var winWidth = window.screen.availWidth;//document.body.clientWidth;
	if (parseInt(winWidth) < 641 && parseInt(winWidth) > 319) {
		document.documentElement.style.fontSize = parseInt(winWidth) / 7.5 + 'px';
	}else if(parseInt(winWidth) < 320){
		document.documentElement.style.fontSize = 320 / 7.5 + 'px';
	}else if(parseInt(winWidth) > 641){
		document.documentElement.style.fontSize = 640 / 7.5 + 'px';
	}
})();



