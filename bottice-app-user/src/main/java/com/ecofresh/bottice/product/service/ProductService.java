package com.ecofresh.bottice.product.service;

import java.util.Collection;

import org.springframework.data.domain.Page;

import com.ecofresh.bottice.product.entities.Product;
import com.ecofresh.bottice.product.form.ProductForm;
import com.ecofresh.bottice.store.entities.Store;
import com.ecofresh.bottice.store.form.StoreForm;

public interface ProductService
{
	/***
     * 分页查询用户信息
     * @param id
     * @return
     */
	public Page<Product> find(int page, int rows, Product dto);
    
    /****
	 * 根据ID 删除产品信息
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
    public Collection<Product> find(int page, int row, final String name);
    
    /***
     * 增加产品信息
     * @param dto
     * @return
     */
    public Product insert(ProductForm dto);

    /***
     * 根据ID 查询
     * @param id 产品id
     * @return
     */
    public Product findById(String id);
    
    /****
	 * 根据ID 假删除产品信息
	 * @param id
	 */
    public boolean delById(Product store);
    
    /***
     * 修改产品信息
     * @param dto
     * @return
     */
    public boolean update(ProductForm dto);

}
