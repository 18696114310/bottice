package com.ecofresh.bottice.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ecofresh.bottice.order.entities.Order;
import com.ecofresh.bottice.product.entities.Product;

public interface OrderRepository extends MongoRepository<Order, String>
{
	@Query("{?0:?1}")
	public List<Order> find(String key, String value);
	
	/**
	 * 有@Query声明查询, 方法名不需要严格遵守规定
	 * 根据 手机号，和昵称搜索
	 * @param 
	 * @param pageable	分页
	 * @return
	 */
	@Query("{$and: [{'$or':[{$and: [{'usersId':?0},{'ordertype':{'$in':?1}}]},{$and: [{'rebateUserId':?0},{'ordertype':{'$in':?2}}]}]}]}")
	//@Query("{$and: [{'usersId':?0},{'ordertype':{'$in':[0,1,3,5,6]}}]}")
	public Page<Order> find(final String userId,Integer[] usersType,Integer[] rebateUserType, Pageable pageable);
	//按时间与状态
	@Query("{$and: [{'$or':[{$and: [{'usersId':?0},{'ordertype':{'$in':?1}}]},{$and: [{'rebateUserId':?0},{'ordertype':{'$in':?2}}]}]},{'cs':{'$gte':?3}}, {'cs':{'$lte':?4}}]}")
	//@Query("{$and: [{'usersId':?0},{'ordertype':{'$in':[0,1,3,5,6]}}]}")
	public Page<Order> find(final String userId,Integer[] usersType,Integer[] rebateUserType,Long startTime,Long endTime, Pageable pageable);
	//只按时间的时候查询所有条件
/*	@Query("{$and: [{'$or':[{$and: [{'usersId':?0},{'ordertype':{'$in':?1}}]},{$and: [{'rebateUserId':?0},{'ordertype':{'$in':?2}}]}]},"
			+ "{'cs':{'$gte':?2}}, {'cs':{'$lte':?3}}]}")
	//@Query("{$and: [{'usersId':?0},{'ordertype':{'$in':[0,1,3,5,6]}}]}")
	public Page<Order> find(final String userId,Integer[] usersType,Integer[] rebateUserType,Long startTime,Long endTime, Pageable pageable);
	*/
	
	/**
	 * 当日积分查询
	 */
	@Query("{$and: [{'ordertype':?0}, {'userId':{'$eq':?1}},{'cs':{'$gte':?2}}, {'cs':{'$lte':?3}}]}")
	public List<Order> finds(Integer ordertype,String userId,Long startDate,Long endTime);
	
}
