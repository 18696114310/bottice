package com.ecofresh.bottice.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.user.form.BackStatageUserForm;
import com.ecofresh.bottice.user.form.UpdateUserPwdForm;
import com.ecofresh.bottice.user.service.UserService;
import com.ecofresh.common.annotation.IgnoreAuth;
import com.ecofresh.common.exception.ErrorDesc;
import com.ecofresh.common.utils.R;
import com.ecofresh.common.validator.ValidatorUtils;
import com.ecofresh.common.validator.group.UpdateGroup;
import com.ecofresh.security.SessionTokenProviderFactory;
import com.ecofresh.utils.Util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 用户信息
 * @author sunhongwei
 * @email sunhongwei@jshijian.com
 * @date 2017-03-26 17:27
 */
@RestController
@RequestMapping("user")
@Api("用户信息接口")
public class ApiUserController extends ApiAbstractController
{
	
	/***
	 * 会员管理分页获取用户信息
	 * @return
	 */
    @GetMapping("getUserInfo")
    @ApiOperation(value = "分页获取用户信息", notes = "分页获取用户信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="String",  name = "key",             value = "关键词", required = false)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,Integer page ,Integer row , String key)
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
        return R.ok(1).put("profile", service.find(page,row,key));
    }
	
	/***
	 * 获取用户信息
	 * @return
	 */
    @GetMapping("profile")
    @ApiOperation(value = "获取当前用戶信息", notes = "获取用戶信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token)
    {
    	//判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
        
        User user = service.findCacheById(sessionId);
        if(null !=user && null != user.getPid()){//表示有推荐人   将推荐人信息加入
        	User users =service.findCacheById(user.getPid());
        	if(null != users){
        		Map<String,Object> map = new HashMap<String,Object>();
        		map.put("pUserName", users.getPrivacy().getName());
        		map.put("pPhone", users.getPhone());
        		user.setPUserInfo(map);
        	}
        }
        return R.ok(1).put("profile", user);
    }

    /**
     * 统计用户日常消费
     * @param secret
     * @param token
     * @param page
     * @param row
     * @param phone
     * @param name
     * @return
     */
    @GetMapping("mapReduceInfo")
    @ApiOperation(value = "获取当前日常统计消费信息", notes = "获取当前日常统计消费信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",  name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
            @ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true),
            @ApiImplicitParam(paramType = "query",  dataType="String",  name = "key",             value = "关键字", required = false)
    })
    public R mapReduceInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,Integer page ,Integer row , String key){
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
/*        UserCountPrice userCountPrice=new UserCountPrice();
        if(name!=null&&!"".equals(name)){
            userCountPrice.setName(name);
        }
        if(phone!=null&&!"".equals(phone)){
            userCountPrice.setPhone(phone);
        }*/
        return R.ok(1).put("profile", service.mapReduceInfo(page,row,key));
    }
    
    /***
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
    @GetMapping("{userId}")
    @ApiOperation(value = "获取其他用戶信息", notes = "获取用戶信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "path", dataType="String",  name = "userId",  value = "用户编码", required = true)
    })
    public R profile(@PathVariable("userId")String userId)
    {
        if(StrUtil.isBlank(userId))
    	{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.USER_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.USER_ID_EMPTY.getMsg());
    	}
        User user = service.findCacheById(userId);
        if(null !=user && null != user.getPid()){//表示有推荐人   将推荐人信息加入
        	User users =service.findCacheById(user.getPid());
        	if(null != users){
        		Map<String,Object> map = new HashMap<String,Object>();
        		map.put("pUserName", users.getPrivacy().getName());
        		map.put("pPhone", users.getPhone());
        		user.setPUserInfo(map);
        	}
        }
        return R.ok(1).put("profile", user);
    }

    /**
     * 获取管理后台用户信息
     *  @param name,phone
     *  @return
     */
    @GetMapping("getBackStageUserInfo")
    @ApiOperation(value = "获取后台用户信息", notes = "获取后台用户信息")
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
        /*User user = new User();
        User.Privacy  privacy  =  user.new Privacy();
        //根据KEY的值 来判断是输入的姓名还是手机号
        if (!StrUtil.isBlank(key))
        {
            if (Validator.isMobile(key))
                user.setPhone(key);
            else {
                privacy.setName(key);
                user.setPrivacy(privacy);
            }
        }*/
        return R.ok(1).put("profile", service.findManager(page,row,key));
    }


    /***
	 * 完善资料
	 * @param dto
	 * @return
	 */
    @PutMapping("profile")
    @ApiOperation(value = "修改用戶信息", notes = "修改用戶信息说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
        @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token, @RequestBody BackStatageUserForm form)
    {
    	//判断Session 是否存在
       String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
    	{
        	return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
    	}
        if(form == null || null==form.getId()){
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.USER_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.USER_ID_EMPTY.getMsg());
        }
        if(form.getState()==Constant.User.DEL&&sessionId.equals(form.getId())){
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.NODEL_CURRENT_USER.getCode(), com.ecofresh.bottice.enums.ErrorDesc.NODEL_CURRENT_USER.getMsg());
        }
        service.updateById(sessionId, form.getId(), form.getName(), form.getGender(), form.getBirthday(), form.getPhone(), form.getStoreid(), form.getUserType(), form.getHiredate(),form.getState(),form.getRole());
       
        return R.ok(1);
    }


    /***
     * 修改密码
     * @param dto
     * @return
     */
    @PutMapping("modifyPwd")
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="String",  name = "acceptType",             value = "修改密码入口类型", required = false)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token, @RequestBody UpdateUserPwdForm form, String acceptType)
    {
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }

        ValidatorUtils.validateEntity(form, UpdateGroup.class);

        //将对象转为Map
        User user = null;
        if(form.getNewpwd().length()< Constant.User.USER_PWD_LIMIT){
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.PSW_ILLEGAL.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PSW_ILLEGAL.getMsg());
        }
        Map<String, Object> dtos = new HashMap<String, Object>();
        user = service.findById(sessionId);
        if (form.getAcceptType().equals("0"))//个人信息入口进来的要验证旧密码
        {
            if(!sessionId.equals(form.getUserId())){
                return R.error(com.ecofresh.bottice.enums.ErrorDesc.IS_NOTNOW_USER.getCode(), com.ecofresh.bottice.enums.ErrorDesc.IS_NOTNOW_USER.getMsg());
            }
            if (user.getPrivacy().getPwd()!=null&&!user.getPrivacy().getPwd().equals(Util.SHA256(form.getOldpwd()))) {
                return R.error(com.ecofresh.bottice.enums.ErrorDesc.OLD_PWD_ERROR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.OLD_PWD_ERROR.getMsg());
			}
        }
        dtos.put("privacy.pwd", Util.SHA256(form.getNewpwd()));
        service.upById(form.getUserId(), dtos);

        return R.ok(1);
    }


    /**
     * 删除用户
     */
    @GetMapping("delUserInfo")
    @ApiOperation(value = "删除用户信息", notes = "删除用户信息说明")
    public R delate(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,String id) {
//        判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if (StrUtil.isBlank(sessionId)) {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        User user = new User();

        if(null == id  || null == (user=service.findCacheById(id))){
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PRODUCT_EMPTY.getMsg());
        }
        if(id.equals(sessionId)){
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.NODEL_CURRENT_USER.getCode(), com.ecofresh.bottice.enums.ErrorDesc.NODEL_CURRENT_USER.getMsg());

        }
       service.delete(id);
        return R.ok(1);

    }
    @Autowired
   	private UserService service;
}
