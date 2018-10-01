package com.ecofresh.bottice.controller.rebateconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.product.entities.Product;
import com.ecofresh.bottice.rebateconfig.entities.RebateConfig;
import com.ecofresh.bottice.rebateconfig.form.RebateConfigForm;
import com.ecofresh.bottice.rebateconfig.service.impl.RebateConfigServiceImpl;
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
 * 返利配置信息
 * @author zzy
 * @email 
 * @date 2018-06-14 12:00
 */
@RestController
@RequestMapping("rebateConfig")
@Api("返利配置接口")
public class ApiRebateConfigController extends ApiAbstractController
{
	/***
	 * 获取返利配置
	 * @return
	 */
    @GetMapping("getRebateConfigInfo")
    @ApiOperation(value = "获取返利配置", notes = "获取返利配置说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "key",             value = "关键词", required = false)
    })
    public R getRebateConfigInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,Integer page ,Integer row , String key)
    {
    	//判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
    	if(null == page || null == row){
    		return R.error(com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getMsg());
    	}
    	RebateConfig rebateConfig = new RebateConfig();
        rebateConfig.setState(1);
        return R.ok(1).put("profile", service.find(page,row,rebateConfig));
    }
    
    /***
	 * 添加返利配置
	 * @param 
	 * @return
	 */
    @PutMapping("addRebateConfigInfo")
    @ApiOperation(value = "添加返利配置", notes = "添加返利配置说明")
    public R addRebateConfigInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody RebateConfigForm form)
    {
       //判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_ID_EMPTY.getMsg());
    	}
	     //参数验证
	   	ValidatorUtils.validateEntity(form, UpdateGroup.class);
	   	form.setUserId(sessionId);
        return R.ok(1).put("product", service.insert(form));
    }
    /**
     * 删除返利配置(假删除)
     */
    @GetMapping("delRebateConfigInfo")
    @ApiOperation(value = "删除返利配置", notes = "删除返利配置说明")
    public R delate(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,String id)
    {
    	//判断Session 是否存在
      String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
        RebateConfig rebateConfig = new RebateConfig();
        if(null == id  || null == (rebateConfig=service.findById(id))){
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_EMPTY.getMsg());
        }
        rebateConfig.setMobUserId(sessionId);
        service.delById(rebateConfig);
        return R.ok(1);
    }
    /***
	 * 修改返利配置
	 * @param 
	 * @return
	 */
    @PutMapping("updateRebateConfigInfo")
    @ApiOperation(value = "修改返利配置", notes = "修改返利配置说明")
    public R updateRebateConfigInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody RebateConfigForm form)
    {
       //判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_EMPTY.getMsg());
    	}
	     //参数验证
	   	form.setMobUserId(sessionId);
	   	if(service.update(form)){
	   		return R.ok(1);
	   	}else {
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_UPD_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.RebateConfig_UPD_ERR.getMsg());
		}
        
    }
    
    @Autowired
	private RebateConfigServiceImpl service;
}
