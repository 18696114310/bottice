package com.ecofresh.bottice.rebateconfig.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecofresh.bottice.rebateconfig.entities.RebateConfig;
import com.ecofresh.bottice.rebateconfig.form.RebateConfigForm;

public interface RebateConfigService
{
	/***
     * 分页查询返利配置信息
     * @param id
     * @return
     */
	public Page<RebateConfig> find(int page, int rows, RebateConfig dto);
    
    /****
	 * 根据ID 删除返利配置信息
	 * @param id
	 */
    public boolean delete(String ...ids);
	
    
    /***
     * 增加返利配置信息
     * @param dto
     * @return
     */
    public RebateConfig insert(RebateConfigForm dto);

    /***
     * 根据ID 查询
     * @param id 返利配置id
     * @return
     */
    public RebateConfig findById(String id);
    
    /****
	 * 根据ID 假删除返利配置信息
	 * @param id
	 */
    public boolean delById(RebateConfig store);
    
    /***
     * 修改返利配置信息
     * @param dto
     * @return
     */
    public boolean update(RebateConfigForm dto);
    
    /***
     * 查询所有未删除的返利配置信息
     * @param state
     * @return
     */
    public List<RebateConfig> findAll();

}
