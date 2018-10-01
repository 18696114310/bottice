package com.ecofresh.bottice.user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Document(collection = "app_user_countPrice")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserCountPrice implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Field(value = "_id")
    private String id;
	
	/****
	 * 用户ID
	 */
	@Field(value = "user_id")
    private String userId;
	
	/***
	 * 真实姓名
	 */
	@Field(value = "name")
    private String name;
	
	/***
	 * 性别 0 女 1男 -1未知
	 */
	@Field(value = "gender")
    private Integer gender;
	
	/**
	 * 年龄
	 */
	@Field(value = "age")
	private int age;
	
	/****
     * 手机号
     */
	@Field(value = "phone")
	@Indexed(name = "un_userCountPrice_phone", unique = true)
    private String phone;
	
	/****
	 * 剩余积分 
	 */
	@Field(value = "score")
    private Integer score ;
	
}
