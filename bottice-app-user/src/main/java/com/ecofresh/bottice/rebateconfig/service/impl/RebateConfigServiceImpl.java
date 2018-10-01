package com.ecofresh.bottice.rebateconfig.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecofresh.bottice.rebateconfig.entities.RebateConfig;
import com.ecofresh.bottice.rebateconfig.form.RebateConfigForm;
import com.ecofresh.bottice.rebateconfig.repository.RebateConfigRepository;
import com.ecofresh.bottice.rebateconfig.service.RebateConfigService;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.core.mongo.utils.MongoUtils;
import com.mongodb.client.result.UpdateResult;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class RebateConfigServiceImpl implements RebateConfigService
{
	
	@Override
	public Page<RebateConfig> find(int page, int row, RebateConfig dto)
	{
		PageRequest pageable = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
														  .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
														  //.withMatcher("storeName", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withIgnorePaths("loc", "score");  //忽略属性：是否关注。因为是基本类型，需要忽略掉;
		Example<RebateConfig> example = Example.of(dto, matcher);
		return repository.findAll(example, pageable);
	}
	
	@Override
	public List<RebateConfig> findAll() {
		return repository.find("state","1");
	}
	
	@Override
	@Transactional
	public boolean delete(String... ids) {
		if (ArrayUtil.isEmpty(ids)) return false;
		
		for(String id : ids)
		{
			if (StrUtil.isBlank(id)) continue;
			
			mongoTemplate.remove(new Query(Criteria.where("id").is(new ObjectId(id))), Store.class);
		}
		
		return true;
	}

	@Override
	@Transactional
	public RebateConfig insert(RebateConfigForm dto) {
			RebateConfig rebateConfig = new RebateConfig();
			BeanUtil.copyProperties(dto, rebateConfig);
			rebateConfig.setState(1);
			rebateConfig.setCs(SystemClock.now()); 							//创建时间
			rebateConfig.setMs(SystemClock.now()); 							//修改时间
			return repository.save(rebateConfig);
	}
	
	@Override
	@Transactional
	public boolean delById(RebateConfig rebateConfig) {
		rebateConfig.setState(0);
		rebateConfig.setMs(SystemClock.now());
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(rebateConfig, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> rebateConfigs = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) 
    	{
    		if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
    		rebateConfigs.put( k, dtos.get(k));
    	}
    	//修改门店状态
    	if(CollectionUtil.isNotEmpty(rebateConfigs))
        {
    		UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(rebateConfig.getId()))), MongoUtils.mongoUpdate(rebateConfigs), RebateConfig.class);
        }
		
		return true;
	}
	
	@Override
	@Transactional
	public boolean update(RebateConfigForm dto) {
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(dto, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> stores = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) 
    	{
    		if(k.equals("status")){
    			stores.put( k, Integer.parseInt(dtos.get(k).toString()));
    		}else {
    			if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
        		stores.put( k, dtos.get(k));
			}
    	}
    	//修改门店信息
    	if(CollectionUtil.isNotEmpty(stores))
        {
    		UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(dto.getId()))), MongoUtils.mongoUpdate(stores), RebateConfig.class);
    		if(null == ret || ret.getModifiedCount() < 1) return false;
    		return true;
        }
    	return false;
	}
	
	@Override
	public RebateConfig findById(String id) {
		return repository.findById(id).orElse(null);
	}

    @Autowired
    private RebateConfigRepository repository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
}
