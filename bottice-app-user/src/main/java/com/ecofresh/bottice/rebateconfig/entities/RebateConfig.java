package com.ecofresh.bottice.rebateconfig.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Document(collection = "app_rebate_config")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RebateConfig implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	/****
	 * 返利配置ID
	 */
	@Id
	@Field(value = "_id")
    private String id;

	/****
     * 0：日常消费；1大健康消费
     */
	@Field(value = "status")
	private Integer status;
    
	/****
     * 一级返利百分比
     */
	@Field(value = "rebateOne")
    private Double rebateOne;
	
	/****
     * 二级返利百分比
     */
	@Field(value = "rebateTwo")
    private Double rebateTwo;
	
	/****
     * 是否删除 0是 1否
     */
	@Field(value = "state")
    private Integer state;

    
	
	/****
     * 创建人id
     */
	@Field(value = "userId")
    private String userId;
	
	/****
     * 修改人id
     */
    @Field(value = "mobUserId")
    private String mobUserId;
	
	/***
	 * 创建时间
	 */
	@Field(value = "cs")
	@Indexed(name = "in_rebateConfig_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;

	/***
	 * 更新时间
	 */
	@Field(value = "ms")
    private Long ms;
    
	
}
