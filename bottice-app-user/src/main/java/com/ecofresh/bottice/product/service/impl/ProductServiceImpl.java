package com.ecofresh.bottice.product.service.impl;

import java.util.Collection;
import java.util.HashMap;
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

import com.ecofresh.bottice.product.entities.Product;
import com.ecofresh.bottice.product.form.ProductForm;
import com.ecofresh.bottice.product.repository.ProductRepository;
import com.ecofresh.bottice.product.service.ProductService;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.core.mongo.utils.MongoUtils;
import com.mongodb.client.result.UpdateResult;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class ProductServiceImpl implements ProductService
{
	public Collection<Product> find(int page, int row, final String productName)
    {
		if(1 > page) page = 1;
		if(1 > row) row = 1;
		
    	if (StrUtil.isBlank(productName)) return null;
		PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		Page<Product> entities = repository.find(productName,"1", pageRequest);
		
		if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
		{
			return entities.getContent();
		}
		
		return null;
    }
	
	@Override
	public Page<Product> find(int page, int row, Product dto)
	{
		PageRequest pageable = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
														  .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
														  .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withIgnorePaths("loc", "score");  //忽略属性：是否关注。因为是基本类型，需要忽略掉;
		Example<Product> example = Example.of(dto, matcher);
		return repository.findAll(example, pageable);
	}
	
	@Override
	@Transactional
	public boolean delete(String... ids) {
		if (ArrayUtil.isEmpty(ids)) return false;
		
		for(String id : ids)
		{
			if (StrUtil.isBlank(id)) continue;
			
			mongoTemplate.remove(new Query(Criteria.where("id").is(new ObjectId(id))), Product.class);
		}
		
		return true;
	}

	@Override
	@Transactional
	public Product insert(ProductForm dto) {
			Product product = new Product();
			BeanUtil.copyProperties(dto, product);
			product.setState(1);
			product.setCs(SystemClock.now()); 							//创建时间
			product.setMs(SystemClock.now()); 							//修改时间
			return repository.save(product);
	}
	
	@Override
	@Transactional
	public boolean delById(Product product) {
		product.setState(0);
		product.setMs(SystemClock.now());
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(product, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> stores = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) 
    	{
    		if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
    		stores.put( k, dtos.get(k));
    	}
    	//修改产品状态
    	if(CollectionUtil.isNotEmpty(stores))
        {
    		UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(product.getId()))), MongoUtils.mongoUpdate(product), Product.class);
        }
		
		return true;
	}
	
	@Override
	@Transactional
	public boolean update(ProductForm dto) {
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(dto, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> products = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) 
    	{
    		if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
    		products.put( k, dtos.get(k));
    	}
    	//修改门店信息
    	if(CollectionUtil.isNotEmpty(products))
        {
    		UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(dto.getId()))), MongoUtils.mongoUpdate(products), Product.class);
    		if(null == ret || ret.getModifiedCount() < 1) return false;
    		return true;
        }
    	return false;
	}

	@Override
	public Product findById(String id) {
		return repository.findById(id).orElse(null);
	}

    @Autowired
    private ProductRepository repository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
}
