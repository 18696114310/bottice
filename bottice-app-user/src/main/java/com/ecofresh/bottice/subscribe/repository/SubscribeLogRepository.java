package com.ecofresh.bottice.subscribe.repository;

import com.ecofresh.bottice.subscribe.entities.SubscribeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscribeLogRepository extends MongoRepository<SubscribeLog, String> {
}
