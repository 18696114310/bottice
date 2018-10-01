package com.ecofresh.bottice.store.form;

import javax.validation.constraints.NotBlank;

import com.ecofresh.common.validator.group.AddGroup;
import com.ecofresh.common.validator.group.UpdateGroup;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/****
 * 门店表单
 * @author zzy
 */
@Data
@ApiModel(value = "showStoreIntegralForm", description = "门店参数详情")
public class ShowStoreIntegralForm 
{
	private String userId;
	
	@NotBlank(message="门店不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String storeId;
    
    /****
     * 展示方式 0：最近30天，1：本月；2，上月；3上周
     */
	@NotBlank(message="展示方式不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String state;
	
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
	@NotBlank(message="消费类型不能为空", groups = {AddGroup.class})
    private String ordertype;

}
