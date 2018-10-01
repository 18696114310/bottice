package com.ecofresh.bottice.user.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;
import org.springframework.data.mongodb.core.mapping.Field;

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
@ApiModel(value = "backStateForm", description = "用户参数详情")
public class BackStatageUserForm
{
	private String id;
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
	//@NotBlank(message="密码不能为空", groups = {AddGroup.class})
    private String pwd;
    
    /****
     * 邀请码
     */
	@ApiModelProperty(value = "邀请码", example = "JZLPR2FL", required = false, position = 3)
	//@NotBlank(message="", groups = {AddGroup.class, UpdateGroup.class})
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
	//@NotNull(message="请上传头像", groups = {AddGroup.class, UpdateGroup.class})
	//@URL(message="头像地址有误", groups = {AddGroup.class, UpdateGroup.class})
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
	@ApiModelProperty(value = "用户类型", example = "0", required = true, position = 7)
	@NotNull(message="", groups = {AddGroup.class, UpdateGroup.class})
	private Integer userType;

	/****
	 * 用户角色 :0管理, 1店员
	 */
	@ApiModelProperty(value = "用户角色", example = "0", required = false, position = 7)
	//@NotNull(message="", groups = {AddGroup.class, UpdateGroup.class})
	private Integer role;

	@ApiModelProperty(value = "店别", example = "5b20cfc392168b02b440f1ec", required = true, position = 8)
	@NotNull(message="", groups = {AddGroup.class, UpdateGroup.class})
	private String storeid;

    /****
     * 入职日期
     */
    @ApiModelProperty(value = "入职日期", example = "2018-06-111", required = true, position = 6)
    @NotNull(message="入职日期不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String hiredate;
    
    /****
	 * 状态
	 * 1   正常
	 * 0  禁用
	 * -1 删除
	 */
    @ApiModelProperty(value = "状态", example = "1", required = true, position = 6)
    private Integer state;
}
