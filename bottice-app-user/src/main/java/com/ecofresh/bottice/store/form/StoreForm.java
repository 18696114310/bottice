package com.ecofresh.bottice.store.form;

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
 * 门店表单
 * @author zzy
 */
@Data
@ApiModel(value = "storeForm", description = "门店参数详情")
public class StoreForm 
{
	String id;
	/****
     * 门店名称
     */
	@ApiModelProperty(value = "门店名称", example = "楚河汉街店", required = true, position = 0)
	@NotBlank(message="门店名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String storeName;
    
    /****
     * 门店电话
     */
	@ApiModelProperty(value = "门店电话", example = "152****9401", required = true, position = 1)
	@NotBlank(message="门店电话不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String storePhone;
	
	/****
     * 是否开启预约 0否 1是
     */
	@ApiModelProperty(value = "是否开启预约", example = "1", required = false, position = 2)
	@NotBlank(message="是否开启预约不能为空", groups = {AddGroup.class})
    private Integer status;
	
	/****
     * 是否删除 0是 1否
     */
	@ApiModelProperty(value = "是否删除", example = "0", required = false, position = 3)
    private Integer state;
	
	/****
     * 预约时间段
     */
	@ApiModelProperty(value = "预约时间段", example = "11:00-12:00", required = false, position = 4)
	@NotBlank(message="预约时间段不能为空", groups = {AddGroup.class})
    private List<String> timeSlot;
    
    /****
     * 门店地址
     */
	@ApiModelProperty(value = "门店地址", example = "123456", required = true, position = 5)
	@NotNull(message="门店地址", groups = {AddGroup.class, UpdateGroup.class})
    private String mapName;

    
    /****
     * 门店坐标
     */
	@ApiModelProperty(value = "门店坐标", example = "0", required = true, position = 6)
	@NotNull(message="请选择门店经度坐标", groups = {AddGroup.class, UpdateGroup.class})
	private Double lng;
	
	/****
     * 门店坐标
     */
	@ApiModelProperty(value = "门店坐标", example = "0", required = true, position = 7)
	@NotNull(message="请选择门店纬度坐标", groups = {AddGroup.class, UpdateGroup.class})
	private Double lat;
	
	/****
     * 创建人id
     */
	@ApiModelProperty(value = "创建人id", example = "0", required = true, position = 8)
    private String userId;
	
	/****
     * 修改人id
     */
	@ApiModelProperty(value = "修改人id", example = "0", required = true, position = 9)
    private String mobUserId;

}
