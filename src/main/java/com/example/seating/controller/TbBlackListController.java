package com.example.seating.controller;


import com.example.seating.entity.TbBlackList;
import com.example.seating.entity.TbUser;
import com.example.seating.service.ITbBlackListService;
import com.example.seating.service.ITbUserService;
import com.example.seating.utils.ReturnUtils;
import com.sun.deploy.util.BlackList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ROY
 * @since 2019-11-25
 */
@Slf4j
@RestController
@RequestMapping("/blackHome")
public class TbBlackListController {

    @Resource
    private ITbBlackListService blackListService;

    @Resource
    private ITbUserService userService;

    /**
     * 获取预期记录
     * @return
     */
    @RequestMapping(value = "/list")
    public Object list(){
        return ReturnUtils.Success(blackListService.list());
    }


    /**
     * 释放指定黑名单用户
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/release",method = RequestMethod.GET)
    public Object release(@RequestParam String id){
        try {
            TbBlackList blackList = blackListService.getById(id);
            blackList.setStatus(0);
            blackList.setEndStatus(1);
            blackList.setActuallyEndTime(LocalDateTime.now());
            blackListService.updateById(blackList);

            TbUser user = userService.getById(blackList.getUserId());
            user.setStatus(0);
            return userService.updateById(user);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
