package com.ecofresh.bottice.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ecofresh.common.annotation.IgnoreAuth;
import com.ecofresh.common.exception.ErrorDesc;
import com.ecofresh.common.exception.RRException;
import com.ecofresh.security.SessionKeys;
import com.ecofresh.security.SessionObject;

import cn.hutool.core.util.StrUtil;

/**
 * 权限(Token)验证
 * @author sunhongwei
 * @email sunhongwei@jshijiain.com
 * @date 2017-03-23 15:38
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        IgnoreAuth annotation = null;

        if(handler instanceof HandlerMethod) 
        {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreAuth.class);
        }

        //如果有@IgnoreAuth注解，则不验证Token
        if(annotation != null)
        {
            return true;
        }

        //从header中获取Token
        String token = request.getHeader(SessionKeys.TOKEN.toString());
        //token为空
        if(StrUtil.isBlank(token))
        {
            throw new RRException(ErrorDesc.TOKEN_EMPTY.getMsg(), ErrorDesc.TOKEN_EMPTY.getCode());
        }
        //从header中获取Token
        String secret = request.getHeader(SessionKeys.SECRET.toString());
        //secret为空
        if(StrUtil.isBlank(secret))
        {
            throw new RRException(ErrorDesc.SECRET_EMPTY.getMsg(), ErrorDesc.SECRET_EMPTY.getCode());
        }
        //token不存在
        SessionObject session = null;
        if(null == (session = com.ecofresh.security.SessionTokenProviderFactory.getSessionTokenProvider().getCurrent(secret, token)))
        {
            throw new RRException(ErrorDesc.SESSION_EMPTY.getMsg(), ErrorDesc.SESSION_EMPTY.getCode());
        }
        //更新token
        com.ecofresh.security.SessionTokenProviderFactory.getSessionTokenProvider().updateSession(secret, token, session);
        return true;
    }
}
