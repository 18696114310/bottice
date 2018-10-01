package com.ecofresh.bottice.subscribe.form;

import io.swagger.annotations.ApiModelProperty;

public class SubscribeLogForm {

    @ApiModelProperty(hidden = true)
    String id;

    /**
     * 预约记录的id
     */
    @ApiModelProperty(value = "预约记录id", example = "21ddsad1231", required = true, position = 0)
    //@NotBlank(message="预约记录id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String subscribeId;

    /**
     * 当前操作
     */
    @ApiModelProperty(value = "当前操作", example = "新增预约", required = true, position = 0)
    //@NotBlank(message="预约记录id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String opertion;

    /**
     * 操作的时间
     */
    @ApiModelProperty(value = "当前操作的时间", example = "2018-6-20 12:12:12", required = true, position = 0)
    //@NotBlank(message="预约记录id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long opertionTime;

    /**
     * 当前操作的状态
     */
    @ApiModelProperty(value = "操作状态", example = "21ddsad1231", required = true, position = 0)
    //@NotBlank(message="预约记录id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private boolean opertion_status;

}
