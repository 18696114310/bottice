package com.ecofresh.bottice.store.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.ecofresh.bottice.order.entities.Order;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.store.entities.StorePrice;
import com.ecofresh.bottice.store.form.StoreForm;
import com.ecofresh.bottice.store.form.ShowStoreIntegralForm;

public interface StoreService
{
	/***
     * 分页查询用户信息
     * @param id
     * @return
     */
	public Page<Store> find(int page, int rows, Store dto);
    
    /****
	 * 根据ID 删除门店信息
	 * @param id
	 */
    public boolean delete(String ...ids);
	
    /****
     * 分页查询 用户信息
     * @param page 页数
     * @param row  每页条数
     * @param name
     * @return
     */
    public Collection<Store> find(int page, int row, final String name);
    
    /***
     * 增加门店信息
     * @param dto
     * @return
     */
    public Store insert(StoreForm dto);

    /***
     * 根据ID 查询
     * @param id 门店id
     * @return
     */
    public Store findById(String id);
    
    /****
	 * 根据ID 假删除门店信息
	 * @param id
	 */
    public boolean delById(Store store);
    
    /***
     * 修改门店信息
     * @param dto
     * @return
     */
    public boolean update(StoreForm dto);
    
    /***
     * 查询当前门店今日报表信息
     */
    public StorePrice find(String userId,String storeid);
    
    /***
     * 查询所有门店
     */
    public List<Store> findAll();
    
    /***
     * 根据门店id查询所查门店报表信息  管理员可以查看所有门店的信息
     */
    public List<StorePrice> findStoreOrderById(ShowStoreIntegralForm form);
    /**
     * 根据id查询缓存信息
     * @param id
     * @return
     */
	public Store findCacheById(String id);

	public void delCacheById(String id);

}
