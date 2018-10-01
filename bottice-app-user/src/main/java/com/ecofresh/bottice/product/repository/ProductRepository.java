package com.ecofresh.bottice.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ecofresh.bottice.product.entities.Product;

public interface ProductRepository extends MongoRepository<Product, String>
{
	@Query("{?0:?1}")
	public List<Product> find(String key, String value);
	
	/**
	 * 有@Query声明查询, 方法名不需要严格遵守规定
	 * 根据 手机号，和昵称搜索
	 * @param storeName 门店名称
	 * @param pageable	分页
	 * @return
	 */
	@Query("{$and: [{'$or':[{'productName':?0}, {'state':{'$eq':?1}}]}]}")
	public Page<Product> find(final String productName,String state, Pageable pageable);
}
