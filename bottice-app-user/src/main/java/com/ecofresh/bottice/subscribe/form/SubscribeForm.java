package com.ecofresh.bottice.subscribe.form;

import com.ecofresh.common.validator.group.AddGroup;
import com.ecofresh.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "subscribeForm", description = "记录参数详情")
public class SubscribeForm {

    @ApiModelProperty(hidden = true)
    String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "21ddsad1231", required = true, position = 0)
   // @NotBlank(message="用户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String userId;

    /**
     * 商店的id
     */
    @ApiModelProperty(value = "店的id", example = "2231dsadas", required = true, position = 1)
    @NotBlank(message="店的id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String storeId;

    /**
     * 店名
     */
    @ApiModelProperty(value = "店名", example = "楚河汉街店", required = true, position = 1)
    @NotBlank(message="店名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String storeName;

    /**
     * 店面电话
     */
    @ApiModelProperty(value = "店面电话", example = "027-88645678", required = true, position = 2)
    @NotBlank(message="店面电话不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String storePhone;

    /**
     * 预约日期不能为空
     */
    @ApiModelProperty(value = "预约日期", example = "2018-08-30", required = true, position = 3)
    @NotNull(message="预约日期不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String subscribeDate;

    /**
     * 预约时间段不能为空
     */
    @ApiModelProperty(value = "预约时间", example = "11:00-12:00", required = true, position = 4)
    @NotBlank(message="预约时间段不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String subscribeTime;

    /**
     * 预约状态
     */
    @ApiModelProperty(value = "预约状态", example = "", required = true, position = 5)
    @NotNull(message="", groups = {AddGroup.class, UpdateGroup.class})
    private Integer storeState=0;

    /****
     * 门店坐标
     */
    @ApiModelProperty(value = "门店坐标", example = "0", required = true, position = 6)
    @NotNull(message="请选择门店经度坐标", groups = {AddGroup.class, UpdateGroup.class})
    private Double lng;

    /****
     * 门店坐标
     */
    @ApiModelProperty(value = "门店坐标", example = "0", required = true, position = 7)
    @NotNull(message="请选择门店纬度坐标", groups = {AddGroup.class, UpdateGroup.class})
    private Double lat;

}
