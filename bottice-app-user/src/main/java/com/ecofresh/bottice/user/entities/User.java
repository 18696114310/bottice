package com.ecofresh.bottice.user.entities;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Document(collection = "app_user")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@CompoundIndexes({
    @CompoundIndex(name = "un_user_phone_type", def = "{'phone': 1, 'userType': 1}", unique = true, sparse = true)
})
public class User implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	/****
	 * 用户ID
	 */
	@Id
	@Field(value = "_id")
    private String id;
	
	 /****
     * 会员卡
     */
	@Field(value = "card_no")
	@Indexed(name = "un_user_card", unique = true, sparse = true)
    private Long cardNo;

	 /****
     * 邀请码
     */
	@Field(value = "ucode")
	@Indexed(name = "un_user_ucode", unique = true, sparse = true)
    private String ucode;
	
	/****
     * 邀请二维码
     */
	@Field(value = "code_url")
    private String codeUrl;
	
	/****
     * 手机号
     */
	@Field(value = "phone")
    private String phone;
	
	/****
     * 推荐人
     */
	@Field(value = "pid")
    private String pid;
	
	/****
	 * 剩余积分 
	 */
	@Field(value = "score")
    private Integer score;
	
	/****
	 * 剩余返还积分 (因为需要乘以百分比  所以可能存在小数)
	 */
	@Field(value = "returnScore")
    private Integer returnScore;
	
	/****
     * 用户类型:0前端用户, 1后台用户
     */
	@Field(value = "userType")
    private Integer userType;

	/****
	 * 用户角色 :0管理, 1店员
	 */
	@Field(value = "role")
	private Integer role;

	/***
	 * 门店
	 */
	@Field(value = "storeid")
	private String storeid;

	/***
	 * 门店名
	 */
	/*@Field(value = "storename")
	private String storename;*/


	/****
     * 直接推荐人数
     */
	@Field(value = "directReferee")
	private Integer directReferee;
	
	/****
     * 间接推荐人数
     */
	@Field(value = "indirectReferee")
	private Integer indirectReferee;
	/**
	 * 入职日期;
	 */
	@Field(value = "hiredate")
	private String hiredate;
	/****
     * 位置信息
     */
	@Field(value = "loc")
    private String loc;
	
	/****
	 * 状态
	 * 1   正常
	 * 0  禁用
	 * -1 删除
	 */
	@Field(value = "state")
    private Integer state = 1;
	
	/***
	 * 最后登录时间
	 */
	@Field(value = "ls")
    private Long ls;
	
	/***
	 * 创建时间
	 */
	@Field(value = "cs")
	@Indexed(name = "in_user_cs", direction = IndexDirection.DESCENDING, unique = false)
    private Long cs;

	/***
	 * 更新时间
	 */
	@Field(value = "ms")
    private Long ms;
	
	/***
	 * 更新人
	 */
	@Field(value = "msId")
    private String msId;
    
	/***
	 * 用户隐私信息
	 */
	@Field(value = "privacy")
    private Privacy privacy;
	
	@Data
	public class Privacy
	{
		/***
		 * 真实姓名
		 */
		@Field(value = "name")
	    private String name;
		
		/***
		 * 密码
		 */
	    @JsonIgnore
		@Field(value = "pwd")
	    private String pwd;
	}
	
	@Field(value = "visible")
    private Visible visible;
	
	@Data
	public class Visible
	{
		/***
		 * 昵称
		 */
		@Field(value = "nick_name")
	    private String nickName;

		/***
		 * 头像
		 */
		@Field(value = "avator")
	    private String avator;
		
		/***
		 * 认证状态 
		 * 0    未认证
		 * 1    待认证
		 * 2    认证通过
		 *-2    认证失败
		 */
		@Field(value = "id_auth")
	    private Integer idAuth = 0;
		
		/***
		 * 性别 0 女 1男 -1未知
		 */
		@Field(value = "gender")
	    private Integer gender = -1;

		/****
		 * 兴趣爱好
		 */
		@Field(value = "hobbies")
	    private String[] hobbies;
		
		/****
		 * 生日
		 */
		@Field(value = "birthday")
	    private String birthday;
		/**
		 * 年龄
		 */
		@Field(value = "age")
		private int age;
		/***
		 * 地址
		 */
		@Field(value = "address")
	    private String address;
		
		/***
		 * 个性签名
		 */
		@Field(value = "sign")
	    private String sign;

		/***
		 * 个人介绍(备注)
		 */
		@Field(value = "remark")
	    private String remark;
		
		/***
		 * 职业
		 */
		@Field(value = "pro")
	    private String[] pro;
		
		/***
		 * 才艺技能
		 */
		@Field(value = "skills")
	    private String[] skills;
		
		/***
		 * 邮箱
		 */
		@Field(value = "email")
	    private String email;
		
		/***
		 * 视频介绍
		 */
		@Field(value = "video")
	    private String video;
	    
	    /***
		 * 背景
		 */
		@Field(value = "backgrounds")
	    private String[] backgrounds;
	}
	
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

	/*****
	 * 微信相关
	 */
	@Field(value = "wx")
	private Wx wx;
	
	@Data
	public class Wx
	{
		@Field(value = "openid")
		@Indexed(name = "un_openid", unique = true, sparse = true)
	    private String openid;
		
		@Field(value = "session_key")
	    private String sessionKey;
		
		@Field(value = "unionid")
	    private String unionid;
		
		@Field(value = "nick_name")
	    private String nickName;
		
		@Field(value = "gender")
	    private String gender;
		
		@Field(value = "language")
	    private String language;
		
		@Field(value = "city")
	    private String city;
		
		@Field(value = "province")
	    private String province;
		
		@Field(value = "country")
	    private String country;
		
		@Field(value = "avatar")
	    private String avatar;
		
		@Field(value = "timestamp")
	    private String timestamp;
	}
	
	Map<String,Object> pUserInfo;
}
