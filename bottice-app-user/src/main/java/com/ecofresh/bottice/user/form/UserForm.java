package com.ecofresh.bottice.user.form;

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
 * 用户表单
 * @author sunhonwei
 */
@Data
@ApiModel(value = "userForm", description = "用户参数详情")
public class UserForm
{
	/****
     * 名称
     */
	@ApiModelProperty(value = "姓名", example = "张三", required = true, position = 0)
	@NotBlank(message="姓名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;
    
    /****
     * 手机号
     */
	@ApiModelProperty(value = "手机号", example = "152****9401", required = true, position = 1)
	@NotBlank(message="手机号不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@Mobile(message="手机号输入有误", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;
	
	/****
     * 密码
     */
	@ApiModelProperty(value = "密码", example = "e10adc3949ba59abbe56e057f20f883e", required = false, position = 2)
	@NotBlank(message="密码不能为空", groups = {AddGroup.class})
    private String pwd;
    
    /****
     * 邀请码
     */
	@ApiModelProperty(value = "邀请码", example = "JZLPR2FL", required = true, position = 3)
	@NotBlank(message="邀请码不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String ucode;
	
	/****
     * openid
     */
	@ApiModelProperty(value = "微信授权码", example = "e10adc3949ba59abbe56e057f20f883e", required = false, position = 2)
	@NotBlank(message="微信授权码不能为空", groups = {AddGroup.class})
    private String openid;

    /****
     * 头像
     */
	@ApiModelProperty(value = "头像", example = "http://www.baidu.com/a.png", required = true, position = 4)
	@NotNull(message="请上传头像", groups = {AddGroup.class, UpdateGroup.class})
	@URL(message="头像地址有误", groups = {AddGroup.class, UpdateGroup.class})
    private String avator;
    
    /****
     * 性别
     */
	@ApiModelProperty(value = "性别(0女1男)", example = "0", required = true, position = 5)
	@NotNull(message="请选择性别", groups = {AddGroup.class, UpdateGroup.class})
    private Integer gender;
    
    /****
     * 生日
     */
	@ApiModelProperty(value = "生日", example = "1987-08-14", required = true, position = 6)
	@NotNull(message="生日不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String birthday;

	/****
	 * 用户类型
	 */
	@ApiModelProperty(value = "用户类型", example = "0", required = true, position = 6)
	@NotNull(message="", groups = {AddGroup.class, UpdateGroup.class})
	private String userType;

	/****
	 * 新密码
	 */
	@ApiModelProperty(value = "新密码", example = "0", required = true, position = 6)
	//@NotNull(message="", groups = {AddGroup.class, UpdateGroup.class})
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
    //@NotBlank(message="密码不能为空", groups = {AddGroup.class})
    private String acceptType;
}
