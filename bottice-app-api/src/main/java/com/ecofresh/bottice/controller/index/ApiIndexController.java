package com.ecofresh.bottice.controller.index;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.user.form.BackStatageUserForm;
import com.ecofresh.bottice.user.form.UserForm;
import com.ecofresh.bottice.user.service.UserService;
import com.ecofresh.bottice.store.service.StoreService;
import com.ecofresh.bottice.wx.service.WxService;
import com.ecofresh.common.annotation.IgnoreAuth;
import com.ecofresh.common.exception.ErrorDesc;
import com.ecofresh.common.exception.RRException;
import com.ecofresh.common.utils.R;
import com.ecofresh.common.validator.Assert;
import com.ecofresh.common.validator.ValidatorUtils;
import com.ecofresh.common.validator.group.UpdateGroup;
import com.ecofresh.enums.Status;
import com.ecofresh.modules.oss.cloud.OSSFactory;
import com.ecofresh.modules.oss.entity.OssEntity;
import com.ecofresh.modules.oss.service.OssService;
import com.ecofresh.security.SessionKeys;
import com.ecofresh.security.SessionObject;
import com.ecofresh.security.SessionTokenProviderFactory;
import com.ecofresh.utils.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册登录
 * @author sunhongwei
 * @email sunhongwei@jshijian.com
 * @date 2017-03-26 17:27
 */
