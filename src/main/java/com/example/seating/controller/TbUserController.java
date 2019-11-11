package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.entity.TbUser;
import com.example.seating.service.ITbUserService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户模块
 * @author ROY
 * @since 2019-11-11
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class TbUserController {

    @Resource
    private ITbUserService userService;

    /**
     * 保存用户
     * @return
     */
    @PutMapping(value = "/save")
    public Object save(@RequestBody TbUser user){
        try {
            return ReturnUtils.Success(userService.save(user));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 用户登录
     * @return
     */
    @PostMapping(value = "/login")
    public Object login(@RequestBody TbUser login){
        try {
            // 查询是否存在用户
            TbUser user = userService.getOne(Wrappers.<TbUser>query().lambda().eq(TbUser::getUsername,login.getUsername()));
            if (user == null) {
                return ReturnUtils.ParamsInvalid();
            }

            // 验证账号密码
            if (user.getPassword().equals(login.getPassword())) {
                return ReturnUtils.Success(user.getId());
            }
            return ReturnUtils.Failure();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
