package com.ecofresh.bottice.store.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ecofresh.bottice.store.entities.StorePrice;

public interface StorePriceRepository extends MongoRepository<StorePrice, String>
{
	@Query("{?0:?1}")
	public List<StorePrice> find(String key, String value);
	
	/**
	 * 有@Query声明查询, 方法名不需要严格遵守规定
	 * 根据 手机号，和昵称搜索
	 * @param phone 	手机号
	 * @param nickname	昵称
	 * @param pageable	分页
	 * @return
	 */
    @Query("{$and: [{'$or':[{'phone':?0},{'Privacy.name':?0},{'cardNo':?0} ]},{'userType':?1},{'state':1}]}")
	public Page<StorePrice> find(final String phone,final String userType, Pageable pageable);
}
