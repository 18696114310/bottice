package com.ecofresh.bottice.controller.subscribe;

import cn.hutool.core.util.StrUtil;
import com.ecofresh.bottice.constant.Constant;
import com.ecofresh.bottice.controller.ApiAbstractController;
import com.ecofresh.bottice.enums.ErrorDesc;
import com.ecofresh.bottice.subscribe.entities.Subscribe;
import com.ecofresh.bottice.subscribe.entities.SubscribeLog;
import com.ecofresh.bottice.subscribe.form.SubscribeForm;
import com.ecofresh.bottice.subscribe.service.SubscribeLogService;
import com.ecofresh.bottice.subscribe.service.SubscribeService;
import com.ecofresh.bottice.subscribe.service.impl.SubscribeServiceImpl;
import com.ecofresh.bottice.user.entities.User;
import com.ecofresh.bottice.user.service.UserService;
import com.ecofresh.common.utils.R;
import com.ecofresh.common.validator.ValidatorUtils;
import com.ecofresh.common.validator.group.UpdateGroup;
import com.ecofresh.security.SessionTokenProviderFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 预约信息相关处理
 * @author yukun
 * @email
 * @date 2017-03-26 17:27
 */
@RestController
@RequestMapping("subscribe")
@Api("预约信息接口")
public class ApiSubscribeController extends ApiAbstractController{

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscribeLogService subscribeLogService;

