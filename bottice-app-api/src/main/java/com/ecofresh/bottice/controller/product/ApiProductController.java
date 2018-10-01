package com.ecofresh.bottice.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.product.entities.Product;
import com.ecofresh.bottice.product.form.ProductForm;
import com.ecofresh.bottice.product.service.impl.ProductServiceImpl;
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
 * 用户信息
 * @author zzy
 * @email 
 * @date 2018-06-14 12:00
 */
@RestController
@RequestMapping("product")
@Api("产品信息接口")
public class ApiProductController extends ApiAbstractController
{
	/***
	 * 获取产品信息
	 * @return
	 */
    @GetMapping("getProductInfo")
    @ApiOperation(value = "获取产品信息", notes = "获取产品信息说明")
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
        Product product = new Product();
        if(null != key){
        	product.setProductName(key);
        }
        product.setState(1);
        return R.ok(1).put("profile", service.find(page,row,product));
    }
    
    /***
	 * 添加产品
	 * @param userId
	 * @return
	 */
    @PutMapping("addProductInfo")
    @ApiOperation(value = "添加产品信息", notes = "添加产品说明")
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody ProductForm form)
    {
       //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_ID_EMPTY.getMsg());
    	}
	     //参数验证
	   	ValidatorUtils.validateEntity(form, UpdateGroup.class);
	   	form.setUserId(sessionId);
        return R.ok(1).put("product", service.insert(form));
    }
    /**
     * 删除产品(假删除)
     */
    @GetMapping("delProductInfo")
    @ApiOperation(value = "删除产品信息", notes = "删除产品信息说明")
    public R delate(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,String id)
    {
    	//判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
       if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
        Product product = new Product();
        if(null == id  || null == (product=service.findById(id))){
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_EMPTY.getMsg());
        }
        product.setMobUserId(sessionId);
        service.delById(product);
        return R.ok(1);
    }
    /***
	 * 修改产品信息
	 * @param userId
	 * @return
	 */
    @PutMapping("updateProductInfo")
    @ApiOperation(value = "修改产品信息", notes = "修改产品说明")
    public R updateProductInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody ProductForm form)
    {
       //判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
       if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_EMPTY.getMsg());
    	}
	     //参数验证
       form.setMobUserId(sessionId);
	   	if(service.update(form)){
	   		return R.ok(1);
	   	}else {
	   		return R.error(com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_UPD_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_UPD_ERR.getMsg());
		}
        
    }
    
    /***
	 * 查看产品信息
	 * @param userId
	 * @return
	 */
    @PutMapping("showProductInfo")
    @ApiOperation(value = "查看产品信息", notes = "查看产品信息说明")
    public R showProductInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token, @RequestBody ProductForm form)
    {
       //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
       if(null == form)
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_ID_EMPTY.getMsg());
    	}
        return R.ok(1).put("product", service.findById(form.getId()));
    }
    
    @Autowired
	private ProductServiceImpl service;
}
