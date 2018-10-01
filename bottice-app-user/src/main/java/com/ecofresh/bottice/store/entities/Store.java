package com.ecofresh.bottice.store.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "app_store")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Store implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	/****
	 * 门店ID
	 */
	@Id
	@Field(value = "_id")
    private String id;

	/****
     * 门店名称
     */
	@Field(value = "storeName")
    private String storeName;
    
    /****
     * 门店电话
     */
	@Field(value = "storePhone")
    private String storePhone;
	
	/****
     * 是否开启预约 0否 1是
     */
	@Field(value = "status")
    private Integer status;
	
	/****
     * 是否删除 0是 1否
     */
	@Field(value = "state")
    private Integer state;
	
	/****
     * 预约时间段
     */
	@Field(value = "timeSlot")
    private List<String> timeSlot;
    
    /****
     * 门店地址
     */
	@Field(value = "mapName")
    private String mapName;

    
    /****
     * 门店坐标
     */
	@Field(value = "mapCoordinate")
    private String mapCoordinate;
	
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
	@Indexed(name = "in_store_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;

	/***
	 * 更新时间
	 */
	@Field(value = "ms")
    private Long ms;
    
	/*****
	 * 地理位置信息
	 */
	@Field(value = "lbs")
	@GeoSpatialIndexed(name = "lbs_store_2d", type = GeoSpatialIndexType.GEO_2DSPHERE)
	private Lbs lbs;
	
	@Data
	public class Lbs
	{
		@Field(value = "lng")
	    private Double lng;
		
		@Field(value = "lat")
	    private Double lat;
	}
	
}
