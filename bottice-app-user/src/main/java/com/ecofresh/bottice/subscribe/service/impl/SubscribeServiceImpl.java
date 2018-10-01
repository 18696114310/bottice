package com.ecofresh.bottice.subscribe.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.subscribe.entities.Subscribe;
import com.ecofresh.bottice.subscribe.entities.SubscribeLog;
import com.ecofresh.bottice.subscribe.form.SubscribeForm;
import com.ecofresh.bottice.subscribe.repository.SubscribeLogRepository;
import com.ecofresh.bottice.subscribe.repository.SubscribeRepository;
import com.ecofresh.bottice.subscribe.service.SubscribeService;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.core.mongo.utils.MongoUtils;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscribeRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SubscribeLogRepository subscribeLogRepository;

    /***
     * 分页查询预约记录信息(个人版)
     * @param subscribe 预约记录信息对象
     * @return
     */
    @Override
    public Page<Subscribe> find(int page, int rows, Subscribe subscribe){
        if(page<1)page=1;
        if(rows<1)rows=1;
        PageRequest pageable = PageRequest.of(page-1, rows, new Sort(Sort.Direction.DESC, "add_time"));
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.contains());//用户id采用全员匹配方式
        Example<Subscribe> example = Example.of(subscribe, matcher);
        return repository.findAll(example, pageable);
    }

    /***
     * 分页查询预约记录信息(管理版)
     * @param subscribe 预约记录信息对象
     * @return
     */
    @Override
    public Page<Subscribe> findByAdmin(int page, int rows, Subscribe subscribe,List<User> users,Integer status){
        if(page<1)page=1;
        if(rows<1)rows=1;
        /*if (status.equals(Constant.User.SUSPENDED)){
            PageRequest pageRequest = PageRequest.of(page-1, rows, new Sort(Sort.Direction.DESC, "add_time"));
            Page<Subscribe> entities = repository.find(users==null?null:(String[])users.stream().map(User::getId).collect(Collectors.toList()).toArray(),subscribe.getStore().getStoreId(),subscribe.getSubscribeDate(),subscribe.getStoreState(), pageRequest);
            return entities;
        }else {
            PageRequest pageable = PageRequest.of(page - 1, rows, new Sort(Sort.Direction.DESC, "add_time"));
            ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
                    .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
                    .withMatcher("Store.storeId", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("subscribeDate", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("storeState", ExampleMatcher.GenericPropertyMatchers.contains());
            Example<Subscribe> example = Example.of(subscribe, matcher);
            return repository.findAll(example, pageable);
        }*/
        if(subscribe!=null&&subscribe.getStoreState().equals(Constant.User.SUBSCRIBE_STATUS_ALL)){
            PageRequest pageRequest = PageRequest.of(page-1, rows, new Sort(Sort.Direction.DESC, "add_time"));
            Page<Subscribe> entities=null;
            if(StrUtil.isNotBlank(subscribe.getUserName()))
            {
                entities = repository.findByName(subscribe.getUserName(),subscribe.getStore().getStoreId(),subscribe.getSubscribeDate(), pageRequest);
            }else{
                entities = repository.findByStoreId(subscribe.getStore().getStoreId(),subscribe.getSubscribeDate(), pageRequest);
            }

            return entities;
        }else{
            PageRequest pageable = PageRequest.of(page - 1, rows, new Sort(Sort.Direction.DESC, "add_time"));
            ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)//改变默认字符串匹配方式：模糊查询
                    .withIgnoreCase(true)//改变默认大小写忽略方式：忽略大小写;
                    .withMatcher("userName", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("Store.storeId", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("subscribeDate", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("storeState", ExampleMatcher.GenericPropertyMatchers.contains());
            Example<Subscribe> example = Example.of(subscribe, matcher);
            return repository.findAll(example, pageable);
        }
    }

    @Override
    public void updateCurrentObject(Subscribe subscribe){
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(subscribe.getId())),MongoUtils.mongoUpdate(subscribe), Subscribe.class);
    }


    /****
     * 分页查询 用户信息
     * @param page 页数
     * @param row  每页条数
     * @param name
     * @return
     */
    @Override
    public Collection<Subscribe> find(int page, int row, final String name){
        if(1 > page) page = 1;
        if(1 > row) row = 1;

        if (StrUtil.isBlank(name)) return null;
        PageRequest pageRequest = PageRequest.of(page-1, row, new Sort(Sort.Direction.DESC, "_cs"));
        Page<Subscribe> entities = repository.find(name, name, pageRequest);

        if(null != entities && CollectionUtil.isNotEmpty(entities.getContent()))
        {
            return entities.getContent();
        }

        return null;
    }

    @Override
    @Transactional
    public Subscribe update(String id,Integer status,String code){
        Subscribe subscribe = mongoTemplate.findOne(new Query(new Criteria().where("id").is(id)), Subscribe.class);
        subscribe.setStoreState(status);
        //将对象转为Map
        Map<String, Object> dtos = BeanUtil.beanToMap(subscribe, false, true);
        //将Map属性转为  mongo 的属性
        Map<String, Object> subscribeForms = new HashMap<String, Object>();
        dtos.forEach((k,v)->{
            if(StrUtil.isBlank(k) || null == v){
                return;
            }
            if("lat".equals(k)||"lng".equals(k)){
                subscribeForms.put("Lbs."+k,v);
                return;
            }else if("storeId".equals(k)||"storeName".equals(k)||"storePhone".equals(k)){
                subscribeForms.put("Store."+k,v);
                return;
            }else if("storeState".equals(k)){
                subscribeForms.put(k,status);
                return;
            }
            subscribeForms.put( k, v);
        });
        //修改预约信息
        if(CollectionUtil.isNotEmpty(subscribeForms))
        {
            UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), MongoUtils.mongoUpdate(subscribeForms), Subscribe.class);
            if(null == ret) return null;
            /*//如果当前操作是取消管理版取消预约的话，就短信通知用户
            if(null!=code&&"adminCancel".equals(code)){

                //得到的预约用户
                String userName=mongoTemplate.find(Query.query(Criteria.where("id").is(subscribe.getUserId())),User.class).get(0).getPrivacy().getName();
                //得到用户的手机号
                String phone=mongoTemplate.find(Query.query(Criteria.where("id").is(subscribe.getUserId())),User.class).get(0).getPhone();
            }*/
            return subscribe;
        }
        return null;
    }

    @Override
    @Transactional
    public Subscribe insert(SubscribeForm dto){
        Subscribe subscribe = new Subscribe();
        BeanUtil.copyProperties(dto, subscribe);
        subscribe.setAddTime(new Date());
        //添加详细状态
        subscribe.setStoreState(Constant.Subscribe.SUBSCRIBE_ROUND);
        //添加门店坐标
        Subscribe.Lbs lbs=subscribe.new Lbs();
        lbs.setLat(dto.getLat());
        lbs.setLng(dto.getLng());
        subscribe.setLbs(lbs);
        //添加门店的信息
        Subscribe.Store store=subscribe.new Store();
        store.setStoreId(dto.getStoreId());//门店id
        store.setStoreName(dto.getStoreName());
        store.setStorePhone(dto.getStorePhone());
        subscribe.setStore(store);
        //添加用户姓名
        subscribe.setUserName(mongoTemplate.find(Query.query(Criteria.where("id").is(dto.getUserId())),User.class).get(0).getPrivacy().getName());
        return repository.save(subscribe);
    }

    @Override
    public Subscribe findone(String sessionId)
    {
        return mongoTemplate.findOne(new Query(new Criteria().where("userId").is(sessionId).and("storeState").is(0)), Subscribe.class);
    }

    @Override
    public Subscribe findOneById(String id){
        return mongoTemplate.findOne(new Query(new Criteria().where("id").is(id)), Subscribe.class);
    }

    @Override
    public Map findOneAdminById(String id){
        Map<String,Object> map=new HashMap<String,Object>();
        Subscribe subscribe = mongoTemplate.findOne(new Query(new Criteria().where("id").is(id)), Subscribe.class);
        if(subscribe!=null){
            map.put("subscribeInfo",subscribe);
            User user=mongoTemplate.findOne(new Query(new Criteria().where("id").is(subscribe.getUserId())), User.class);
            map.put("user",user==null?null:user);
        }else{
            map.put("subscribeInfo",null);
            map.put("user",null);
        }

        return map;
    }


    @Scheduled(cron = "0 58 23 * * * ")
    public void syncTask(){
       List<Subscribe> list= mongoTemplate.find(new Query(Criteria.where("storeState").is(0)),Subscribe.class);
       list.stream()
               .filter(x->strToDate(x.getSubscribeDate()).before(new Date()))
               .forEach((y)->{
                   Long startTime=System.currentTimeMillis();
                   y.setStoreState(Constant.Subscribe.SUBSCRIBE_TIMEOUT);
                   UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(new ObjectId(y.getId()))), MongoUtils.mongoUpdate(y), Subscribe.class);
                   //新增取消预约记录的日志
                   SubscribeLog s=new SubscribeLog();
                   s.setOpertionCode(Constant.Subscribe.SUBSCRIBELOG_TIMEOUT);
                   s.setOpertionUserId("系统定时操作的");
                   s.setOpertion("预约失效");
                   Long end = System.currentTimeMillis();
                   s.setOpertionStartTime(startTime);
                   s.setOpertionEndTime(end);
                   s.setPayTime(end-startTime);
                   if(ret!=null){
                       s.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_SUCCESS);
                   }else{
                       s.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
                   }
                   s.setSubscribeId(y.getId());
                   subscribeLogRepository.insert(s);
               });
    }

    /**
     * 将Date类型的日期去除月和日中的0
     * @param d
     * @return
     */
    public static String strToStr(Date d){
        String str=new SimpleDateFormat("yyyy-MM-dd").format(d);
        String year=str.substring(0,str.indexOf("-"));
        String month=str.substring(5,str.lastIndexOf("-"));
        String date=str.substring(str.lastIndexOf("-")+1,str.length());
        String newMonth;
        String newDate;
        if(month.substring(0,1).equals("0")){
            newMonth=month.replace("0","");
        }else{
            newMonth=month;
        }
        if(date.substring(0,1).equals("0")){
            newDate=date.replace("0","");
        }else{
            newDate=date;
        }
        return year+"-"+newMonth+"-"+newDate;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
     }

    @Override
    public Map<String,Object> mapReduceInfo(String storeId,String userId){
        Map<String,Object> map=new HashMap<String,Object>();
        List<Subscribe> list= mongoTemplate.find(new Query(new Criteria().where("Store.storeId").is(storeId)),Subscribe.class);
        //得到当前门店今日预约的总数
        Long totalSubscribe = list.stream()
                .filter(x->x.getSubscribeDate().equals(strToStr(new Date()))&&(x.getStoreState().equals(0)||x.getStoreState().equals(1)))
                .map(Subscribe::getId)
                .collect(Collectors.counting());
        //统计当前门店今日预约待到达总数
        Long totalWaitSubscribe = list.stream()
                .filter(x->x.getSubscribeDate().equals(strToStr(new Date()))&&x.getStoreState().equals(0))
                .map(Subscribe::getId)
                .collect(Collectors.counting());
        //统计当前门店今日预约已到达总数
        Long totalReachSubscribe = list.stream()
                .filter(x->x.getSubscribeDate().equals(strToStr(new Date()))&&x.getStoreState().equals(1))
                .map(Subscribe::getId)
                .collect(Collectors.counting());
        List<User> user = mongoTemplate.find(Query.query(Criteria.where("id").is(userId)),User.class);
        List<Store> store= mongoTemplate.find(Query.query(Criteria.where("id").is(user.get(0).getStoreid())),Store.class);
        map.put("totalSubscribe",totalSubscribe==0?0:totalSubscribe);
        map.put("totalWaitSubscribe",totalWaitSubscribe==0?0:totalWaitSubscribe);
        map.put("totalReachSubscribe",totalReachSubscribe==0?0:totalReachSubscribe);
        map.put("store",null == store ?null : store.get(0));
        map.put("user",null == user ? null : user.get(0));
        return map;
    }
    /**
     * 根据门店id与预约状态查询
     */
    public Subscribe findByStoreIdAndState(String storeId,Integer state){
    	Subscribe subscribe= mongoTemplate.findOne(new Query(Criteria.where("store.store_id").is(storeId).and("store_state").is(state)), Subscribe.class);
    	return subscribe;
    }

    @Transactional
    @Override
    public Store updateSubScribeInfoByStoreId(String storeId){
        Store store=mongoTemplate.findOne(Query.query(Criteria.where("id").is(storeId)),Store.class);
        List<Subscribe> list=mongoTemplate.find(Query.query(Criteria.where("store.storeId").is(storeId)),Subscribe.class);
        if(store!=null&&list!=null&&list.size()>0&&StrUtil.isNotBlank(store.getStoreName())&&StrUtil.isNotBlank(store.getStorePhone())&&(!store.getStoreName().equals(list.get(0).getStore().getStoreName())||!store.getStorePhone().equals(list.get(0).getStore().getStorePhone())||!store.getLbs().getLat().equals(list.get(0).getLbs().getLat())||!store.getLbs().getLng().equals(list.get(0).getLbs().getLng()))){
            list.stream().forEach(x->{
                //设置店铺店名和店铺电话信息
                Subscribe.Store store1=x.getStore();
                store1.setStoreName(store.getStoreName());
                store1.setStorePhone(store.getStorePhone());
                x.setStore(store1);
                //设置店铺坐标系信息
                Subscribe.Lbs lbs=x.getLbs();
                lbs.setLat(store.getLbs().getLat());
                lbs.setLng(store.getLbs().getLng());
                x.setLbs(lbs);
                UpdateResult ret = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(x.getId())), MongoUtils.mongoUpdate(x), Subscribe.class);
            });
        }
        return store;
    }

}
