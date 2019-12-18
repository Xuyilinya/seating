package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.entity.TbUser;
import com.example.seating.service.ITbUserService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
     * 用户列表分页
     * @param current
     * @param size
     * @return
     */
    @GetMapping(value = "/page")
    public Object page(@RequestParam int current,@RequestParam int size){
        try {
            return ReturnUtils.Success(userService.page(new Page<>(current,size)));
        }catch (Exception e){
            log.error(e.getMessage());
            return ReturnUtils.Failure();
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping(value = "/remove/{id}")
    public Object remove(@PathVariable("id")String id){
        try {
            return ReturnUtils.Success(userService.removeById(id));
        }catch (Exception e){
            log.error(e.getMessage());
            return ReturnUtils.Failure();
        }
    }

    /**
     * 保存用户
     * @return
     */
    @PutMapping(value = "/save")
    public Object save(@RequestBody TbUser user){
        try {
            user.setCreateTime(LocalDateTime.now());
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
            // 检验图书证号码
            TbUser user = userService.getOne(Wrappers.<TbUser>query().lambda().eq(TbUser::getLibraryCard,login.getLibraryCard()));
            if (user == null) {
                return ReturnUtils.Failure("请输入正确的图书证号码");
            }

            // 返回用户编号
            return ReturnUtils.Success(user.getUserId());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