    /***
     * 获取用户预约记录信息(个人版)
     * @return
     */
    @GetMapping("subscribeInfo")
    @ApiOperation(value = "获取当前用户预约记录信息", notes = "获取用户预约记录说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="int",     name = "page",            value = "页码",  required = true),
            @ApiImplicitParam(paramType = "query",  dataType="int",     name = "row",             value = "条数",  required = true)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,Integer page ,Integer row)
    {
        if(null == page || null == row){
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getMsg());
        }
        //判断Session 是否存
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        Subscribe subscribe=new Subscribe();
        if(StrUtil.isNotBlank(sessionId)){
            subscribe.setUserId(sessionId);
            subscribe.setStoreState(null);
        }else{
            return R.error(ErrorDesc.USER_ID_EMPTY.getCode(), ErrorDesc.USER_ID_EMPTY.getMsg());
        }
        return R.ok(1).put("profile", subscribeService.find(page,row,subscribe));
    }

    /***
     * 根据门店的id修改当前预约中门店的信息
     * @return
     */
    @GetMapping("updateSubScribeInfoByStoreId/{storeId}")
    @ApiOperation(value = "根据门店的id修改当前预约中门店的信息", notes = "根据门店的id修改当前预约中门店的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="String",     name = "storeId",            value = "门店id",  required = true)
    })
    public R updateSubScribeInfoByStoreId(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("storeId")String storeId)
    {
        //判断Session 是否存
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(StrUtil.isNotBlank(storeId)){
            return R.ok(1).put("profile", subscribeService.updateSubScribeInfoByStoreId(storeId));
        }else{
            return R.error(ErrorDesc.STORE_ID_EMPTY.getCode(), ErrorDesc.STORE_ID_EMPTY.getMsg());
        }

    }

    /**
     * 根据当前预约记录的id查询预约的详细信息(用户版只返回当前预约的信息)
     * @param secret
     * @param token
     * @param id
     * @return
     */
    @GetMapping("subscribeInfoById/{id}")
    @ApiOperation(value = "获取当前预约记录信息(用户版)", notes = "获取预约记录说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="String",  name = "id",             value = "预约id", required = true)
    })
    public R getSubscribeInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("id") String id){
        //判断Session 是否存
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(StrUtil.isBlank(id)){
            return R.error(ErrorDesc.SUBSCRIBE_INFO_ERROR.getCode(), ErrorDesc.SUBSCRIBE_INFO_ERROR.getMsg());
        }else{
            return R.ok(1).put("profile",subscribeService.findOneById(id));
        }

    }

    /**
     * 根据当前预约记录的id查询预约的详细信息(管理版)
     * @param secret
     * @param token
     * @param id
     * @return
     */
    @GetMapping("subscribeInfoAdminById/{id}")
    @ApiOperation(value = "获取当前预约记录信息(管理版)", notes = "获取预约记录说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="String",  name = "id",             value = "预约id", required = true)
    })
    public R subscribeInfoAdminById(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("id") String id){
        //判断Session 是否存
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(StrUtil.isBlank(id)){
            return R.error(ErrorDesc.SUBSCRIBE_INFO_ERROR.getCode(), ErrorDesc.SUBSCRIBE_INFO_ERROR.getMsg());
        }else{
            return R.ok(1).put("profile",subscribeService.findOneAdminById(id));
        }

    }

    /***
     * 获取门店预约记录信息(管理员版)
     * @return
     */
    @GetMapping("subscribeInfoByAdmin")
    @ApiOperation(value = "获取当前门店预约记录信息", notes = "获取门店预约记录说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query", dataType="int",   name = "page", required = true),
            @ApiImplicitParam(paramType = "query", dataType="int",   name = "row", required = true),
            @ApiImplicitParam(paramType = "query", dataType="String",   name = "storeId", value = "门店id",required = true),
            @ApiImplicitParam(paramType = "query", dataType="String",   name = "subscribeDate", value = "预约日期",required = true),
            @ApiImplicitParam(paramType = "query", dataType="int",   name = "storeState", value = "预约状态",required = true),
            @ApiImplicitParam(paramType = "query", dataType="String",   name = "userName", value = "用户名",required = false)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,Integer page ,Integer row , String storeId,String subscribeDate,Integer storeState,String userName)
    {
        if(null == page || null == row){
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.PAGE_EMPTY.getMsg());
        }
        //判断Session 是否存
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);

        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        //根据用户的姓名查询出当前用户(只查询普通用户，不查询店员和管理员)
        List<User> users=null;
        Integer status=Constant.User.ACTIVE;
        Subscribe subscribe=new Subscribe();
        if(StrUtil.isNotBlank(userName)){
            /*User user=new User();
            User.Privacy privacy=user.new Privacy();
            privacy.setName(userName);
            user.setPrivacy(privacy);
            user.setUserType(String.valueOf(Constant.User.SUSPENDED));
            status=Constant.User.SUSPENDED;
            users = userService.findUserByUsername(user);*/
            subscribe.setUserName(userName);
        }/*else{
            status=Constant.User.ACTIVE;
        }*/



        //添加门店id作为查询条件
        if(StrUtil.isNotBlank(storeId)){
            Subscribe.Store store=subscribe.new Store();
            store.setStoreId(storeId);
            subscribe.setStore(store);
        }
        //添加预约日期作为查询条件
        if(StrUtil.isNotBlank(subscribeDate)){
            subscribe.setSubscribeDate(subscribeDate);
        }
        //添加预约状态作为查询条件
        if(null!=storeState){
            subscribe.setStoreState(storeState);
        }
        Page<Subscribe> entity = subscribeService.findByAdmin(page,row,subscribe,users,status);
        if(null != entity && null !=entity.getContent()){
        	for(Subscribe sub :entity.getContent()){
        		User user = userService.findCacheById(sub.getUserId());
        		sub.setUserName(user.getPrivacy().getName());
        		//如果当前预约记录中的用户真实姓名和用户表中的用户真实姓名不一致
        		if(!user.getPrivacy().getName().equals(sub.getUserName())){
                       subscribeService.updateCurrentObject(sub);
                }
        	}
        }
        return R.ok(1).put("profile",entity );


    }

    /***
     * 获取门店预约记录统计
     * @param id
     * @return
     */
    @GetMapping("mapReduceSubscribeInfo/{id}")
    @ApiOperation(value = "获取当前门店预约记录统计信息", notes = "获取门店预约统计记录说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query",  dataType="String",  name = "id", value = "门店id", required = true)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("id") String id)
    {
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(null==id||"".equals(id)){
            return R.error(ErrorDesc.STORE_ID_EMPTY.getCode(), ErrorDesc.STORE_ID_EMPTY.getMsg());
        }
        System.out.println(id);
        return R.ok(1).put("profile", subscribeService.mapReduceInfo(id,sessionId));
    }

    /***
     * 添加预约信息
     * @param
     * @return
     */
    @PostMapping("addSubscribeInfo")
    @ApiOperation(value = "添加预约信息", notes = "添加预约说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true)
    })
    public R profile(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@RequestBody SubscribeForm form)
    {
        Long startTime=System.currentTimeMillis();
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(null == form)
        {
            return R.error(com.ecofresh.bottice.enums.ErrorDesc.STORE_ID_EMPTY.getCode(), com.ecofresh.bottice.enums.ErrorDesc.STORE_ID_EMPTY.getMsg());
        }
        //预约的时间超过一个星期
        if(strToDate(form.getSubscribeDate()) .after(getPastDate(7))){
            return R.error(ErrorDesc.SUBSCRIBE_OVER.getCode(),ErrorDesc.SUBSCRIBE_OVER.getMsg());
        }
        //预约的时间在当前时间之前
        if(strToDate(form.getSubscribeDate()).before(new Date())){
            return R.error(ErrorDesc.SUBSCRIBE_BEFORETIME.getCode(),ErrorDesc.SUBSCRIBE_BEFORETIME.getMsg());
        }
        //有过预约外未完成
        Subscribe subscribe=subscribeService.findone(sessionId);
        System.out.println(subscribe);
        if(null!=subscribeService.findone(sessionId)){
            return R.error(ErrorDesc.SUBSCRIBE_OVER_HAND.getCode(),ErrorDesc.SUBSCRIBE_OVER_HAND.getMsg());
        }
        //参数验证
        ValidatorUtils.validateEntity(form, UpdateGroup.class);
        form.setUserId(sessionId);
        //记录操作预约的日志
        SubscribeLog subscribeLog=new SubscribeLog();
        Subscribe subscribe1=null;
        try {
            subscribe1=subscribeService.insert(form);
            if(subscribe1!=null){
                subscribeLog.setSubscribeId(subscribe1.getId());
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_SUCCESS);
            }else{
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
            }
        } catch (Exception e){
            subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
        } finally {
            Long endTime=System.currentTimeMillis();
            subscribeLog.setOpertionCode(Constant.Subscribe.SUBSCRIBELOG_ADD);
            subscribeLog.setOpertion("新增预约");
            subscribeLog.setOpertionStartTime(startTime);
            subscribeLog.setOpertionEndTime(endTime);
            subscribeLog.setPayTime(endTime-startTime);
            subscribeLog.setOpertionUserId(sessionId);
            subscribeLogService.insert(subscribeLog);
            return R.ok(1).put("subscribeInfo", subscribe1);
        }
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
     }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static Date getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
