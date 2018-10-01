package com.ecofresh.bottice.subscribe.service;

import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.subscribe.entities.Subscribe;
import com.ecofresh.bottice.subscribe.form.SubscribeForm;
import com.ecofresh.bottice.user.entities.User;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SubscribeService {

    /***
     * 分页查询预约记录信息(个人版)
     * @param page
     * @param rows
     * @param subscribe
     * @return Page
     */
    public Page<Subscribe> find(int page, int rows, Subscribe subscribe);

    /***
     * 分页查询预约记录信息(管理员版)
     * @param page
     * @param rows
     * @param subscribe
     * @return Page
     */
    public Page<Subscribe> findByAdmin(int page, int rows, Subscribe subscribe, List<User> list,Integer status);

    /****
     * 分页查询 用户信息
     * @param page 页数
     * @param row  每页条数
     * @param name
     * @return
     */
    public Collection<Subscribe> find(int page, int row, final String name);

    /**
     * 取消预约或者完成信息
     * @param SubscribeFor
     * @return
     */
    public Subscribe update(String id,Integer status,String code);

    /***
     * 增加预约信息
     * @param dto
     * @return
     */
    public Subscribe insert(SubscribeForm dto);

    /**
     * 查询一个文档
     * @param code
     * @return
     */
    public Subscribe findone(String code);

    /**
     * 统计各个门店的预约信息
     */
    public Map mapReduceInfo(String storeId,String userId);

    /**
     *
     * @param id
     * @return
     */
    public Subscribe findOneById(String id);

    /**
     *
     * @param id
     * @param sessionId
     * @return
     */
    public Map findOneAdminById(String id);

    /**
     *
     * @param storeId
     * @return
     */
    public Store updateSubScribeInfoByStoreId(String storeId);

    /**
     *
     * @param storeId
     * @param state
     * @return
     */
    public Subscribe findByStoreIdAndState(String storeId,Integer state);

    /**
     *
     * @param subscribe
     */
    public void updateCurrentObject(Subscribe subscribe);
}
