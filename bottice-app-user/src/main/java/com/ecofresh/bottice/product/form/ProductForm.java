package com.ecofresh.bottice.product.form;

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
 * 产品表单
 * @author zzy
 */
@Data
@ApiModel(value = "productForm", description = "产品参数详情")
public class ProductForm 
{
	String id;
	/****
     * 产品名称
     */
	@ApiModelProperty(value = "产品名称", example = "楚河汉街店", required = true, position = 0)
	@NotBlank(message="产品名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String productName;
    
    /****
     * 产品价格
     */
	@ApiModelProperty(value = "产品价格", example = "30", required = true, position = 1)
	@NotBlank(message="产品价格不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String productPrice;
	
	/****
     * 是否删除 0是 1否
     */
	@ApiModelProperty(value = "是否删除", example = "1", required = false, position = 2)
    private String state;
    
    /****
     * 产品说明
     */
	@ApiModelProperty(value = "产品说明", example = "123456", required = true, position = 3)
	//@NotNull(message="产品说明不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String explain;
	
	/****
     * 创建人id
     */
	@ApiModelProperty(value = "创建人id", example = "0", required = true, position = 4)
    private String userId;
	
	/****
     * 修改人id
     */
	@ApiModelProperty(value = "修改人id", example = "0", required = true, position = 5)
    private String mobUserId;


}