@RestController
@RequestMapping("document")
@Api("注册登录接口")
public class ApiIndexController extends ApiAbstractController
{
    /**
     * 注册
     */
    @IgnoreAuth
    @PostMapping("register")
    @ApiOperation(value = "注册",notes = "注册说明")
    public R register(@RequestBody UserForm form)
    {
    	if(null == form)
    	{
        	return R.error(ErrorDesc.PARAM_EMPTY.getCode(), ErrorDesc.PARAM_EMPTY.getMsg());
    	}

    	//参数验证
    	ValidatorUtils.validateEntity(form, UpdateGroup.class);
        
    	User dto = null;
        //判断手机是否注册
        if(null != (dto = service.findByPhone(form.getPhone()))&&dto.getUserType().intValue()==Constant.User.USER_TYPE_WEB.intValue())
        {
        	return R.error(ErrorDesc.PHONE_REG.getCode(), ErrorDesc.PHONE_REG.getMsg()).put("data", dto.getId());
        }

        if(null == (dto = service.insert(form.getName(), form.getPhone(), form.getPwd(), form.getUcode(), form.getAvator(), form.getGender(), form.getBirthday(), form.getOpenid())))
        {
        	return R.error(ErrorDesc.REG_ERROR.getCode(), ErrorDesc.REG_ERROR.getMsg());
        }
        
        return R.ok(1).put("data", dto.getId());
    }
	/**
	 * 后台管理注册
	 *
	 */
	@IgnoreAuth
	@PostMapping("backStageRegister")
	@ApiOperation(value = "后台管理注册",notes = "后台管理注册说明")
	public R backStageRegister(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody BackStatageUserForm form) {
		//判断Session 是否存在
		String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
		if (StrUtil.isBlank(sessionId)) {
			return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
		}
		if (null == form) {
			return R.error(ErrorDesc.PARAM_EMPTY.getCode(), ErrorDesc.PARAM_EMPTY.getMsg());
		}

		//参数验证
		ValidatorUtils.validateEntity(form, UpdateGroup.class);
		User dto = null;
		//判断手机是否注册
		if (null != (dto = service.findMangerByPhone(form.getPhone())) && dto.getUserType() == Constant.User.USER_TYPE_MGR && dto.getState() == Constant.User.ACTIVE) {
			return R.error(ErrorDesc.PHONE_REG.getCode(), ErrorDesc.PHONE_REG.getMsg()).put("data", dto.getId());
		} else if (null != (dto = service.findMangerByPhone(form.getPhone())) && dto.getUserType() == Constant.User.USER_TYPE_MGR && dto.getState() == Constant.User.DEL){
			Map<String, Object> formMap = BeanUtil.beanToMap(form, false, true);
			formMap.remove("id");
			service.upByPhone(form.getPhone(), formMap);
			return R.ok(1).put("data", dto.getId());
		}

		if(null == (dto = service.insert(form.getName(), form.getPhone(), form.getPwd(),form.getUcode(), form.getAvator(), form.getGender(), form.getBirthday(), form.getOpenid(),form.getUserType(),form.getStoreid(),form.getHiredate(),form.getRole())))
		{
			return R.error(ErrorDesc.REG_ERROR.getCode(), ErrorDesc.REG_ERROR.getMsg());
		}

		return R.ok(1).put("data", dto.getId());
	}
	/**
     * 微信授权
     */
    @IgnoreAuth
    @GetMapping("wxAuth/{code}")
    @ApiOperation(value = "微信授权",notes = "微信授权说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "path", 	dataType="string", name = "code",  value = "微信编码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="string", name = "uid",   value = "用户标识",  required = false),
    })
    public R wxAuth(@PathVariable("code")String code, HttpServletResponse respose)
    {
        Assert.isBlank(code, com.ecofresh.bottice.enums.ErrorDesc.CODE_EMPTY.getMsg(), com.ecofresh.bottice.enums.ErrorDesc.CODE_EMPTY.getCode());
        User dto = null;
    	WxMaJscode2SessionResult wxSession = null;
        
        try
    	{
        	wxSession = wxService.getWxService().jsCode2SessionInfo(code);
        	if(null == wxSession || StrUtil.isBlank(wxSession.getOpenid()))
        	{
            	return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_AUTH_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_AUTH_ERR.getMsg());
        	}
        	
        	//根据openId查询用户信息
        	if(null != (dto = service.findByWxOpenId(wxSession.getOpenid().trim())) && StrUtil.isNotBlank(dto.getId()))
        	{
            	return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_BING_REP.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_BING_REP.getMsg());
        	}
    	}
    	catch (Exception e)
    	{
    		logger.error("wxAuth err:", e);
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_AUTH_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_AUTH_ERR.getMsg());
		}
        
        return R.ok(1).put("data", wxSession);
    }
    
    /**
     * 微信登陆
     */
    @IgnoreAuth
    @GetMapping("wxLogin/{code}")
    @ApiOperation(value = "微信登录",notes = "微信登录说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "path", 	dataType="string", name = "code",  value = "微信编码",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="string", name = "uid",   value = "用户标识",  required = false),
    })
    public R wxLogin(@PathVariable("code")String code, String uid, HttpServletResponse respose)
    {
        Assert.isBlank(code, com.ecofresh.bottice.enums.ErrorDesc.CODE_EMPTY.getMsg(), com.ecofresh.bottice.enums.ErrorDesc.CODE_EMPTY.getCode());
        try
    	{
        	User dto = null;
        	WxMaJscode2SessionResult wxSession = wxService.getWxService().jsCode2SessionInfo(code);
        	if(null == wxSession || StrUtil.isBlank(wxSession.getOpenid()))
        	{
            	return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_LOGIN_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_LOGIN_ERR.getMsg());
        	}
        	
        	//根据openId查询用户信息
        	if(null == (dto = service.findByWxOpenId(wxSession.getOpenid().trim())) || StrUtil.isBlank(dto.getId()))
        	{
        		//判断用户是否存在
            	if(StrUtil.isNotBlank(uid))
            	{
            		if(null == (dto = service.findCacheById(uid.trim())) || StrUtil.isBlank(dto.getId()))//判断用户是否存在
            		{
                    	return R.error(com.ecofresh.bottice.enums.ErrorDesc.USER_NOT_EXIST.getCode(), com.ecofresh.bottice.enums.ErrorDesc.USER_NOT_EXIST.getMsg());
            		}

                	//绑定用户
            		Map<String, Object> params = new HashMap<String, Object>();
            		params.put("wx.openid", wxSession.getOpenid());
            		params.put("wx.sessionKey", wxSession.getSessionKey());
            		params.put("wx.unionid", wxSession.getUnionid());
            		
            		try 
            		{
						service.upById(uid, params);
					} 
            		catch (DuplicateKeyException e) 
            		{
            			return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_BING_REP.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_BING_REP.getMsg());
					}
            	}
            	else return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_UNBING.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_UNBING.getMsg());
        	}
        	String userId = uid;
        	if(null != dto && StrUtil.isNotBlank(dto.getId()))
        	{
        		userId = dto.getId();
        	}
        	//删除会话
        	String secret = SecureUtil.md5(userId + (com.ecofresh.constant.Constant.PC));
        	SessionTokenProviderFactory.getSessionTokenProvider().removeSession(secret);

            //保存Session会话
            SessionObject session = SessionTokenProviderFactory.getSessionTokenProvider().setSession(userId, null, null, null, false);
            respose.setHeader(SessionKeys.TOKEN.toString(), session.getToken());
            respose.setHeader(SessionKeys.SECRET.toString(), session.getSecret());
    	}
    	catch (Exception e)
    	{
    		logger.error("wxLogin err:", e);
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.WX_LOGIN_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.WX_LOGIN_ERR.getMsg());
		}
        
        return R.ok(1);
    }
    
    /**
     * 系统登陆
     */
    @IgnoreAuth
    @GetMapping("login")
    @ApiOperation(value = "登录",notes = "登录说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "query", 	dataType="string", name = "phone",  value = "手机",  required = true),
    	@ApiImplicitParam(paramType = "query",  dataType="string", name = "pwd",    value = "密码",  required = true),
    })
    public R login(String phone, String pwd, HttpServletResponse respose)
    {
    	Assert.isBlank(phone, ErrorDesc.PHONE_EMPTY.getMsg(), ErrorDesc.PHONE_EMPTY.getCode());
        Assert.isBlank(pwd,   ErrorDesc.PWD_EMPTY.getMsg(),   ErrorDesc.PWD_EMPTY.getCode());
        
        User user = null;
		Store store=null;
        try
    	{
        	if(!Validator.isMobile(phone))
            {
            	return R.error(ErrorDesc.PHONE_ERR.getCode(), ErrorDesc.PHONE_ERR.getMsg());
            }
        	
        	 //根据手机号获取用户信息
            if(null == (user = service.findMangerByPhone(phone)) || StrUtil.isBlank(user.getId()))
            {
            	return R.error(ErrorDesc.PHONE_UNREG.getCode(), ErrorDesc.PHONE_UNREG.getMsg());
            }
            else if(user.getUserType()!=Constant.User.USER_TYPE_MGR)
            {
                return R.error(com.ecofresh.bottice.enums.ErrorDesc.NO_MANAGE_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.NO_MANAGE_ERR.getMsg());
            }
            else if(!user.getPrivacy().getPwd().equalsIgnoreCase(Util.SHA256(pwd)))
            {
            	return R.error(ErrorDesc.PHONE_OR_PWD_REG.getCode(), ErrorDesc.PHONE_OR_PWD_REG.getMsg());
            }
            else if(user.getState() != Constant.User.ACTIVE)
            {
            	return R.error(ErrorDesc.ACC_SUSPEND.getCode(), ErrorDesc.ACC_SUSPEND.getMsg());
            }
			else if(StrUtil.isBlank(user.getStoreid())||(null== (store = storeService.findCacheById(user.getStoreid())))||store.getState()==com.ecofresh.constant.Constant.DEF) {
				return R.error(com.ecofresh.bottice.enums.ErrorDesc.NO_LOGIN_STORESTATE.getCode(), com.ecofresh.bottice.enums.ErrorDesc.NO_LOGIN_STORESTATE.getMsg());
			}
        	//删除会话
        	String secret = SecureUtil.md5(user.getId() + (com.ecofresh.constant.Constant.PC));
        	SessionTokenProviderFactory.getSessionTokenProvider().removeSession(secret);
            //保存Session会话you
            SessionObject session = SessionTokenProviderFactory.getSessionTokenProvider().setSession(user.getId(), null, null, phone, false);
            respose.setHeader(SessionKeys.TOKEN.toString(), session.getToken());
            respose.setHeader(SessionKeys.SECRET.toString(), session.getSecret());
    	}
    	catch (Exception e)
    	{
    		logger.error("login err:", e); 
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.LOGIN_ERR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.LOGIN_ERR.getMsg());
		}
        
        return R.ok(1);
    }
    
    /**
     * 退出
     */
    @DeleteMapping("logout")
    @ApiOperation(value = "退出",notes = "退出说明")
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType = "header", dataType="string", name = "X-EFRESH-SECRET",  value = "密钥",  required = true),
        @ApiImplicitParam(paramType = "header", dataType="string", name = "X-EFRESH-SESSION", value = "会话",  required = true)
    })
    public R logout(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token, HttpServletRequest request)
    {
        Assert.isBlank(secret,  ErrorDesc.SECRET_EMPTY.getMsg(),  ErrorDesc.SECRET_EMPTY.getCode());
        Assert.isBlank(token,  ErrorDesc.TOKEN_EMPTY.getMsg(),  ErrorDesc.TOKEN_EMPTY.getCode());
        
        try
    	{
	        //删除Session会话
	        SessionTokenProviderFactory.getSessionTokenProvider().removeSession(secret, token);
    	}
    	catch (Exception e)
    	{
    		logger.error("logout Exception:", e);
		}
        
        return R.ok(1);
    }
    
    /**
	 * 上传文件
	 */
    @IgnoreAuth
    @PostMapping("upload")
    @ApiOperation(value = "头像上传",notes = "头像上传说明")
	public R upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception 
    {
		if (file.isEmpty()) 
		{
        	return R.error(com.ecofresh.bottice.enums.ErrorDesc.FILE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.FILE_EMPTY.getMsg());
		}
		
		String filename = request.getParameter("fileName");
		if(StrUtil.isBlank(filename))
		{
			filename = file.getOriginalFilename();
		}
		
		//上传文件
		String suffix = null;
		String url = null;
		
		//保存文件信息
		OssEntity ossEntity = new OssEntity();
		ossEntity.setNameOri(filename);
		ossEntity.setLenByte(file.getSize());
		ossEntity.setCs(SystemClock.now());
		ossEntity.setMs(SystemClock.now());
		
		try 
		{
			suffix = filename.substring(filename.lastIndexOf("."));
			ossEntity.setExt(suffix);
			url = OSSFactory.build().uploadSuffix(file.getBytes(), suffix);
			ossEntity.setUrl(url);
			ossEntity.setState(Status.NOR.getType());
			ossService.save(ossEntity);
		} 
		catch (Exception e)
		{
			ossEntity.setState(Status.NOT.getType());
			ossService.save(ossEntity);
			throw new RRException("上传失败", e);
		}
		catch (Error e)
		{
			ossEntity.setState(Status.NOT.getType());
			ossService.save(ossEntity);
			throw new RRException("上传失败", e);
		}
		
		return R.ok().put("data", url);
	}
    
    @Autowired
	private UserService service;
    
    @Autowired
	private WxService wxService;

	@Autowired
	private OssService ossService;

	@Autowired
	private StoreService storeService;
    //日志记录
    private static final Logger logger = LoggerFactory.getLogger(ApiIndexController.class);
}
