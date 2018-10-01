package com.ecofresh.bottice.subscribe.service;

import com.ecofresh.bottice.subscribe.entities.SubscribeLog;

public interface SubscribeLogService {

    /***
     * 增加预约日志
     * @param dto
     * @return
     */
    public SubscribeLog insert(SubscribeLog dto);
}
