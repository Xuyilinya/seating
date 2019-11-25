package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.CheckUser;
import com.example.seating.entity.TbBlackList;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.entity.TbUser;
import com.example.seating.service.ITbBlackListService;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.service.ITbUserService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private ITbUserService userService;

    @Resource
    private ITbBlackListService blackListService;



    /**
     * 保存预约记录
     * @param order
     * @return
     */
    @PutMapping(value = "/save")
    public Object save(@RequestBody TbOrder order){
        try {
            TbUser user = userService.getById(order.getUserId());
            if (user.getStatus() == 0) {
                TbBlackList blackList =
                blackListService.getOne(Wrappers.<TbBlackList>query().lambda().eq(TbBlackList::getUserId,order.getUserId()));
                return ReturnUtils.Failure("由于你最近逾期次数超过三次现已被拉入黑名单，于"+blackList.getExpectEndTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))
                        +"后自动取消黑名单如有异议请联系管理员解除");
            }

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
            List<Map<String,Object>> res = new ArrayList<>();
            List<TbOrder> tbOrders =
                    orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getUserId,userId).orderByDesc(TbOrder::getCreatTime));

            tbOrders.forEach(
                    order -> {
                        Map<String,Object> map = new HashMap<>();
                        String seatName = seatService.getById(order.getSeatId()).getSeatName();
                        map.put("orderId",order.getOrderId());
                        map.put("seatName",seatName);
                        map.put("startTime",order.getStartTime()+":00");
                        map.put("endTime",order.getEndTime()+":00");
                        map.put("status",order.getStatus());
                        map.put("creatTime",order.getCreatTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd")));
                        res.add(map);
                    }
            );
            return ReturnUtils.Success(res);
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
            // 获取当前最为接近的预定记录自动签到
            List<TbOrder> orders = orderService.list(Wrappers.<TbOrder>query().lambda()
                    .eq(TbOrder::getUserId,userId)
                    .eq(TbOrder::getSeatId,seatId)
                    .eq(TbOrder::getStatus,0));
            if (orders.size() < 1) {
                return ReturnUtils.Failure("当前没有需签到的座位");
            }

            if (orders.size() > 1) {
                orders.sort(new Comparator<TbOrder>() {
                                @Override
                                public int compare(TbOrder o1, TbOrder o2) {
                                    int a = LocalDateTime.now().getHour() - Integer.valueOf(o1.getStartTime());
                                    int b = LocalDateTime.now().getHour() - Integer.valueOf(o2.getStartTime());
                                    return Math.abs(Math.abs(a-b));
                                }
                            }
                );
            }
            TbOrder order = orders.get(0);
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
                TbSeat seat = seatService.getById(order.getSeatId());
                seat.setSeatStatus(1);
                seatService.updateById(seat);

                 return ReturnUtils.Success(orderService.removeById(order));
            }


            return ReturnUtils.Failure("请在开始前十五分钟取消");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
