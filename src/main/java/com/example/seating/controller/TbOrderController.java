package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约记录模块
 * @author ROY
 * @since 2019-11-11
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class TbOrderController {

    @Resource
    private ITbOrderService orderService;

    @Resource
    private ITbSeatService seatService;

    /**
     * 保存预约记录
     * @param order
     * @return
     */
    @PutMapping(value = "/save")
    public Object save(@RequestBody TbOrder order){
        try {
            // 查询预约
            List<TbOrder> orders = orderService.list(Wrappers.<TbOrder>query().lambda()
                    .eq(TbOrder::getUserId,order.getUserId())
                    .last("and DATE_FORMAT(tb_order.`creat_time`,'%Y%m%d') = DATE_FORMAT(NOW(),'%Y%m%d')"));

            // 时间段冲突
            for (TbOrder ho:orders) {
                if (Integer.valueOf(ho.getEndTime()) > Integer.valueOf(order.getStartTime())) {
                    return ReturnUtils.Failure("时间段预约冲突");
                }
            }
            order.setCreatTime(LocalDateTime.now());

            // 更新座位状态
            TbSeat seat = seatService.getById(order.getSeatId());
            seat.setSeatStatus(2);
            seatService.updateById(seat);

            return ReturnUtils.Success(orderService.save(order));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 获取当前用户预约记录
     * @param userId
     * @return
     */
    @GetMapping(value = "/list")
    public Object list(@NotNull @RequestParam String userId){
        try {
            return ReturnUtils.Success(orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getUserId,userId)));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 签到
     * @param seatId
     * @param userId
     * @return
     */
    @GetMapping(value = "/signIn")
    public Object signIn(@RequestParam String seatId,@RequestParam String userId){
        try {
            // 获取当前最为最近的预定记录自动签到
            TbOrder order = orderService.getOne(Wrappers.<TbOrder>query().lambda()
                    .eq(TbOrder::getUserId,userId)
                    .eq(TbOrder::getSeatId,seatId)
                    .eq(TbOrder::getStatus,0)
                    .last("ORDER BY ABS(DATE_FORMAT(NOW(),'%h')- tb_order.`start_time` LIMIT 1)"));

            if (order == null) {
                return ReturnUtils.Failure("当前没有需签到的座位");
            }
            order.setStatus(1);
            return ReturnUtils.Success(orderService.updateById(order));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 取消预约
     * @param orderId
     * @return
     */
    @PostMapping(value = "/cancel")
    public Object cancel(@RequestParam String orderId){
        try {
            TbOrder order = orderService.getById(orderId);

            // 在开始时间十五分钟前可取消
            if (LocalDateTime.now().getHour() < Integer.valueOf(order.getStartTime()) && LocalDateTime.now().getMinute() < 45) {
                 return ReturnUtils.Success(orderService.removeById(order));
            }

            return ReturnUtils.Failure("请在开始前十五分钟取消");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
