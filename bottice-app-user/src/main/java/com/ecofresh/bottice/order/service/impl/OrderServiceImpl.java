package com.ecofresh.bottice.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.order.entities.Order;
import com.ecofresh.bottice.order.form.OrderForm;
import com.ecofresh.bottice.order.repository.OrderRepository;
import com.ecofresh.bottice.order.service.OrderService;
import com.ecofresh.bottice.rebateconfig.entities.RebateConfig;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.store.entities.StorePrice;
import com.ecofresh.bottice.store.repository.StorePriceRepository;
import com.ecofresh.bottice.store.service.StoreService;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.user.entities.UserCountPrice;
import com.ecofresh.bottice.user.repository.UserCountPriceRepository;
import com.ecofresh.bottice.user.repository.UserRepository;
import com.ecofresh.bottice.user.service.UserService;
import com.ecofresh.bottice.util.DateUtils;
import com.ecofresh.common.exception.RRException;
import com.ecofresh.core.mongo.utils.MongoUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;

@Service
public class OrderServiceImpl implements OrderService
{
	@Override
	public Page<Order> find(int page, int row, String userId,String startDateTime,String endDateTime,String orderType) {
		if(1 > page) page = 1;
		if(1 > row) row = 1;
		Page<Order> entities = null;
    	if (StrUtil.isBlank(userId)) return null;
		PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "cs"));
		if(!StrUtil.isBlank(startDateTime) && !StrUtil.isBlank(endDateTime) && StrUtil.isBlank(orderType)){//只按时间查询
			Integer[] usersType={Constant.User.RECHARGE,Constant.User.REDUCE_DAILY_CONSUMPTION,
	                Constant.User.REDUCE_HEALTHY_CONSUMPTION,Constant.User.REFUND,Constant.User.RETURN_INTEGRAL};
			Integer[] rebateUserType={Constant.User.RET_DAILY_CONSUMPTION,Constant.User.RET_HEALTHY_CONSUMPTION};
			Long startTime=Long.parseLong(startDateTime);
			Long endTime=Long.parseLong(endDateTime);
			entities=repository.find(userId, usersType,rebateUserType,startTime,endTime,pageRequest);
		}else if (!StrUtil.isBlank(startDateTime) && !StrUtil.isBlank(endDateTime) && !StrUtil.isBlank(orderType)) {//按时间与类型查询
			int type=Integer.parseInt(orderType);
			Long startTime=Long.parseLong(startDateTime);
			Long endTime=Long.parseLong(endDateTime);
			if(type==Constant.User.RET_DAILY_CONSUMPTION || type==Constant.User.RET_HEALTHY_CONSUMPTION ){
				Integer[] usersType={};
				Integer[] rebateUserType={type};
				entities=repository.find(userId, usersType,rebateUserType,startTime,endTime,pageRequest);
			}else {
				Integer[] usersType={type};
				Integer[] rebateUserType={};
				entities=repository.find(userId, usersType,rebateUserType,startTime,endTime,pageRequest);
			}
		}else if(StrUtil.isBlank(startDateTime) && StrUtil.isBlank(endDateTime) && !StrUtil.isBlank(orderType)){//只按类型
			int type=Integer.parseInt(orderType);
			if(type==Constant.User.RET_DAILY_CONSUMPTION || type==Constant.User.RET_HEALTHY_CONSUMPTION ){
				Integer[] usersType={};
				Integer[] rebateUserType={type};
				entities=repository.find(userId, usersType,rebateUserType,pageRequest);
			}else {
				Integer[] usersType={type};
				Integer[] rebateUserType={};
				entities=repository.find(userId, usersType,rebateUserType,pageRequest);
			}
		}else {//查询全部
			Integer[] usersType={Constant.User.RECHARGE,Constant.User.REDUCE_DAILY_CONSUMPTION,
	                Constant.User.REDUCE_HEALTHY_CONSUMPTION,Constant.User.REFUND,Constant.User.RETURN_INTEGRAL};
			Integer[] rebateUserType={Constant.User.RET_DAILY_CONSUMPTION,Constant.User.RET_HEALTHY_CONSUMPTION};
			entities=repository.find(userId, usersType,rebateUserType,pageRequest);
		}
		
		
		if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
		{
			for(Order o :entities.getContent()){
				User user = userService.findCacheById(o.getUserId());
				Store store = storeService.findCacheById(o.getStoreid());
				if(null != user){
					String userName= user.getPrivacy().getName();
					o.setUserName(userName);
				}
				if(null != store){
					String storeName= store.getStoreName();
					o.setStoreName(storeName);
				}
			}
			return entities;
		}
		
		return null;
	}
	@Override
	@Transactional
	public boolean insert(OrderForm dto,String storeid,String userId) {
		Order order = new Order();
		BeanUtil.copyProperties(dto, order);
		User user = userRepository.findById(order.getUsersId()).orElse(null);
		if(order.getOrdertype()!=Constant.User.RECHARGE && order.getOrdertype()!=Constant.User.RETURN_INTEGRAL && null == user.getScore() 
				|| order.getOrdertype()!=Constant.User.RECHARGE && order.getOrdertype()!=Constant.User.RETURN_INTEGRAL && user.getScore()<order.getOrderPrice().longValue()){
			return false;
		}
		if(order.getOrdertype()==Constant.User.RETURN_INTEGRAL && null == user.getReturnScore() 
				|| order.getOrdertype()==Constant.User.RETURN_INTEGRAL && user.getReturnScore()<order.getOrderPrice().longValue()){
			return false;
		}
		order.setState(Constant.Order.ACTIVE);
		order.setCs(SystemClock.now()); 							//创建时间
		order.setStoreid(storeid);
		order.setId(userId+DateUtil.currentSeconds());
		if(order.getOrdertype()==Constant.User.RECHARGE){//充值  直接给用户账户加钱
			order.setSurplusPrice(null == user.getScore() ?0:user.getScore()+order.getOrderPrice());
			repository.save(order);//添加积分订单信息
			user.setScore(null == user.getScore() ?0:user.getScore() + order.getOrderPrice());
			user.setMs(SystemClock.now());
			updateStorePriceByDay(dto,storeid);
			userService.upById(user.getId(), updateUserSource(user));
		}else if (order.getOrdertype()==Constant.User.REDUCE_DAILY_CONSUMPTION || 
				order.getOrdertype()==Constant.User.REDUCE_HEALTHY_CONSUMPTION ) {//扣除日常消费积分或者大健康积分
			order.setSurplusPrice(user.getScore()-order.getOrderPrice());
			repository.save(order);//添加积分订单信息
			//updateFirstInvitedMemberSource(dto);//首邀用户
			user.setScore(user.getScore()-order.getOrderPrice());
			user.setMs(SystemClock.now());
			//用户减积分并返利
			if(order.getOrdertype()==Constant.User.REDUCE_DAILY_CONSUMPTION){
				scheduledThreadPool.schedule(new Runnable() {
             	   public void run() {
             		  updateStorePriceByDay(dto,storeid);
             		  updateUserSource(user,dto,storeid,Constant.User.DAILY_CONSUMPTION);
             	   }
                 }, 1, TimeUnit.SECONDS);
			}else {
				scheduledThreadPool.schedule(new Runnable() {
	             	   public void run() {
	             		  updateStorePriceByDay(dto,storeid);
	             		  updateUserSource(user,dto,storeid,Constant.User.HEALTHY_CONSUMPTION);
	             	}
	            }, 1, TimeUnit.SECONDS);
			}
		}else if(order.getOrdertype()==Constant.User.REFUND ){
			order.setSurplusPrice(user.getScore()-order.getOrderPrice());
			repository.save(order);//添加积分订单信息
			user.setScore(user.getScore()-order.getOrderPrice());
			user.setMs(SystemClock.now());
			updateStorePriceByDay(dto,storeid);
			userService.upById(user.getId(), updateUserSource(user));
		}else if (order.getOrdertype()==Constant.User.RETURN_INTEGRAL ) {
			order.setSurplusPrice(user.getReturnScore()-order.getOrderPrice());
			repository.save(order);//添加积分订单信息
			user.setReturnScore(user.getReturnScore()-order.getOrderPrice());
			user.setMs(SystemClock.now());
			updateStorePriceByDay(dto,storeid);
			userService.upById(user.getId(), updateUserSource(user));
		}
		return true;
	}
	
	//修改用户积分信息
	@Transactional
	public boolean updateUserSource(User user,OrderForm dto,String storeid,Integer type) throws RRException{
		//将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(user, false, true);
        //将Map属性转为  mongo 的属性
    	Map<String, Object> users = new HashMap<String, Object>();
    	for (String k : dtos.keySet()) {
    		if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
    		users.put( k, dtos.get(k));
    	}
    	//修改用户金额
    	if(CollectionUtil.isNotEmpty(users)){
    		userService.upById(user.getId(), users);
    	}
    	if(null != type){//返利
    		//0.查出返利比例
    		RebateConfig rebateConfig = mongoTemplate.findOne(new Query(Criteria.where("status").is(type)), RebateConfig.class);
    		if(null == rebateConfig){
    			return false;
    		}
    		//1.返给当前用户的pid 
    			//查询当前用户pid是不是管理员 若果是则不返利   
    		user = userService.findCacheById(user.getPid());
    		if(null != user && user.getUserType()==Constant.User.USER_TYPE_WEB){
    			if(type==Constant.User.DAILY_CONSUMPTION){
    				type=Constant.User.REDUCE_DAILY_CONSUMPTION;
    			}else {
    				type=Constant.User.REDUCE_HEALTHY_CONSUMPTION;
				}
    			BigDecimal bd=new BigDecimal(Integer.parseInt(dto.getOrderPrice())*(rebateConfig.getRebateOne()/100)).setScale(0, BigDecimal.ROUND_HALF_UP);
    		    Integer price =Integer.parseInt(bd.toString()); 
    			//double b=Integer.parseInt(dto.getOrderPrice())*(rebateConfig.getRebateOne()/100);
    			Order orders = new Order();
    			BeanUtil.copyProperties(dto, orders);
    			orders.setState(Constant.Order.ACTIVE);
    			orders.setCs(SystemClock.now()); 	
    			orders.setOrderPrice(price);
    			orders.setOrdertype(type+1);
    			orders.setRebateUserId(user.getId());
    			orders.setStoreid(storeid);//便于根据门店id统计
    			orders.setSurplusPrice(null == user.getScore() ? 0 :user.getReturnScore() +(int) (Integer.parseInt(dto.getOrderPrice())*(rebateConfig.getRebateOne()/100)));
    			repository.save(orders);
    			//给接收返利的用户增加返利积分
    			user.setReturnScore(null == user.getScore() ? 0 :user.getReturnScore() +orders.getOrderPrice());
    			user.setMs(SystemClock.now());
    			userService.upById(user.getId(), updateUserSource(user));
    			//2.返给pid用户的pid
    			user = userService.findCacheById(user.getPid());
    			if(null != user && user.getUserType()==Constant.User.USER_TYPE_WEB){
    				bd=new BigDecimal(Integer.parseInt(dto.getOrderPrice())*(rebateConfig.getRebateTwo()/100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        		    price =Integer.parseInt(bd.toString()); 
    	    		orders = new Order();
    	    		BeanUtil.copyProperties(dto, orders);
    	    		orders.setState(Constant.Order.ACTIVE);
    	    		orders.setCs(SystemClock.now()); 	
    	    		orders.setOrderPrice(price);
    	    		orders.setOrdertype(type+1);
    	    		orders.setRebateUserId(user.getId());
    	    		orders.setStoreid(storeid);
    	    		orders.setSurplusPrice(null == user.getScore() ? 0 :user.getReturnScore() +(int) (Integer.parseInt(dto.getOrderPrice())*(rebateConfig.getRebateTwo()/100)));
    	    		repository.save(orders);
    	    		//给接收返利的用户增加返利积分
    	    		user.setReturnScore(null == user.getScore() ? 0 :user.getReturnScore() +orders.getOrderPrice());
    	    		user.setMs(SystemClock.now());
    	    		userService.upById(user.getId(), updateUserSource(user));
    			}
    		}
    	}
		return true;
	}
	
	//修改用户积分信息
	@Transactional
	public Map<String, Object> updateUserSource(User user) throws RRException{
		//将对象转为Map
	    Map<String, Object> dtos = BeanUtil.beanToMap(user, false, true);
	    //将Map属性转为  mongo 的属性
	   Map<String, Object> users = new HashMap<String, Object>();
	    for (String k : dtos.keySet()) 
	    {
	    	if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
	    	users.put( k, dtos.get(k));
	    }
		return users;
	}
	
	//查询当前当前被操作用户是否的上级或上上级用户是否是店员或管理员直接推荐用户   如果是  则直接给系统首邀会员消费金额增加
	@Transactional
	public void updateFirstInvitedMemberSource(OrderForm form) throws RRException{
		boolean boo = true;
		User user = userService.findCacheById(form.getUserId());//查询当前用户是否是首邀用户
		if(null !=user && user.getUserType()==Constant.User.USER_TYPE_WEB){//不是首邀用户  则查询上一级用户是否是首邀用户
			user = userService.findCacheById(user.getPid());
			if(null !=user && user.getUserType()==Constant.User.USER_TYPE_WEB){//不是首邀用户  则查询上一级用户是否是首邀用户
				user = userService.findCacheById(user.getPid());
				if(null !=user && user.getUserType()==Constant.User.USER_TYPE_WEB){//不是首邀用户  则查询上一级用户是否是首邀用户
					boo=false;
				}
			}
		}
		if(boo){
			UserCountPrice u = mongoTemplate.findOne(new Query(Criteria.where("userId").is(user.getId())), UserCountPrice.class);
			if(null != u){//
				int price = Integer.parseInt(form.getOrderPrice());
				u.setScore(u.getScore().intValue()+price);
				updateUserCountPrice(u);
			}else {
				UserCountPrice userCountPrice = new UserCountPrice();
				userCountPrice.setUserId(user.getId());
				userCountPrice.setName(user.getPrivacy().getName());
				userCountPrice.setAge(user.getVisible().getAge());
				userCountPrice.setGender(user.getVisible().getGender());
				userCountPrice.setPhone(user.getPhone());
				userCountPrice.setScore(Integer.parseInt(form.getOrderPrice()));
				userCountPriceRepository.save(userCountPrice);
			}
		}
	}
	//根据当前年月日将当天门店产生的积分交易以及交易次数存入数据库
	@Transactional
	public void updateStorePriceByDay(OrderForm form,String storeid) throws RRException{
		//1.判断当天门店是否有产生交易  如果没有则根据当前订单类型产生新的交易信息
		Long nowDate = DateUtils.getDate(new Date(),1);//获取当天的开始时间
		StorePrice u = mongoTemplate.findOne(new Query(Criteria.where("storeid").is(storeid).and("cs").is(nowDate)), StorePrice.class);
		Integer orderType=Integer.parseInt(form.getOrdertype());
		long orderPrice = Long.parseLong(form.getOrderPrice());
		if(null != u){
			if(orderType==Constant.User.RECHARGE){
				u.setRechargeIntegral(orderPrice+u.getRechargeIntegral());
				u.setRechargeIntegralCount(u.getRechargeIntegralCount().intValue()+1);
			}else if (orderType==Constant.User.REDUCE_DAILY_CONSUMPTION) {
				u.setDayConsumptionIntegral(orderPrice+u.getDayConsumptionIntegral());
				u.setDayConsumptionIntegralCount(1+u.getDayConsumptionIntegralCount().intValue());
			}else if(orderType==Constant.User.REDUCE_HEALTHY_CONSUMPTION){
				u.setHealthyIntegral(orderPrice+u.getHealthyIntegral());
				u.setHealthyIntegralCount(1+u.getHealthyIntegralCount().intValue());
			}else if (orderType==Constant.User.REFUND) {
				u.setRefundIntegral(orderPrice+u.getRefundIntegral());
				u.setRefundIntegralCount(1+u.getRefundIntegralCount().intValue());
			}else if (orderType==Constant.User.RETURN_INTEGRAL) {
				u.setReturnIntegral(orderPrice+u.getReturnIntegral());
				u.setReturnIntegralCount(1+u.getReturnIntegralCount().intValue());
			}
			updateStorePrice(u);
		}else {//2.如果有发生过交易   则将当前订单类型的信息加上之前的信息   修改
			u = new StorePrice();
			u.setCs(nowDate);
			u.setStoreid(storeid);
			if(orderType==Constant.User.RECHARGE){
				u.setRechargeIntegral(orderPrice);
				u.setRechargeIntegralCount(1);
			}else if (orderType==Constant.User.REDUCE_DAILY_CONSUMPTION) {
				u.setDayConsumptionIntegral(orderPrice);
				u.setDayConsumptionIntegralCount(1);
			}else if(orderType==Constant.User.REDUCE_HEALTHY_CONSUMPTION){
				u.setHealthyIntegral(orderPrice);
				u.setHealthyIntegralCount(1);
			}else if (orderType==Constant.User.REFUND) {
				u.setRefundIntegral(orderPrice);
				u.setRefundIntegralCount(1);
			}else if (orderType==Constant.User.RETURN_INTEGRAL) {
				u.setReturnIntegral(orderPrice);
				u.setReturnIntegralCount(1);
			}
			storePriceRepository.save(u);
		}
	}
	//修改首邀用户积分信息
	@Transactional
	public void updateUserCountPrice(UserCountPrice userCountPrice) throws RRException{
		//将对象转为Map
		Map<String, Object> dtos = BeanUtil.beanToMap(userCountPrice, false, true);
		//将Map属性转为  mongo 的属性
		Map<String, Object> userCountPrices = new HashMap<String, Object>();
		for (String k : dtos.keySet()) 
		{
		  if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
		  userCountPrices.put( k, dtos.get(k));
		}
		//修改用户金额
		if(CollectionUtil.isNotEmpty(userCountPrices))
		{
		  mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(userCountPrice.getId()))), MongoUtils.mongoUpdate(userCountPrices), UserCountPrice.class);
		}
	}
	
	//修改首邀用户积分信息
	@Transactional
	public void updateStorePrice(StorePrice storePrice) throws RRException{
		//将对象转为Map
		Map<String, Object> dtos = BeanUtil.beanToMap(storePrice, false, true);
		//将Map属性转为  mongo 的属性
		Map<String, Object> storePrices = new HashMap<String, Object>();
		for (String k : dtos.keySet()) 
		{
			if(StrUtil.isBlank(k) || null == dtos.get(k)) continue; //忽略空字段
			storePrices.put( k, dtos.get(k));
		}
		//修改用户金额
		if(CollectionUtil.isNotEmpty(storePrices))
		{
			mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(storePrice.getId()))), MongoUtils.mongoUpdate(storePrices), StorePrice.class);
		}
	}
	
	@Autowired
   	private UserService userService;
	
	@Autowired
   	private StoreService storeService;
	
    @Autowired
    private OrderRepository repository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserCountPriceRepository userCountPriceRepository;
    
    @Autowired
    private StorePriceRepository storePriceRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10); 

	

}
