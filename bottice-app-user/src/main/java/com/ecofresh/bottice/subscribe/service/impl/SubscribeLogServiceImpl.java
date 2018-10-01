package com.ecofresh.bottice.subscribe.service.impl;

import com.ecofresh.bottice.subscribe.entities.SubscribeLog;
import com.ecofresh.bottice.subscribe.repository.SubscribeLogRepository;
import com.ecofresh.bottice.subscribe.service.SubscribeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscribeLogServiceImpl implements SubscribeLogService {

    @Autowired
    private SubscribeLogRepository subscribeLogRepository;

    @Override
    public SubscribeLog insert(SubscribeLog subscribeLog){
          return subscribeLogRepository.insert(subscribeLog);
    }
}
