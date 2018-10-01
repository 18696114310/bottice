package com.ecofresh.bottice.store.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Document(collection = "app_store_price")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorePrice implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Field(value = "_id")
    private String id;
	
	/****
	 * 门店ID
	 */
	@Field(value = "storeid")
	@Indexed(name = "in_storeid_cs", unique = false)
    private String storeid;
    
    /****
     * 充值积分
     */
	@Field(value = "rechargeIntegral")
    private Long rechargeIntegral = (long) 0;
	
	/****
     * 充值积分人次
     */
	@Field(value = "rechargeIntegralCount")
    private Integer rechargeIntegralCount = 0;
	
	/****
     * 日常消费积分
     */
	@Field(value = "dayConsumptionIntegral")
    private Long dayConsumptionIntegral = (long) 0;
	
	/****
     * 日常消费积分人次
     */
	@Field(value = "dayConsumptionIntegralCount")
    private Integer dayConsumptionIntegralCount = 0;
	
	/****
     * 大健康消费积分
     */
	@Field(value = "healthyIntegral")
    private Long healthyIntegral = (long) 0;
	
	/****
     * 大健康消费积分人次
     */
	@Field(value = "healthyIntegralCount")
    private Integer healthyIntegralCount = 0;
	
	/****
     * 退款积分
     */
	@Field(value = "refundIntegral")
    private Long refundIntegral = (long) 0;
	
	/****
     * 退款积分人次
     */
	@Field(value = "refundIntegralCount")
    private Integer refundIntegralCount = 0;
	
	/****
     * 返还消费积分
     */
	@Field(value = "returnIntegral")
    private Long returnIntegral = (long) 0;
	
	/****
     * 返还消费积分人次
     */
	@Field(value = "returnIntegralCount")
    private Integer returnIntegralCount = 0;
	
	/***
	 * 创建时间（以年月日为时间戳）
	 */
	@Field(value = "cs")
	@Indexed(name = "in_StorePrice_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;

	
}
