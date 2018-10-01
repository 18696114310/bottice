package com.ecofresh.bottice.subscribe.repository;

import com.ecofresh.bottice.subscribe.entities.Subscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SubscribeRepository extends MongoRepository<Subscribe, String> {

    @Query("{?0:?1}")
    public List<Subscribe> find(String key, String value);

    /**
     * 有@Query声明查询, 方法名不需要严格遵守规定
     * 根据 手机号，和昵称搜索
     * @param phone 	手机号
     * @param nickname	昵称
     * @param pageable	分页
     * @return
     */
    @Query("{$and: [{'$or':[{'phone':?0}, {'visible.nickName':{'$regex':?1, $options:'si'}}]}]}")
    public Page<Subscribe> find(final String phone, final String nickname, Pageable pageable);

    /**
     * 根据相关条件查询出预约信息
     * @param userName
     * @param storeid
     * @param sudate
     * @param pageable
     * @return
     */
    @Query("{$and: [{'userName':{'$regex':?0}},{'store.storeId':?1},{'subscribeDate':?2},{'storeState':{'$in':[0,1,2,3]}}]}")
    public Page<Subscribe> findByName(String userName,String storeid,String sudate,Pageable pageable);

    /**
     * 根据相关条件查询出预约信息
     * @param storeid
     * @param sudate
     * @param pageable
     * @return
     */
    @Query("{$and: [{'store.storeId':?0},{'subscribeDate':?1},{'storeState':{'$in':[0,1,2,3]}}]}")
    public Page<Subscribe> findByStoreId(String storeid,String sudate,Pageable pageable);
}
