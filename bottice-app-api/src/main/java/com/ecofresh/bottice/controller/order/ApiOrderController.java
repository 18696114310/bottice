package com.ecofresh.bottice.controller.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.order.form.OrderForm;
import com.ecofresh.bottice.order.service.OrderService;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.user.service.UserService;
import com.ecofresh.common.annotation.IgnoreAuth;
import com.ecofresh.common.exception.ErrorDesc;
import com.ecofresh.common.utils.R;
import com.ecofresh.common.validator.ValidatorUtils;
import com.ecofresh.common.validator.group.UpdateGroup;
import com.ecofresh.security.SessionTokenProviderFactory;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 积分订单信息
 * @author zzy
 * @email 
 * @date 2018-06-15 12:00
 */
@RestController
@RequestMapping("order")
@Api("积分订单接口")
public class ApiOrderController extends ApiAbstractController
{
	
    /***
	 * 添加积分订单
	 * @param userId
	 * @return
	 */
    @PutMapping("addOrderInfo")
    @ApiOperation(value = "添加积分信息", notes = "添加积分说明")
    public R addOrderInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody OrderForm form)
    {
       //判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
       if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_ID_EMPTY.getMsg());
    	}
	     //参数验证
	   	ValidatorUtils.validateEntity(form, UpdateGroup.class);
	   	if(Long.parseLong(form.getOrderPrice())<=Constant.Order.MAX_PRICE)
    	{
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.ORDER_PRICE_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.ORDER_PRICE_ERR.getMsg());
    	}
	   	form.setUserId(sessionId);
	   	User user = userService.findById(sessionId);
	   	if(null == user || null == user.getUserType() || user.getUserType()==Constant.User.USER_TYPE_WEB ){
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.LACK_OF_AUTHORITY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.LACK_OF_AUTHORITY.getMsg());
	   	}
	   	if(service.insert(form,user.getStoreid(),sessionId)){
	   		return R.ok(1);
	   	}else {
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.INTEGRAL_OPERATION_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.INTEGRAL_OPERATION_ERR.getMsg());
		}
    }
   
    /***
	 * 获取用户订单详细信息
	 * @return
	 */
    @IgnoreAuth
    @GetMapping("getOrderInfos")
    @ApiOperation(value = "获取用户订单详细信息", notes = "获取用户订单详细信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "userId",             value = "要精确查询用户id", required = false),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "startDateTime",             value = "查询开始时间", required = false),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "endDateTime",             value = "查询结束时间", required = false),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "orderType",             value = "订单类型", required = false)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,Integer page ,
    		Integer row , String userId,String startDateTime,String endDateTime,String orderType)
    {
    	//判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
         if(StrUtil.isBlank(sessionId))
     	{
         	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
     	}
        if(StrUtil.isBlank(userId)){
        	userId=sessionId;
        }
    	if(null == page || null == row){
    		return R.error(com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getMsg());
    	}
        return R.ok(1).put("profile", service.find(page,row,userId,startDateTime,endDateTime,orderType));
    }
    @Autowired
	private OrderService service;
    
    @Autowired
    private UserService userService;
}
