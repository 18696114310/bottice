package com.ecofresh.bottice.subscribe.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "app_subscribe_log")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SubscribeLog implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    /****
     * 预约记录日志id
     */
    @Id
    @Field(value = "_id")
    private String id;

    /**
     * 预约记录的id
     */
    @Field(value = "subscribe_id")
    private String subscribeId;

    /**
     * 当前操作预约的操作是什么
     */
    @Field(value = "opertion")
    private String opertion;


    @Field(value = "opertion_code")
    private String opertionCode;

    /**
     * 当前操作开始的时间
     */
    @Field(value = "opertion_st")
    private Long opertionStartTime;

    /**
     * 当前操作结束的时间
     */
    @Field(value = "opertion_et")
    private Long opertionEndTime;

    /**
     * 总共耗时
     */
    @Field(value = "pay_time")
    private Long payTime;


    /**
     * 当前操作是否成功(0表示失败,1表示成功,2表示超时)
     */
    @Field(value = "opertion_status")
    private Integer opertionStatus;

    /**
     * 当前操作的人是谁
     */
    @Field(value = "opertion_userId")
    private String opertionUserId;
}
