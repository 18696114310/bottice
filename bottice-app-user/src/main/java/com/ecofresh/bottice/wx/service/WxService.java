package com.ecofresh.bottice.wx.service;

import cn.binarywang.wx.miniapp.api.WxMaService;

public interface WxService
{
	public String getPage();
	
	/***
     * @param id
     * @return
     */
	public WxMaService getWxService();
}
