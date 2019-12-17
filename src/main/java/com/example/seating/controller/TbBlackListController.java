package com.example.seating.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.contstant.SysConstant;
import com.example.seating.dto.BlackListDetailsDTO;
import com.example.seating.entity.TbBlackList;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbUser;
import com.example.seating.service.ITbBlackListService;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.service.ITbUserService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Resource
    private ITbOrderService orderService;

    @Resource
    private ITbSeatService seatService;

    /**
     * 获取预期记录
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public Object list(){
        return ReturnUtils.Success(blackListService.list());
    }

    /**
     * 分页获取黑名单
     * @param current
     * @param size
     * @return
     */
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public Object page(@RequestParam int current,@RequestParam int size){
        return ReturnUtils.Success(blackListService.pageOf(new Page<>(current,size)));
    }


    /**
     * 黑名单详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/details/{id}",method = RequestMethod.GET)
    public Object details(@PathVariable("id")String id){
        Map<String,Object> res = new HashMap<>();
        TbBlackList bl = blackListService.getById(id);

        List<BlackListDetailsDTO.OrderDTO> orders = new ArrayList<>();
        String[] orderIds = bl.getOrderIds().split(",");
        for (String oi: orderIds) {
            TbOrder order = orderService.getById(oi);
            BlackListDetailsDTO.OrderDTO dto = new BlackListDetailsDTO.OrderDTO();
            BeanUtils.copyProperties(order,dto);
            dto.setSeatName(seatService.getById(order.getSeatId()).getSeatName());
            orders.add(dto);
        }
        TbUser user = userService.getById(bl.getUserId());
        res.put("createTime",bl.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        res.put("expectEndTime",bl.getExpectEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        res.put("userName",user.getName());
        res.put("orders",orders);
        return ReturnUtils.Success(res);
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
            blackList.setStatus(SysConstant.BLACK_LIST_STATUS_INVALID);
            blackList.setEndStatus(SysConstant.BLACK_LIST_END_STATUS_MANUAL);
            blackList.setActuallyEndTime(LocalDateTime.now());
            blackListService.updateById(blackList);

            TbUser user = userService.getById(blackList.getUserId());
            user.setStatus(SysConstant.USER_STATUS_ENABLE);
            return ReturnUtils.Success(userService.updateById(user));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
