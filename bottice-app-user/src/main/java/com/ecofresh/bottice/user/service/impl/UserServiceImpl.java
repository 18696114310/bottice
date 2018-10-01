package com.ecofresh.bottice.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.store.service.StoreService;
import com.ecofresh.bottice.user.dto.UCache;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.user.entities.UserCountPrice;
import com.ecofresh.bottice.user.repository.UserCountPriceRepository;
import com.ecofresh.bottice.user.repository.UserRepository;
import com.ecofresh.bottice.user.service.UserService;
import com.ecofresh.bottice.wx.service.WxService;
import com.ecofresh.common.exception.ErrorDesc;
import com.ecofresh.common.exception.RRException;
import com.ecofresh.core.mongo.utils.MongoUtils;
import com.ecofresh.modules.oss.cloud.OSSFactory;
import com.ecofresh.utils.ShareCodeUtil;
import com.ecofresh.utils.Util;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class UserServiceImpl implements UserService
{
	public Page<User> find(int page, int row, final String name)
    {
		if(1 > page) page = 1;
		if(1 > row) row = 1;
		
    	//if (StrUtil.isBlank(name)) return null;
		PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		Page<User> entities = null;
		if (!StrUtil.isBlank(name)){
			if(StringUtils.isNumeric(name)){
				Long cardNo=Long.parseLong(name);
				entities = repository.find(name,cardNo,Constant.User.USER_TYPE_WEB, pageRequest);
			}else {
				entities = repository.find(name,Constant.User.USER_TYPE_WEB, pageRequest);
			}
		}else {
			User user = new User();
	        user.setState(Constant.User.ACTIVE);
	        user.setUserType(Constant.User.USER_TYPE_WEB);
			entities = this.find(page,row,user);
		}
		if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
		{
			return entities;
		}
		
		return null;
    }
	
	@Override
	public Page<User> find(int page, int row, User dto)
	{
		PageRequest pageable = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
														  .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
														  .withMatcher("phone", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withMatcher("userType", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.contains())//姓名采用“开始匹配”的方式查询
														  .withIgnorePaths("loc", "score");  //忽略属性：是否关注。因为是基本类型，需要忽略掉;
		Example<User> example = Example.of(dto, matcher);
		return repository.findAll(example, pageable);
	}

	@Override
	public Page<User> findManager (int page, int row, String key)
	{
		if(1 > page) page = 1;
		if(1 > row) row = 1;
		
    	//if (StrUtil.isBlank(name)) return null;
		PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
		Page<User> entities = null;
        if (!StrUtil.isBlank(key)){
            entities = repository.find(key,Constant.User.USER_TYPE_MGR, pageRequest);
        }else {
            entities=repository.finds(Constant.User.USER_TYPE_MGR.toString(), pageRequest);
        }

		if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
		{
			return entities;
		}
		return null;
	}

	@Override
	public Collection<UserCountPrice> mapReduceInfo(int page, int row, final String key){

		if (!StrUtil.isBlank(key)){
			PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "score"));
			Page<UserCountPrice> entities = userCountPriceRepository.find(key, pageRequest);

			if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
			{
				return entities.getContent();
			}
		}else{
			PageRequest pageable = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "score"));
			ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
					.withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
					.withIgnorePaths("gender","age","score");
			Example<UserCountPrice> example = Example.of(new UserCountPrice(), matcher);
			return userCountPriceRepository.findAll(example, pageable).getContent();
		}


		return null;

	}

	@Override
	public List<User> findUserByUsername(User user){
		return mongoTemplate.find(Query.query(Criteria.where("privacy.name").is(user.getPrivacy().getName()).and("userType").is(user.getUserType())),User.class);
	}


	@Override
	public void upById(String id, Map<String, Object> map) 
	{
		if (CollectionUtil.isEmpty(map) || StrUtil.isBlank(id)) return;
		map.put("ms", SystemClock.now());
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(id))), MongoUtils.mongoUpdate(map), User.class);
    	//删除用户缓存信息
    	this.delCacheById(id);
	}

	@Override
	public boolean upByPhone(String mobile, Map<String, Object> map) 
	{
		if (CollectionUtil.isEmpty(map) || StrUtil.isBlank(mobile)) return false;
		map.put("ms", SystemClock.now());
    	mongoTemplate.updateFirst(new Query(Criteria.where("phone").is(mobile).and("userType").is(Constant.User.USER_TYPE_MGR)), MongoUtils.mongoUpdate(map), User.class);
    	return true;
	}
