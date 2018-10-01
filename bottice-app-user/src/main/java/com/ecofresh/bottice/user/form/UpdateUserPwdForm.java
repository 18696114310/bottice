package com.ecofresh.bottice.user.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ecofresh.common.validator.group.AddGroup;
import com.ecofresh.common.validator.group.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/****
 * 用户表单
 * @author sunhonwei
 */
@Data
@ApiModel(value = "updateUserForm", description = "用户参数详情")
public class UpdateUserPwdForm
{
	/***
	 * 修改密码用户id
	 */
	@ApiModelProperty(value = "用户id", example = "0", required = true, position = 6)
	@NotNull(message="被修改用户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String userId;
	/****
	 * 新密码
	 */
	@ApiModelProperty(value = "新密码", example = "0", required = true, position = 6)
	@NotNull(message="新密码不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String newpwd;
	/****
	 * 旧密码
	 */
	@ApiModelProperty(value = "旧密码", example = "e10adc3949ba59abbe56e057f20f883e", required = false, position = 2)
	//@NotBlank(message="密码不能为空", groups = {AddGroup.class})
	private String oldpwd;

    /****
     * acceptType 修改密码入口，0 个人信息 1 后台管理入口
     */
    @ApiModelProperty(value = "旧密码", example = "0", required = false, position = 2)
    @NotBlank(message="密码不能为空", groups = {AddGroup.class})
    private String acceptType;
}
