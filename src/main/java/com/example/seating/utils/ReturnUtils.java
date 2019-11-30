package com.example.seating.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ToString
@AllArgsConstructor
public class ReturnUtils implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String msg;

    @Setter
    @Getter
    private Object data;


    public static  ReturnUtils Success(Object data){
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("data",data);
        JSON json = new JSONObject(retMap);
        log.info("----------------output data is{}",json);
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_SUCCESS.getCode(),ReturnEnum.RETURN_STATUS_SUCCESS.getMsg(),data);
    }

    public static  ReturnUtils Success(Object data,String msg){
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("data",data);
        JSON json = new JSONObject(retMap);
        log.info("----------------output data is{}",json);
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_SUCCESS.getCode(),msg,data);
    }

    public static ReturnUtils Failure(){
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_FAIL.getCode(),ReturnEnum.RETURN_STATUS_FAIL.getMsg(),null);
    }

    public static ReturnUtils Failure(String msg){
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_FAIL.getCode(),msg,null);
    }

    public static ReturnUtils ParamsInvalid(){
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_PARAM_INVALID.getCode(),ReturnEnum.RETURN_STATUS_PARAM_INVALID.getMsg(),null);
    }

    public static ReturnUtils ParamsIsBlank(){
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_PARAM_IS_BLANK.getCode(),ReturnEnum.RETURN_STATUS_PARAM_IS_BLANK.getMsg(),null);
    }

    public static ReturnUtils UserNotLogin(){
        return new ReturnUtils(ReturnEnum.RETURN_STATUS_USER_IS_NOT_LOGIN.getCode(),ReturnEnum.RETURN_STATUS_USER_IS_NOT_LOGIN.getMsg(),null);
    }

}
