package com.ecofresh.bottice.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.ecofresh.bottice.interceptor.AuthorizationInterceptor;

/**
 * MVC配置
 * @author sunhongwei
 * @email sunhongwei@jshijian.com
 * @date 2017-04-20 22:30
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport 
{
    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) 
    {
    	List<String> patterns = new ArrayList<String>();
    	patterns.add("/document/**");     	//注册登录相关
    	patterns.add("/user/**");     	  	//用户相关
        patterns.add("/subscribe/**");
        patterns.add("/order/**");
        patterns.add("/product/**");
        patterns.add("/rebateConfig/**");
        patterns.add("/store/**");
        registry.addInterceptor(authorizationInterceptor).addPathPatterns(patterns.toArray(new String[patterns.size()]));
    }
    
    /**
     * 这个地方要重新注入一下资源文件，不然不会注入资源的，也没有注入requestHandlerMappping,相当于xml配置的
     *  <!--swagger资源配置-->
     *  <mvc:resources location="classpath:/META-INF/resources/" mapping="swagger-ui.html"/>
     *  <mvc:resources location="classpath:/META-INF/resources/webjars/" mapping="/webjars/**"/>
     *  不知道为什么，这也是spring boot的一个缺点（菜鸟觉得的）
     * @param registry
     */
     @Override
     public void addResourceHandlers(ResourceHandlerRegistry registry) 
     {
         registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
         registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
     }
}
