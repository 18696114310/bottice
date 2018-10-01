package com.ecofresh.bottice.constant;

public interface Constant
{
	public static final String PROFILE_KEY = "u:profile"; 			//用户基础信息
	public static final Long INV_MIN_CODE = 800000000000L;  		//邀请码初始值
	public static final String CODE_URL_FIX = ".jpg";  				//二维码后缀
	public static final String STORE_KEY = "s:profile"; 			//门店基础信息
	
	interface User
	{
    	//状态 正常
    	public static final Integer ACTIVE = 1;
		//状态 禁用
    	public static final Integer SUSPENDED = 0;
		//已删除
    	public static final Integer DEL = -1;
		//未认证
		public static final Integer AUTH_DEF =  0;
		//认证中
		public static final Integer AUTH_SUC =  1;
		//认证通过
		public static final Integer AUTH_PAS =  2;
		//认证失败
		public static final Integer AUTH_FAI = -2;
		//默认性别
		public static final Integer GENDER = -1;
		//默认性别 0女
		public static final Integer WOMAN = 0;
		//默认性别1男
		public static final Integer MAN = 1;
		//日常消费
		public static final Integer DAILY_CONSUMPTION=0;
		//大健康消费
		public static final Integer HEALTHY_CONSUMPTION=1;
		//用户充值
		public static final Integer RECHARGE=0;
		//用户扣除日常消费积分
		public static final Integer REDUCE_DAILY_CONSUMPTION=1;
		//日常积分反利
		public static final Integer RET_DAILY_CONSUMPTION=2;
		//用户扣除大健康消费积分
		public static final Integer REDUCE_HEALTHY_CONSUMPTION=3;
		//大健康积分反利
		public static final Integer RET_HEALTHY_CONSUMPTION=4;
		//用户退款
		public static final Integer REFUND=5;
		//扣除返还积分
		public static final Integer RETURN_INTEGRAL=6;
		//用户积分默认值
		public static final Integer USER_DEFAULT_INTEGRAL=  0;
		//用户默认密码加密
		public static final String USER_DEFAULT_PWD="e10adc3949ba59abbe56e057f20f883e";
		//前端用户
    	public static final Integer USER_TYPE_WEB = 0;
		//后端用户
    	public static final Integer USER_TYPE_MGR = 1;
    	//预约中的全部预约状态
		public static final Integer SUBSCRIBE_STATUS_ALL=4;
		//密码长度限制
		public static final Integer USER_PWD_LIMIT=6;
		//默认推荐人数
		public static final Integer USER_REFEREE=0;
	}
	
	interface Order
	{
    	//是否删除 1=未删除  
    	public static final Integer ACTIVE = 1;
    	
    	//必须大于最小金额
    	public static final Long MAX_PRICE=(long) 0;
	}

	interface Subscribe
	{
		//预约状态待完成状态码
		public static final Integer SUBSCRIBE_ROUND=0;

		//已完成状态码
		public static final Integer SUBSCRIBE_REACH=1;

		//预约状态过期状态码
		public static final Integer SUBSCRIBE_TIMEOUT=2;

		//取消状态码
		public static final Integer SUBSCRIBE_CANCEL=3;

		//管理版取消预约标志码
		public static final String SUBSCRIBE_CANCEL_ADMIN="adminCancel";

		//预约日志操作的code
		public static final String SUBSCRIBELOG_REACH="subscribe_reach";

		public static final String SUBSCRIBELOG_CANCEL_ADMIN="subscribe_cancel_admin";

		public static final String SUBSCRIBELOG_CANCEL="subscribe_cancel";

		public static final String SUBSCRIBELOG_ADD="subscribe_add";

		public static final String SUBSCRIBELOG_TIMEOUT="subscribe_timeout";

		//预约日志状态
		public static final Integer OPERTIONSTATION_FAILED=0;

		public static final Integer OPERTIONSTATION_SUCCESS=1;

		public static final Integer OPERTIONSTATION_TIMEOUT=2;
	}

}
