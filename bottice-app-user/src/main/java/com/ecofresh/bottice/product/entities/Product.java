package com.ecofresh.bottice.product.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "app_product")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Product implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	/****
	 * 产品ID
	 */
	@Id
	@Field(value = "_id")
    private String id;

	/****
     * 产品名称
     */
	@Field(value = "productName")
    private String productName;
    
    /****
     * 产品价格
     */
	@Field(value = "productPrice")
    private String productPrice;
	
	
	/****
     * 是否删除 0是 1否
     */
	@Field(value = "state")
    private Integer state = 1;
	
	/****
     * 产品说明
     */
	@Field(value = "explain")
    private String explain;
	
	/****
     * 创建人id
     */
	@Field(value = "userId")
    private String userId;
	
	/****
     * 修改人id
     */
	@Field(value = "mobUserId")
    private String mobUserId;
	
	/***
	 * 创建时间
	 */
	@Field(value = "cs")
	@Indexed(name = "in_product_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;

	/***
	 * 更新时间
	 */
	@Field(value = "ms")
    private Long ms;
    
	
}