//	@Override
//	public boolean upPwd(String mobile, Map<String, Object> map)
//	{
//
//		if (CollectionUtil.isEmpty(map) || StrUtil.isBlank(mobile)) return false;
//		map.put("ms", SystemClock.now());
//		mongoTemplate.updateFirst(new Query(Criteria.where("phone").is("id").and("pwd") ), MongoUtils.mongoUpdate(map), User.class);
//		return true;
//	}



	@Override
	public boolean delete(String... ids) 
	{
		if (ArrayUtil.isEmpty(ids)) return false;
		
		for(String id : ids)
		{
			if (StrUtil.isBlank(id)) continue;
			
			mongoTemplate.remove(new Query(Criteria.where("id").is(new ObjectId(id))), User.class);
	    	//删除用户缓存信息
	    	this.delCacheById(id);
		}
		
		return true;
	}
	
	public User findCacheById(String id)
	{
		if (StrUtil.isBlank(id)) return null;
		
		//此处先查找缓存，缓存没有在查询数据库
		String key = Constant.PROFILE_KEY;
		HashOperations<String, String, String> operations = redisTemplate.opsForHash();
		String cache = operations.get(key, id);
		
		if(StrUtil.isNotBlank(cache))
		{
			return JSON.parseObject(cache, User.class);
		}

		User user = this.findById(id);
		if(null != user && StrUtil.isNotBlank(user.getId()))
		{
			String json = JSON.toJSONString(user);
			if(StrUtil.isBlank(json)) return null;
			operations.put(key, id, json);
			return user;
		}
		
		return null;
	}

	@Override
	public UCache findCache(String id) 
	{
		if (StrUtil.isBlank(id)) return null;
		
		User dto = this.findCacheById(id);
		if(null != dto && StrUtil.isNotBlank(dto.getId()))
		{
			UCache cache = new UCache();
			if(null != dto.getVisible())
			{
				BeanUtil.copyProperties(dto.getVisible(), cache);
			}
			cache.setId(id);
			cache.setPhone(dto.getPhone());
			
			return cache;
		}
		
		return null;
	}

	@Override
	public void delCacheById(String id) 
	{
		if (StrUtil.isBlank(id)) return;
		//此处先查找缓存，缓存没有在查询数据库
		redisTemplate.opsForHash().delete(Constant.PROFILE_KEY, id);
	}

	@Override
	public User insert(User entity) 
	{
		return repository.save(entity);
	}

	private Long genCode() 
	{
		// 第一次，设置初始值
   		if(0L == redisAtomic.get())
   		{
   			Long max = findMaxCode();
   			if(Constant.INV_MIN_CODE >= max.longValue())
   			{
   				redisAtomic.set(Constant.INV_MIN_CODE);
   			}
   			else
   			{
   				redisAtomic.set(max);
   			}
   		}
   		Long code = redisAtomic.incrementAndGet();
   		//判断是否连号
   		if(Util.getCharEq(String.valueOf(code)))
   		{
   			return redisAtomic.incrementAndGet();
   		}
   		//判断是否有4
   		if(this.getChar4(String.valueOf(code)))
   		{
   			return redisAtomic.incrementAndGet();
   		}
   		
   		return code;
	}
	
	/**
     * 判断是否是连号 
     * @param str 字符 
     * @return
     */
    private boolean getChar4(String str)
    {
        char[] chars = str.toCharArray();
        char c = '4';
        int len = chars.length;
        
        for(int i = 0; i < len; i++)
        {
            if(c == chars[i])
            {
                return true;
            }
        }
        
        return false;
    }
	
	/***
	 * 获取数据库最大编号
	 * @return
	 */
	private Long findMaxCode()
   	{
   		Query query = new Query();
   		query.with(new Sort(Sort.Direction.DESC, "card_no"));
   		
   		User user = mongoTemplate.findOne(query, User.class);
   		if(null != user && null != user.getCardNo())
   		{
   			return user.getCardNo();
   		}
		
		return 0L;
   	}
	@Override
	@Transactional
	public User insert(String name, String phone, String pwd, String ucode, String avator, Integer gender, String birthday, String openid,Integer userType,String storeid,String hiredat,Integer role) throws RRException
	{
		User user = new User();
		//初始化
		User.Privacy secret = user.new Privacy();
		secret.setName(name);
		secret.setPwd(Util.SHA256(Constant.User.USER_DEFAULT_PWD));
		user.setPrivacy(secret);
		user.setPhone(phone);										//手机号码
		user.setCardNo(this.genCode());								//卡号
		user.setUcode(ShareCodeUtil.toSerialCode(user.getCardNo()));//邀请码
		user.setState(Constant.User.ACTIVE);
		user.setCs(SystemClock.now()); 							//创建时间
		user.setMs(SystemClock.now()); 							//修改时间
		user.setUserType(Constant.User.USER_TYPE_MGR);  		//后端用户
		user.setStoreid(storeid);
		user.setRole(role);//店别ID
		//user.setStorename(storename);
        user.setHiredate(hiredat);                               //入职日期
        user.setDirectReferee(Constant.User.USER_REFEREE);
        user.setIndirectReferee(Constant.User.USER_REFEREE);
        user.setScore(Constant.User.USER_DEFAULT_INTEGRAL);
        user.setReturnScore(Constant.User.USER_DEFAULT_INTEGRAL);
		//初始化个人信息
		User.Visible visibleUser = user.new Visible();
		visibleUser.setIdAuth(Constant.User.AUTH_DEF);
		visibleUser.setGender(gender);
		visibleUser.setNickName(phone.substring(0, 3) + "****" + phone.substring(7));
		visibleUser.setAvator(avator);
		visibleUser.setBirthday(birthday);
		user.setVisible(visibleUser);
        //生成二维码
        try {
            File file = wxService.getWxService().getQrcodeService().createWxCodeLimit(user.getUcode(), wxService.getPage());
            String url = OSSFactory.build().uploadSuffix(FileUtil.readBytes(file), Constant.CODE_URL_FIX);
            user.setCodeUrl(url);
        } catch (Exception e) {
            logger.error("wx code err:", e);
            throw new RRException(ErrorDesc.INV_CODE_ERR.getMsg(), ErrorDesc.INV_CODE_ERR.getCode());
        }



		User entity = null;
		try
		{
			entity = this.insert(user);
		}
		catch (DuplicateKeyException e)
		{
			user.setCardNo(this.genCode());	//邀请码
			user.setUcode(ShareCodeUtil.toSerialCode(user.getCardNo()));//邀请码
			entity = this.insert(user);
		}
		return entity;

	}

	@Override
   	@Transactional
	public User insert(String name, String phone, String pwd, String ucode, String avator, Integer gender, String birthday, String openid) throws RRException
	{

    	User dto = null;
        //判断邀请码是否存在
        if(null == (dto = this.findByCode(ucode)))
        {
			throw new RRException(com.ecofresh.bottice.enums.ErrorDesc.U_CODE_ERR.getMsg(), com.ecofresh.bottice.enums.ErrorDesc.U_CODE_ERR.getCode());
        }
		
		User user = new User();
		//初始化
		User.Privacy secret = user.new Privacy();
		secret.setName(name);
		secret.setPwd(StrUtil.isBlank(pwd) ? null : Util.SHA256(pwd));
		user.setPrivacy(secret);
		user.setPid(dto.getId());									//推荐人ID这样取法有问题，推荐人ID，应该是取邀请码对应的_id先 暂时屏蔽掉
		user.setPhone(phone);										//手机号码
		user.setCardNo(this.genCode());								//卡号
		user.setUcode(ShareCodeUtil.toSerialCode(user.getCardNo()));//邀请码
		user.setState(Constant.User.ACTIVE);
        user.setCs(SystemClock.now()); 							//创建时间
        user.setMs(SystemClock.now()); 							//修改时间
		user.setUserType(Constant.User.USER_TYPE_WEB); 			//前端用户
		user.setDirectReferee(Constant.User.USER_REFEREE);
        user.setIndirectReferee(Constant.User.USER_REFEREE);
        user.setScore(Constant.User.USER_DEFAULT_INTEGRAL);
        user.setReturnScore(Constant.User.USER_DEFAULT_INTEGRAL);
        //初始化个人信息
        User.Visible visibleUser = user.new Visible();
  		visibleUser.setIdAuth(Constant.User.AUTH_DEF);
  		visibleUser.setGender(gender);
  		visibleUser.setNickName(phone.substring(0, 3) + "****" + phone.substring(7));
  		visibleUser.setAvator(avator);
  		visibleUser.setBirthday(birthday);
  		user.setVisible(visibleUser);
  		//初始化微信授权信息
  		if(StrUtil.isNotBlank(openid))
  		{
  	  		User.Wx wx = user.new Wx();
  	  		wx.setOpenid(openid);
  	  		user.setWx(wx);
  		}
  		
  		//生成二维码
  		try
  		{
			File file = wxService.getWxService().getQrcodeService().createWxCodeLimit(user.getUcode(), wxService.getPage());
			String url = OSSFactory.build().uploadSuffix(FileUtil.readBytes(file), Constant.CODE_URL_FIX);
			user.setCodeUrl(url);
		} 
  		catch (Exception e) 
  		{
  			logger.error("wx code err:", e);
			throw new RRException(ErrorDesc.INV_CODE_ERR.getMsg(), ErrorDesc.INV_CODE_ERR.getCode());
		}
  		
  		User entity = null;
  		try 
  		{
			entity = this.insert(user);
		} 
  		catch (DuplicateKeyException e)
  		{
  			user.setCardNo(this.genCode());	//邀请码
  			user.setUcode(ShareCodeUtil.toSerialCode(user.getCardNo()));//邀请码
			entity = this.insert(user);
		}
  		/*

  		 */
		/*final String uid = entity.getId();	//用户ID
		final String pid = dto.getId();		//推荐人ID
		executors.execute(new Runnable()
		{
			@Override
			public void run() 
			{
				regAsyn(uid, pid, ucode);
			}
		});*/
		final String pid = dto.getId();

		Integer directReferee = dto.getDirectReferee()==null?0:dto.getDirectReferee();
		updateDirectRefereeCount(pid,directReferee,dto);
		return entity;
	}




	/****
   	 * 异步，方法
   	 */
   	public void regAsyn(String id, String pid, String ucode)
   	{
   		//绑定用户
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pid", pid);
		this.upById(id, params);
   	}

	/**
	 * 更新直接推荐人和间接推荐人的数量
	 *
	 */
	public void updateDirectRefereeCount(String pid,Integer directReferee,User dto)
	{
		Map<String,Object> params = new HashMap<String,Object>();

		directReferee =directReferee+1;

		params.put("directReferee", String.valueOf(directReferee) );
		this.upById(pid,params);
		if(null != dto.getPid() && !dto.getPid().isEmpty()){
			dto = this.findCacheById(dto.getPid());//根据推荐人ID获取推荐人信息为了更新间接推荐人的信息
			Map<String,Object> inDirectRefereeParams = new HashMap<String,Object>();
			Integer indirectReferee = dto.getIndirectReferee().intValue()+1;
			inDirectRefereeParams.put("indirectReferee",indirectReferee);
			this.upById(dto.getId(),inDirectRefereeParams);
		}

	}
	
	@Override
	@Transactional
	public void updateById(String id, String userId, String name, Integer gender, String birthday, String phone,
			String storeid, Integer usertype, String hiredate,Integer state,Integer role) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("msId", id);
		if(StrUtil.isNotBlank(name))
			map.put("privacy.name", name);
		if(null!=gender&&StrUtil.isNotBlank(gender.toString()))
			map.put("Visible.gender", gender);
		if(StrUtil.isNotBlank(birthday)){
			map.put("Visible.birthday", birthday);
		}
		if(StrUtil.isNotBlank(phone))
			map.put("phone", phone);
		if(StrUtil.isNotBlank(storeid)){
			map.put("storeid", storeid);
		}
		if(null!=usertype&&StrUtil.isNotBlank(usertype.toString()))
			map.put("userType", usertype);
		if(StrUtil.isNotBlank(hiredate))
			map.put("hiredate", hiredate);
		if(null!=state&&StrUtil.isNotBlank(state.toString()))
			map.put("state", state);
        if(null!=role&&StrUtil.isNotBlank(role.toString()))
            map.put("role", role);
		this.upById(userId,map);
	}

	@Override
	public boolean existsCode(String code) 
	{
		return mongoTemplate.findOne(new Query(Criteria.where("ucode").is(code)), User.class) != null;
	}

	@Override
	public User findById(String id) 
	{
		return repository.findById(id).orElse(null);
	}

	@Override
	public User findByCode(String code) 
	{
		return mongoTemplate.findOne(new Query(Criteria.where("ucode").is(code)), User.class);
	}

	@Override
	public User findByPhone(String phone)
	{
		return mongoTemplate.findOne(new Query(Criteria.where("phone").is(phone).and("userType").is(Constant.User.USER_TYPE_WEB)), User.class);
	}

	@Override
	public User findMangerByPhone(String phone)
	{
		return mongoTemplate.findOne(new Query(Criteria.where("phone").is(phone).and("userType").is(Constant.User.USER_TYPE_MGR)), User.class);
	}

	@Override
	public User findByWxOpenId(String openid)
	{
		return mongoTemplate.findOne(new Query(Criteria.where("wx.openid").is(openid)), User.class);
	}
	
	@Autowired
    private WxService wxService;

	@Autowired
	private StoreService storeService;
    @Autowired
    private UserRepository repository;

    @Autowired
	private UserCountPriceRepository userCountPriceRepository;

    @Autowired
    private RedisAtomicLong redisAtomic;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //创建5个线程执行 任务
    public final ExecutorService executors = Executors.newFixedThreadPool(5);

	protected Logger logger = LoggerFactory.getLogger(getClass());

	
}
