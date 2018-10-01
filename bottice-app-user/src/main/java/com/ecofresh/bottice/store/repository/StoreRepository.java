package com.ecofresh.bottice.store.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ecofresh.bottice.store.entities.Store;

public interface StoreRepository extends MongoRepository<Store, String>
{
	@Query("{?0:?1}")
	public List<Store> find(String key, String value);
	
	/**
	 * 有@Query声明查询, 方法名不需要严格遵守规定
	 * 根据 手机号，和昵称搜索
	 * @param storeName 门店名称
	 * @param pageable	分页
	 * @return
	 */
	@Query("{$and: [{'$or':[{'phone':?0}, {'state':{'$eq':?1}}]}]}")
	public Page<Store> find(final String storeName,String state, Pageable pageable);

	/***
	 * 根据状态查询所有未删除的门店
	 * @param store
	 */
	@Query("{$and: [{'state':1}, {'state':{'$eq':?0}}]}")
	public List<Store> findAll(Integer status);
	
}
