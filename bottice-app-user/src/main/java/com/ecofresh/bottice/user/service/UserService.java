package com.ecofresh.bottice.user.service;

import com.ecofresh.bottice.user.dto.UCache;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.user.entities.UserCountPrice;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService
{
	/***
     * 分页查询用户信息
     * @param id
     * @return
     */
	public Page<User> find(int page, int rows, User dto);

    /***
     * 分页查询用户信息
     * @param id
     * @return
     */
    public Page<User> findManager(int page, int rows, String key);
	/****
	 * 根据手机号修改信息
	 * @param id
	 * @param map
	 */
    public void upById(String id, Map<String, Object> map);

    /****
	 * 根据电话号码修改信息
	 * @param id
	 * @param map
	 */
    public boolean upByPhone(String mobile, Map<String, Object> map);


    /****
	 * 根据ID 删除用户信息
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
    public Page<User> find(int page, int row, final String name);

    public User findCacheById(String id);

    /***
     * 根据缓存查找用户信息
     * @param id
     * @return
     */
    public UCache findCache(String id);

    /***
     * 根据ID删除缓存用户信息
     * @param id
     * @return
     */
    public void delCacheById(String id);

    /***
     * 增加用户信息
     * @param dto
     * @return
     */
    public User insert(User dto);

    /***
     * 用戶注冊
     * @param mobile pass
     * @return
     * @throws NimException
     */
    public User insert(String name, String phone, String pwd, String ucode, String avator, Integer gender, String birthday, String openid);

    /***
     * 用戶注冊
     * @param mobile pass
     * @return
     * @throws NimException
     */
    public User insert(String name, String phone, String pwd, String ucode, String avator, Integer gender, String birthday, String openid,Integer usertype,String storeid,String hiredate,Integer role);


    /***
     * 判斷邀请码是否存在
     * @param dto
     * @return
     */
    public boolean existsCode(String code);

    /***
     * 根据ID 查询
     * @param code 邀请码
     * @return
     */
    public User findById(String id);

    /***
     * 根据邀请码，获取用户信息
     * @param code 邀请码
     * @return
     */
    public User findByCode(String code);

    /***
     * 根据用户名号码查询前端用户信息
     * @param dto
     * @return
     */
    public User findByPhone(String phone);

    /***
     * 根据用户电话查询管理信息
     * @param dto
     * @return
     */
    public User findMangerByPhone(String phone);
    /***
     * 根据OpenId查询用户信息
     * @param dto
     * @return
     */
    public User findByWxOpenId(String openid);

    /**
     *统计一级用户加上二级和三级用户消费金额
     */
    public Collection<UserCountPrice> mapReduceInfo(int page,int row,final String key);
    
    /***
     * 修改用户信息
     * @param mobile pass
     * @return
     * @throws NimException
     */
    public void updateById(String id,String userId,String name,Integer gender, String birthday,String phone, String storeid,Integer usertype,String hiredate,Integer state,Integer role);

    /**
     * 根据用户id查询出所有用户
     * @param user
     * @return
     */
    public List<User> findUserByUsername(User user);

}
