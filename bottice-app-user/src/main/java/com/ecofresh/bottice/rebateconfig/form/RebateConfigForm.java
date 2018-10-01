package com.ecofresh.bottice.rebateconfig.form;

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
 * 返还积分配置表单
 * @author zzy
 */
@Data
@ApiModel(value = "rebateonfigForm", description = "返还积分配置参数详情")
public class RebateConfigForm 
{
	String id;
	/****
     * 0：日常消费；1大健康消费
     */
	@ApiModelProperty(value = "返还积分配置类型", example = "0", required = true, position = 0)
	@NotBlank(message="返还积分配置类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String status;
    
    /****
     * 一级返利百分比
     */
	@ApiModelProperty(value = "一级返利百分比", example = "30", required = true, position = 1)
	@NotBlank(message="一级返利百分比不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String rebateOne;
	
	/****
     * 二级返利百分比
     */
	@ApiModelProperty(value = "二级返利百分比", example = "20", required = true, position = 2)
	@NotBlank(message="二级返利百分比不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String rebateTwo;
	
	/****
     * 创建人id
     */
	@ApiModelProperty(value = "创建人id", example = "0", required = true, position = 3)
    private String userId;
	
	/****
     * 修改人id
     */
	@ApiModelProperty(value = "修改人id", example = "0", required = true, position = 4)
    private String mobUserId;


}
