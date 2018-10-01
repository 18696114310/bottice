package com.ecofresh.bottice.rebateconfig.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ecofresh.bottice.rebateconfig.entities.RebateConfig;
import com.ecofresh.bottice.store.entities.Store;

public interface RebateConfigRepository extends MongoRepository<RebateConfig, String>
{
	@Query("{?0:?1}")
	public List<RebateConfig> find(String key, String value);
	
	/**
	 * 有@Query声明查询, 方法名不需要严格遵守规定
	 * 根据 手机号，和昵称搜索
	 * @param 
	 * @param pageable	分页
	 * @return
	 */
	@Query("{$and: [{'$or':[{'state':{'$eq':?1}}]}]}")
	public Page<RebateConfig> find(final String state, Pageable pageable);
	
}
