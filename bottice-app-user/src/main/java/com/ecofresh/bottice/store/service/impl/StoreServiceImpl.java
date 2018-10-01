package com.ecofresh.bottice.store.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.store.entities.StorePrice;
import com.ecofresh.bottice.store.form.ShowStoreIntegralForm;
import com.ecofresh.bottice.store.form.StoreForm;
import com.ecofresh.bottice.store.repository.StoreRepository;
import com.ecofresh.bottice.store.service.StoreService;
import com.ecofresh.bottice.util.DateUtils;
import com.ecofresh.core.mongo.utils.MongoUtils;
import com.mongodb.client.result.UpdateResult;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class StoreServiceImpl implements StoreService
{

	public Collection<Store> find(int page, int row, final String storeName)
    {
		if(1 > page) page = 1;
		if(1 > row) row = 1;
		
    	if (StrUtil.isBlank(storeName)) return null;
		PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		Page<Store> entities = repository.find(storeName,"1", pageRequest);
		
		if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
		{
			return entities.getContent();
		}
		
		return null;
    }
	
	@Override
	public Page<Store> find(int page, int row, Store dto)
	{
		PageRequest pageable = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "cs"));
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
														  .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
														  .withMatcher("storeName", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withIgnorePaths("loc", "score");  //忽略属性：是否关注。因为是基本类型，需要忽略掉;
		Example<Store> example = Example.of(dto, matcher);
		return repository.findAll(example, pageable);
	}
	
	@Override
	@Transactional
	public boolean delete(String... ids) {
		if (ArrayUtil.isEmpty(ids)) return false;
		
		for(String id : ids)
		{
			if (StrUtil.isBlank(id)) continue;
			
			mongoTemplate.remove(new Query(Criteria.where("id").is(new ObjectId(id))), Store.class);
			//删除门店缓存信息
        	this.delCacheById(id);
		}
		
		return true;
	}

	@Override
	@Transactional
	public Store insert(StoreForm dto) {
			Store store = new Store();
			BeanUtil.copyProperties(dto, store);
			store.setState(1);
			store.setCs(SystemClock.now()); 							//创建时间
			store.setMs(SystemClock.now()); 							//修改时间
			Store.Lbs lbs=store.new Lbs();
			lbs.setLat(dto.getLat());
			lbs.setLng(dto.getLng());
			store.setLbs(lbs);
			return repository.save(store);
	}
	
	@Override
	@Transactional
	public boolean delById(Store store) {
		store.setState(0);
		store.setMs(SystemClock.now());
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(store, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> stores = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) 
    	{
    		if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
    		stores.put( k, dtos.get(k));
    	}
    	//修改门店状态
    	if(CollectionUtil.isNotEmpty(stores))
        {
    		UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(store.getId()))), MongoUtils.mongoUpdate(store), Store.class);
    		//删除门店缓存信息
        	this.delCacheById(store.getId());
        }
		
		return true;
	}
	
	@Override
	@Transactional
	public boolean update(StoreForm dto) {
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(dto, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> stores = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) 
    	{
    		if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
    		if(k.equals("lng")){
    			stores.put( "Lbs."+k, dtos.get(k));
    		}else if (k.equals("lat")) {
    			stores.put( "Lbs."+k, dtos.get(k));
			}else {
				stores.put( k, dtos.get(k));
			}
    		
    	}
    	//删除门店缓存信息
    	this.delCacheById(dto.getId());
    	//修改门店信息
    	if(CollectionUtil.isNotEmpty(stores))
        {
    		UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(dto.getId()))), MongoUtils.mongoUpdate(stores), Store.class);
    		
    		if(null == ret || ret.getModifiedCount() < 1) return false;
    		return true;
        }
    	return false;
	}

	@Override
	public Store findById(String id) {
		return repository.findById(id).orElse(null);
	}
	
	@Override
	public List<Store> findAll() {
		List<Store> list = repository.findAll(1);
		return list;
	}
	/***
     * 根据门店id查询所查门店报表信息  管理员可以查看所有门店的信息
     */
	@Override
	public List<StorePrice> findStoreOrderById(ShowStoreIntegralForm form) {
		//店员与管理员都有默认的店铺   管理员可以查看别的店铺  
		//过滤掉类型等于0的数据
		String val ="";
		Integer orderType=Integer.parseInt(form.getOrdertype());
		if(orderType==Constant.User.RECHARGE){
			val="rechargeIntegral";
		}else if (orderType==Constant.User.REDUCE_DAILY_CONSUMPTION) {
			val="dayConsumptionIntegral";
		}else if (orderType==Constant.User.REDUCE_HEALTHY_CONSUMPTION) {
			val="healthyIntegral";
		}else if (orderType==Constant.User.REFUND) {
			val="refundIntegral";
		}else if (orderType==Constant.User.RETURN_INTEGRAL) {
			val="returnIntegral";
		}
		//展示方式 0：最近30天，1：本月；2，上月；3上周
		//得到展示方式的天数
		List<Date> list = null;
		List<Long> l = new ArrayList<Long>();
		List<StorePrice> storePriceList = null;
		if(form.getState().equals("0")){
			list = DateUtils.getNowTimeSlot(30);
			for(int i=0;i<list.size();i++){
				l.add(DateUtils.getDate(list.get(i),1));
			}
			storePriceList = mongoTemplate.find(new Query(Criteria.where("storeid").is(form.getStoreId()).
					and("cs").in(l).and(val).gt(0)).with(new Sort(Sort.Direction.DESC, "cs")), StorePrice.class);
		}else if(form.getState().equals("1")){
			try {
				list = DateUtils.findDates(sdf.parse(DateUtils.getPastDate()),new Date());
				for(int i=0;i<list.size()-1;i++){
					l.add(DateUtils.getDate(list.get(i),1));
				}
				storePriceList = mongoTemplate.find(new Query(Criteria.where("storeid").is(form.getStoreId()).
						and("cs").in(l).and(val).gt(0)).with(new Sort(Sort.Direction.DESC, "cs")), StorePrice.class);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(form.getState().equals("2")){
			try {
				list = DateUtils.findDates(sdf.parse(DateUtils.getBeginDayOfLastMonth()),sdf.parse(DateUtils.getEndDayOfLastMonth()));
				for(int i=0;i<list.size();i++){
					l.add(DateUtils.getDate(list.get(i),1));
				}
				storePriceList = mongoTemplate.find(new Query(Criteria.where("storeid").is(form.getStoreId()).
						and("cs").in(l).and(val).gt(0)).with(new Sort(Sort.Direction.DESC, "cs")), StorePrice.class);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		}else if(form.getState().equals("3")){
			String[] a = DateUtils.getLastTimeInterval().split(","); 
			try {
				list = DateUtils.findDates(sdf.parse(a[0]), sdf.parse(a[1]));
				for(int i=0;i<list.size();i++){
					l.add(DateUtils.getDate(list.get(i),1));
				}
				storePriceList = mongoTemplate.find(new Query(Criteria.where("storeid").is(form.getStoreId()).
						and("cs").in(l).and(val).gt(0)).with(new Sort(Sort.Direction.DESC, "cs")), StorePrice.class);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return storePriceList;
	}
	
	/**
	 * key店铺ID  为null则是默认第一次查询   不为null则是管理员查看别的门店信息
	 */
	@Override
	public StorePrice find(String userId,String storeid) {
		//店员与管理员都有默认的店铺   管理员可以查看别的店铺  
		Long nowDate = DateUtils.getDate(new Date(),1);//获取当天的开始时间
		return mongoTemplate.findOne(new Query(Criteria.where("storeid").is(storeid).and("cs").is(nowDate)), StorePrice.class);
	}
	/**
	 * 根据id查询缓存信息   如果没有则添加  
	 * @param id
	 * @return
	 */
	@Override
	public Store findCacheById(String id)
	{
		if (StrUtil.isBlank(id)) return null;
		
		//此处先查找缓存，缓存没有在查询数据库
		String key = Constant.STORE_KEY;
		HashOperations<String, String, String> operations = redisTemplate.opsForHash();
		String cache = operations.get(key, id);
		
		if(StrUtil.isNotBlank(cache))
		{
			return JSON.parseObject(cache, Store.class);
		}

		Store store = this.findById(id);
		if(null != store && StrUtil.isNotBlank(store.getId()))
		{
			String json = JSON.toJSONString(store);
			if(StrUtil.isBlank(json)) return null;
			operations.put(key, id, json);
			return store;
		}
		
		return null;
	}
	
	@Override
	public void delCacheById(String id) 
	{
		if (StrUtil.isBlank(id)) return;
		//此处先查找缓存，缓存没有在查询数据库
		redisTemplate.opsForHash().delete(Constant.STORE_KEY, id);
	}
	
    @Autowired
    private StoreRepository repository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  

}