/*        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);*/
        return today;
    }

    /**
     * 取消预约(管理版)
     * @param secret
     * @param token
     * @param id
     * @return
     */
    @PutMapping("adminCancelSubscribeInfo/{id}")
    @ApiOperation(value = "管理版取消预约信息", notes = "管理版取消预约信息说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query", dataType="String",   name = "id", value = "预约记录的id", required = true)
    })
    public R adminCacelSubscribeInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("id") String id)
    {
        Long startTime=System.currentTimeMillis();
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(StrUtil.isBlank(id))
        {
            return R.error(ErrorDesc.SUBSCRIBE_ID_EMPTY.getCode(), ErrorDesc.SUBSCRIBE_ID_EMPTY.getMsg());
        }
        Subscribe ub=subscribeService.findOneById(id);
        if(null!=ub&&!ub.getStoreState().equals(Constant.Subscribe.SUBSCRIBE_ROUND)){
            return R.error(ErrorDesc.SUBSCRIBE_UPDATE_ERROR.getCode(), ErrorDesc.SUBSCRIBE_UPDATE_ERROR.getMsg());
        }
        //记录操作预约的日志
        SubscribeLog subscribeLog=new SubscribeLog();
        Subscribe subscribe=null;
        try {
            subscribe=subscribeService.update(id,Constant.Subscribe.SUBSCRIBE_CANCEL,Constant.Subscribe.SUBSCRIBE_CANCEL_ADMIN);
            if(subscribe!=null){
                subscribeLog.setSubscribeId(subscribe.getId());
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_SUCCESS);
            }else{
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
            }
        } catch (Exception e){
            subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
        } finally {
            Long endTime=System.currentTimeMillis();
            subscribeLog.setOpertionCode(Constant.Subscribe.SUBSCRIBELOG_CANCEL_ADMIN);
            subscribeLog.setOpertion("管理版取消预约");
            subscribeLog.setOpertionStartTime(startTime);
            subscribeLog.setOpertionEndTime(endTime);
            subscribeLog.setPayTime(endTime-startTime);
            subscribeLog.setOpertionUserId(sessionId);
            subscribeLogService.insert(subscribeLog);
            if(subscribe!=null){
                return R.ok(1).put("subscribeInfo",subscribe);
            }else {
                return R.error(com.ecofresh.bottice.enums.ErrorDesc.SUBSCRIBE_ERROR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.SUBSCRIBE_ERROR.getMsg());
            }
        }
    }

    /**
     * 取消预约(个人版)
     * @param secret
     * @param token
     * @param id
     * @return
     */
    @PutMapping("cacelSubscribeInfo/{id}")
    @ApiOperation(value = "个人版取消预约信息", notes = "个人版取消预约信息说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query", dataType="String",   name = "id", value = "预约记录的id", required = true)
    })
    public R updateStoreInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("id") String id)
    {
        Long startTime=System.currentTimeMillis();
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(StrUtil.isBlank(id))
        {
            return R.error(ErrorDesc.SUBSCRIBE_ID_EMPTY.getCode(), ErrorDesc.SUBSCRIBE_ID_EMPTY.getMsg());
        }
        Subscribe ub=subscribeService.findOneById(id);
        if(null!=ub&&!ub.getStoreState().equals(Constant.Subscribe.SUBSCRIBE_ROUND)){
            return R.error(ErrorDesc.SUBSCRIBE_UPDATE_ERROR.getCode(), ErrorDesc.SUBSCRIBE_UPDATE_ERROR.getMsg());
        }
        //记录操作预约的日志
        SubscribeLog subscribeLog=new SubscribeLog();
        Subscribe subscribe=null;
        try {
            subscribe=subscribeService.update(id,Constant.Subscribe.SUBSCRIBE_CANCEL,null);
            if(subscribe!=null){
                subscribeLog.setSubscribeId(subscribe.getId());
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_SUCCESS);
            }else{
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
            }
        } catch (Exception e){
            subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
        } finally {
            Long endTime=System.currentTimeMillis();
            subscribeLog.setOpertionCode(Constant.Subscribe.SUBSCRIBELOG_CANCEL);
            subscribeLog.setOpertion("用户版取消预约");
            subscribeLog.setOpertionStartTime(startTime);
            subscribeLog.setOpertionEndTime(endTime);
            subscribeLog.setPayTime(endTime-startTime);
            subscribeLog.setOpertionUserId(sessionId);
            subscribeLogService.insert(subscribeLog);
            if(subscribe!=null){
                return R.ok(1).put("subscribeInfo",subscribe);
            }else {
                return R.error(com.ecofresh.bottice.enums.ErrorDesc.SUBSCRIBE_ERROR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.SUBSCRIBE_ERROR.getMsg());
            }
        }
    }

    /**
     * 完成预约
     * @param secret
     * @param token
     * @param id
     * @return
     */
    @PutMapping("successSubscribeInfo/{id}")
    @ApiOperation(value = "完成预约", notes = "完成预约信息说明")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SECRET",  value = "密钥", required = true),
            @ApiImplicitParam(paramType = "header", dataType="String",   name = "X-EFRESH-SESSION", value = "会话", required = true),
            @ApiImplicitParam(paramType = "query", dataType="String",   name = "id", value = "预约记录的id", required = true)
    })
    public R successSubscribeInfo(@RequestHeader("X-EFRESH-SECRET") String secret, @RequestHeader("X-EFRESH-SESSION") String token,@PathVariable("id") String id)
    {
        Long startTime=System.currentTimeMillis();
        //判断Session 是否存在
        String sessionId = SessionTokenProviderFactory.getSessionTokenProvider().getUserId(secret, token);
        if(StrUtil.isBlank(sessionId))
        {
            return R.error(ErrorDesc.SESSION_EMPTY.getCode(), ErrorDesc.SESSION_EMPTY.getMsg());
        }
        if(StrUtil.isBlank(id))
        {
            return R.error(ErrorDesc.SUBSCRIBE_ID_EMPTY.getCode(), ErrorDesc.SUBSCRIBE_ID_EMPTY.getMsg());
        }
        Subscribe ub=subscribeService.findOneById(id);
        if(null!=ub&&!ub.getStoreState().equals(Constant.Subscribe.SUBSCRIBE_ROUND)){
            return R.error(ErrorDesc.SUBSCRIBE_UPDATE_ERROR.getCode(), ErrorDesc.SUBSCRIBE_UPDATE_ERROR.getMsg());
        }
        if(subscribeService.findOneById(id)!=null&&!subscribeService.findOneById(id).getSubscribeDate().equals(SubscribeServiceImpl.strToStr(new Date()))){
            return R.error(ErrorDesc.SUBSCRIBE_SUCCESS_FAILE.getCode(),ErrorDesc.SUBSCRIBE_SUCCESS_FAILE.getMsg());
        }
        //记录操作预约的日志
        SubscribeLog subscribeLog=new SubscribeLog();
        Subscribe subscribe=null;
        try {
            subscribe=subscribeService.update(id,Constant.Subscribe.SUBSCRIBE_REACH,null);
            if(subscribe!=null){
                subscribeLog.setSubscribeId(subscribe.getId());
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_SUCCESS);
            }else{
                subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
            }
        } catch (Exception e){
            subscribeLog.setOpertionStatus(Constant.Subscribe.OPERTIONSTATION_FAILED);
        } finally {
            Long endTime=System.currentTimeMillis();
            subscribeLog.setOpertionCode(Constant.Subscribe.SUBSCRIBELOG_REACH);
            subscribeLog.setOpertion("完成预约");
            subscribeLog.setOpertionStartTime(startTime);
            subscribeLog.setOpertionEndTime(endTime);
            subscribeLog.setPayTime(endTime-startTime);
            subscribeLog.setOpertionUserId(sessionId);
            subscribeLogService.insert(subscribeLog);
            if(subscribe!=null){
                return R.ok(1).put("subscribeInfo",subscribe);
            }else {
                return R.error(com.ecofresh.bottice.enums.ErrorDesc.SUBSCRIBE_ERROR.getCode(), com.ecofresh.bottice.enums.ErrorDesc.SUBSCRIBE_ERROR.getMsg());
            }
        }
    }
}
