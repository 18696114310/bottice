package com.ecofresh.bottice.order.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Document(collection = "app_order")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Order implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	/****
	 * 订单ID
	 */
	@Id
	@Field(value = "_id")
    private String id;
	
	/****
	 * 店员与管理员可以无积分限制给会员充值，
	 * 扣除的积分不需要给店员或者管理员,只有大健康与日常消费时需要返利给上两级用户;
	 * 充值积分与返还积分不可同时使用，充值积分只可以用作大健康消费,日常消费，一级退款。返还积分只可以用于扣除返还积分操作
     * 订单类型:
     * 		 充值操作:
     * 		 0.用户充值(用户积分增加)，
     * 		扣除日常消费积分操作:
     * 		 1.用户扣除日常消费积分(用户积分减少)，2.日常返利积分反利(两位上级用户分别增加系统设置的返利百分比值)
     * 		扣除大健康积分操作:
     * 		 3.用户扣除大健康消费积分(用户积分减少)，4.大健康返利积分反利(两位上级用户分别增加系统设置的返利百分比值)
     * 		退款操作:
     * 		 5.用户退款(用户积分减少)
     * 		扣除返还积分操作:
     * 		 6.扣除返还积分 (用户返还积分减少)
     */
	@Field(value = "ordertype")
	@Indexed(name = "in_order_ordertype",  unique = false)
    private Integer ordertype;
    
    /****
     * 订单价格
     */
	@Field(value = "orderPrice")
    private Integer orderPrice;
	/***
	 * 剩余订单价格
	 */
	@Field(value = "surplusPrice")
    private Integer surplusPrice;
    
    /****
     * 返利订单号(返利状态下产生)
     */
    @Field(value = "orderNum")
	@Indexed(name = "in_order_orderNum",  unique = false)
    private String orderNum;
	
	/****
     * 是否删除 0是 1否
     */
    @Field(value = "state")
    private Integer state;
	
	/****
     * 操作人id
     */
	@Field(value = "userId")
	@Indexed(name = "in_order_userId",  unique = false)
    private String userId;
	
	/****
     * 用户id(被操作的用户)
     */
	@Field(value = "usersId")
	@Indexed(name = "in_order_usersId",  unique = false)
    private String usersId;
	
	/***
	 * 门店
	 */
	@Field(value = "storeid")
	@Indexed(name = "in_order_storeid",  unique = false)
	private String storeid;
	
	/****
     * 接收返利人id
     */
	@Field(value = "rebateUserId")
	@Indexed(name = "in_order_rebateUserId",  unique = false)
    private String rebateUserId;

	/***
	 * 创建时间
	 */
	@Field(value = "cs")
	@Indexed(name = "in_order_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;
	
	private String userName;
	
	private String storeName;

}
