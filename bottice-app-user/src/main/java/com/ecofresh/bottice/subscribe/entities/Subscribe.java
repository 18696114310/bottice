package com.ecofresh.bottice.subscribe.entities;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "app_subscribe")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Subscribe implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    /****
     * 预约记录id
     */
    @Id
    @Field(value = "_id")
    private String id;

    /**
     * 预约状态(0表示待到达，1表示已完成，2表示已过期，3表示已取消,4表示全部)
     */
    @Field(value = "store_state")
    private Integer storeState=0;

    /**
     * 用户id
     */
    @Field(value = "user_id")
    private String userId;

    /**
     * 用户真实姓名
     */
    @Field(value = "user_name")
    private String userName;

    @Field(value = "store")
    private Store store;

    @Data
    public class Store
    {
        @Field(value = "store_id")
        @Indexed(name = "s_id", sparse = true)
        private String storeId;

        /****
         * 店面名称
         */
        @Field(value = "store_name")
        private String storeName;

        /**
         * 店面电话
         */
        @Field(value="store_phone")
        private String storePhone;
    }


    /**
     * 预约日期
     */
    @Field(value = "subscribe_date")
    private String subscribeDate;

    /**
     * 预约时间
     */
    @Field(value = "subscribe_time")
    private String subscribeTime;

    /**
     * 取消预约的时间
     */
    @Field(value = "cancel_subscribe_time")
    private Long cancelSubscribeTime;

    /**
     * 完成预约的时间
     */
    @Field(value = "reach_subscribe_time")
    private Long reachSubscribeTime;

    /**
     * 新增预约时间
     */
    @Field(value = "add_time")
    private Date addTime;
    
    /***
	 * 创建时间
	 */
	@Field(value = "cs")
	@Indexed(name = "in_subscribe_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;


    /*****
     * 地理位置信息
     */
    @Field(value = "lbs")
    @GeoSpatialIndexed(name = "lbs_user_2d", type = GeoSpatialIndexType.GEO_2DSPHERE)
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
