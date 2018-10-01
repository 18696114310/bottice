package com.ecofresh.bottice.order.form;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

import com.ecofresh.common.validator.annotation.Mobile;
import com.ecofresh.common.validator.group.AddGroup;
import com.ecofresh.common.validator.group.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/****
 * 积分订单表单
 * @author zzy
 */
@Data
@ApiModel(value = "orderForm", description = "产品参数详情")
public class OrderForm 
{
	String id;
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
     * 		 5.扣除用户退款(用户积分减少)
     * 		扣除返还积分操作:
     * 		 6.扣除返还积分 (用户返还积分减少)
     */
	@ApiModelProperty(value = "订单类型", example = "0", required = true, position = 0)
	@NotBlank(message="订单类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String ordertype;
    
    /****
     * 产品价格
     */
	@ApiModelProperty(value = "订单金额", example = "30", required = true, position = 1)
	@NotBlank(message="订单金额不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String orderPrice;
	
	/****
     * 是否删除 0是 1否
     */
	@ApiModelProperty(value = "是否删除", example = "1", required = false, position = 2)
    private String state;
	
	/****
     * 操作人id
     */
	@ApiModelProperty(value = "操作人id", example = "0", required = true, position = 4)
    private String userId;
	
	/****
     * 用户id(被操作的用户/返利用户)
     */
	@ApiModelProperty(value = "用户id", example = "0", required = true, position = 5)
	@NotBlank(message="用户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String usersId;


}
