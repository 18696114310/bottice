package com.ecofresh.bottice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UCache
{
	/****
     * 用戶ID
     */
    private String id;
    
    /****
     * 昵称
     */
    private String nickName;
    
    /****
     * 手机号
     */
    private String phone;
    
    /****
     * 邀请码
     */
    private Long ucode;

    /****
     * 头像
     */
    private String avator;
    
    /****
     * 性别
     */
    private Integer gender;
    
    /****
     * 生日
     */
    private String birthday;
    
    /****
	 * 剩余积分
	 */
    private Long score = 0L;
    
    /****
	 * 认证状态 
	 * 0    未认证
	 * 1    待认证
	 * 2    认证通过
	 *-2    认证失败
	 */
    private Integer idAuth = 0;
}
