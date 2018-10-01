package com.ecofresh.bottice.enums;

/**
 * 错误枚举类
 * @author sunhongwei
 * @email sunhongwei@jshijian.com
 * @date 2017年11月4日 上午11:07:35
 */
public enum ErrorDesc 
{
	CODE_EMPTY(320, "code不能为空"),
	USER_NOT_EXIST(322, "用户不存在"),
	USER_ID_EMPTY(323, "用户ID不能为空"),
	LOGIN_ERR(324, "登陆失败"),
	FILE_EMPTY(325, "文件不能为空"),
	WX_LOGIN_ERR(421, "微信登陆失败"),
	WX_UNBING(422, "先绑定后才可登录"),
	WX_BING_REP(423, "该微信已被绑定"),
	WX_AUTH_ERR(424, "微信授权失败"),
	PAGE_EMPTY(424, "页码不能为空"),
	STORE_EMPTY(425, "门店信息获取失败"),
	STORE_UPD_ERR(426, "门店信息修改失败"),
	PRODUCT_EMPTY(427, "产品信息获取失败"),
	PRODUCT_UPD_ERR(428, "产品信息修改失败"),
	STORE_ID_EMPTY(429, "门店ID不能为空"),
	PRODUCT_ID_EMPTY(430, "产品ID不能为空"),
	RebateConfig_EMPTY(431, "返利配置获取失败"),
	RebateConfig_UPD_ERR(432, "返利配置修改失败"),
	RebateConfig_ID_EMPTY(433, "返利配置ID不能为空"),
	LACK_OF_AUTHORITY(434, "权限不足"),
	INTEGRAL_OPERATION_ERR(435, "积分不足!"),

	SUBSCRIBE_OVER(436, "请预约一个星期内的时间"),
	SUBSCRIBE_OVER_HAND(437, "请完成之前的预约再申请预约"),
	SUBSCRIBE_ERROR(438,"预约状态修改失败"),
	SUBSCRIBE_BEFORETIME(439,"您预约的时间不能在今天和今天以前"),
	SUBSCRIBE_INFO_ERROR(440,"当前预约信息不存在"),
	SESSION_EMPTY(441,"sessionId不能为空"),
	OLD_PWD_ERROR(442,"密码验证失败"),
	NODEL_CURRENT_USER(443,"不能删除当前用户信息"),
	PSW_ILLEGAL(444,"密码不能少于6位数"),
	SUBSCRIBE_SUCCESS_FAILE(445,"您只能在预约的当天完成预约"),
	STORE_PHONE_ERR(446,"门店电话格式错误"),
	ORDER_PRICE_ERR(447,"金额必须大于0"),
	NO_MANAGE_ERR(448,"非后台用户不能登录"),
	SUBSCRIBE_ID_EMPTY(449,"预约记录的id不能为空"),
	SUBSCRIBE_UPDATE_ERROR(450,"当前预约状态不能取消预约或者完成预约"),
	NO_LOGIN_STORESTATE(451,"店铺失效不允许登录"),
	STORE_IS_RESERVATION(452,"当前账户有未完成预约,不能删除"),
	IS_NOTNOW_USER(453,"校验密码用户非当前登录用户"),
	U_CODE_ERR(454,"您未被邀请，无法注册！");

	private Integer code;
    private String msg;

    private ErrorDesc(Integer code, String msg) 
    {
    	this.code = code;
        this.msg = msg;
    }
    
    public Integer getCode() 
    {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}