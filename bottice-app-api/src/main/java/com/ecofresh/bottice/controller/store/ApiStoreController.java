package com.ecofresh.bottice.controller.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.store.form.StoreForm;
import com.ecofresh.bottice.store.form.ShowStoreIntegralForm;
import com.ecofresh.bottice.store.service.StoreService;
import com.ecofresh.bottice.subscribe.entities.Subscribe;
import com.ecofresh.bottice.subscribe.service.SubscribeService;
import com.ecofresh.common.annotation.IgnoreAuth;
import com.ecofresh.common.exception.ErrorDesc;
import com.ecofresh.common.utils.R;
import com.ecofresh.common.validator.ValidatorUtils;
import com.ecofresh.common.validator.group.UpdateGroup;
import com.ecofresh.security.SessionTokenProviderFactory;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 用户信息
 * @author zzy
 * @email 
 * @date 2018-06-14 12:00
 */
@RestController
@RequestMapping("store")
@Api("门店信息接口")
public class ApiStoreController extends ApiAbstractController
{
	/***
	 * 获取门店信息
	 * @return
	 */
    @GetMapping("getStoreInfos")
    @ApiOperation(value = "获取门店信息", notes = "获取门店信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "key",             value = "关键词", required = false)
    })
    public R profile(Integer page ,Integer row , String key)
    {
    	if(null == page || null == row){
    		return R.error(com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getMsg());
    	}
        Store store = new Store();
        if(null != key){
        	store.setStoreName(key);
        }
        store.setState(Constant.User.ACTIVE);
        store.setStatus(Constant.User.ACTIVE);
        return R.ok(1).put("profile", service.find(page,row,store));
    }
    
    /***
	 * 获取门店信息
	 * @return
	 */
    @IgnoreAuth
    @GetMapping("getStoreMsg")
    @ApiOperation(value = "获取门店信息", notes = "获取门店信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "key",             value = "关键词", required = false)
    })
    public R getStoreMsg(Integer page ,Integer row , String key)
    {
    	if(null == page || null == row){
    		return R.error(com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getMsg());
    	}
        Store store = new Store();
        if(null != key){
        	store.setStoreName(key);
        }
        store.setState(1);
        //store.setStatus(Constant.User.ACTIVE);
        return R.ok(1).put("profile", service.find(page,row,store));
    }
	
	/***
	 * 根据Id获取门店详细信息
	 * @return
	 */
    @GetMapping("getStoreInfoById")
    @ApiOperation(value = "根据Id获取门店详细信息", notes = "根据Id获取门店详细信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "id",             value = "关键词", required = false)
    })
    public R getStoreInfoById(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,String id)
    {
    	
    	//判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
         if(StrUtil.isBlank(sessionId))
     	{
         	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
     	}
        return R.ok(1).put("profile", service.findCacheById(id));
    }
    /***
	 * 根据门店Id获取门店积分信息
	 * @return
	 */
    @PutMapping("getStoreIntegralInfoById")
    @ApiOperation(value = "获取门店积分信息", notes = "获取门店积分信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true)
    })
    public R getStoreIntegralInfoById(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody ShowStoreIntegralForm form)
    {
    	
    	//判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
         if(StrUtil.isBlank(sessionId))
     	{
         	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
     	}
         ValidatorUtils.validateEntity(form, UpdateGroup.class);
         form.setUserId(sessionId);
        return R.ok(1).put("profile", service.findStoreOrderById(form));
    }
	/***
	 * 获取门店积分信息
	 * @return
	 */
    @GetMapping("getStoreIntegralInfo")
    @ApiOperation(value = "获取门店积分信息", notes = "获取门店积分信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "storeid",             value = "关键词", required = false)
    })
    public R getStoreIntegralInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,String storeid)
    {
    	
    	//判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
     	{
         	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
     	}
    	//String sessionId="5b29bd2792168b3204155282";
        return R.ok(1).put("profile", service.find(sessionId,storeid));
    }
    
    /***
	 * 添加门店
	 * @param userId
	 * @return
	 */
    @PutMapping("addStoreInfo")
    @ApiOperation(value = "添加门店信息", notes = "添加门店说明")
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody StoreForm form)
    {
       //判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
       if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_EMPTY.getMsg());
    	}
	     //参数验证
	   	ValidatorUtils.validateEntity(form, UpdateGroup.class);
	   	if(!Validator.isNumber(form.getStorePhone()))
    	{
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_PHONE_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_PHONE_ERR.getMsg());
    	}
	   	form.setUserId(sessionId);
        return R.ok(1).put("store", service.insert(form));
    }
    /**
     * 删除门店(假删除)
     */
    @GetMapping("profile")
    @ApiOperation(value = "删除门店信息", notes = "删除门店信息说明")
    public R delate(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,String id)
    {
    	//判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
       if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
        Store store = new Store();
        if(null == id  || null == (store=service.findCacheById(id))){
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_EMPTY.getMsg());
        }
        Subscribe subscribe=subscribeService.findByStoreIdAndState(store.getId(), Constant.User.SUSPENDED);
        if(null != subscribe)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_IS_RESERVATION.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_IS_RESERVATION.getMsg());
    	}
        store.setMobUserId(sessionId);
        service.delById(store);
        return R.ok(1);
    }
    /***
	 * 修改门店信息
	 * @param userId
	 * @return
	 */
    @PutMapping("updateStoreInfo")
    @ApiOperation(value = "修改门店信息", notes = "修改门店说明")
    public R updateStoreInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody StoreForm form)
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
       form.setMobUserId(sessionId);
	   	if(service.update(form)){
	   		return R.ok(1);
	   	}else {
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_UPD_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_UPD_ERR.getMsg());
		}
        
    }
    /**
     * 获取所有未删除的门店信息
     */
    @GetMapping("getStoreInfo")
    @ApiOperation(value = "获取所有未删除的门店信息", notes = "获取所有未删除的门店信息说明")
    public R getStoreInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token, String storeId)
    {
    	//判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
    	if(null != storeId)
    		return R.ok(1).put("profile", service.findCacheById(storeId));
        return R.ok(1).put("profile", service.findAll());
        
    }
    @Autowired
	private StoreService service;
    
    @Autowired
	private SubscribeService subscribeService;
}
