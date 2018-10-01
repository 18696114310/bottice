package com.ecofresh.bottice.wx.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ecofresh.bottice.wx.service.WxService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;

@Service
public class WxServiceImpl implements WxService
{
	@Override
	public WxMaService getWxService() 
	{
		this.wxConfig.setAppid(this.appid);
		this.wxConfig.setSecret(this.secret);
		this.wxService.setWxMaConfig(this.wxConfig);
		return this.wxService;
	}
	
	public String getAppid() {
		return appid;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public String getPage() {
		return page;
	}
	
	public WxMaInMemoryConfig getWxConfig() {
		return wxConfig;
	}

	/****
     *应用ID
     */
    @Value("${wx.appid}")
    private String appid;
    
    /****
     *密钥
     */
    @Value("${wx.secret}")
    private String secret;
    
    /****
     *page
     */
    @Value("${wx.page}")
    private String page;
	
	private final WxMaService wxService = new WxMaServiceImpl();
	private final WxMaInMemoryConfig  wxConfig = new WxMaInMemoryConfig();
}
