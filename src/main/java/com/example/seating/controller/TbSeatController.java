package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.contstant.SysConstant;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.ReturnUtils;
import com.example.seating.handler.LeaveHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 座位模块
 * @author ROY
 * @since 2019-11-11
 */
@RestController
@RequestMapping("/seat")
@Slf4j
public class TbSeatController {

    @Resource
    private ITbSeatService seatService;

    @Resource
    private ITbOrderService orderService;

    /**
     * 更新座位
     * @param seat
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public Object update(@RequestBody TbSeat seat){
        try {
            TbSeat seat1 = seatService.getById(seat.getSeatId());
            if (seat1 == null) {
                return ReturnUtils.ParamsInvalid();
            }
            seat1.setSeatStatus(seat.getSeatStatus());
            return ReturnUtils.Success(seatService.updateById(seat));
        }catch (Exception e){
            log.error(e.getMessage());
            return ReturnUtils.Failure();
        }

    }


    /**
     * 保存座位
     * @param seat
     * @return
     */
    @RequestMapping(value = "/save",method = RequestMethod.PUT)
    public Object save(@RequestBody TbSeat seat){
        seat.setCreateTime(LocalDateTime.now());
        return ReturnUtils.Success(seatService.save(seat));
    }

    /**
     * 删除座位
     * @param seatId
     * @return
     */
    @RequestMapping(value = "/remove/{seatId}",method = RequestMethod.DELETE)
    public Object remove(@PathVariable("seatId")String seatId){
        return ReturnUtils.Success(seatService.removeById(seatId));
    }


    /**
     * 查询教室座位
     * @param roomId
     * @return
     */
    @GetMapping(value = "/list")
    public Object list(@RequestParam String roomId){
        if(StringUtils.isBlank(roomId)) return ReturnUtils.ParamsIsBlank();

        try {
            // 查询教室座位
            List<TbSeat> seatList = seatService.list(Wrappers.<TbSeat>query().lambda().eq(TbSeat::getRoomId,roomId));
            return ReturnUtils.Success(seatList);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 获取当前座位可用时间
     * @param seatId
     * @return
     */
    @GetMapping(value = "/getTimeList")
    public Object getTimeList(@RequestParam String seatId){
        int minTime = 0;
        int maxTime = 0;
        // 获取当前座位预约记录
        List<TbOrder> list  = orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getSeatId,seatId).ne(TbOrder::getStatus, SysConstant.ORDER_STATUS_OVERDUE).orderByAsc(TbOrder::getStartTime));
        if (list.size()>1){
            minTime = Integer.valueOf(list.get(0).getStartTime());
            maxTime = Integer.valueOf(list.get(list.size()-1).getEndTime());
            getTimeList(minTime,maxTime);
        }else if (list.size() == 1){
            minTime = Integer.valueOf(list.get(0).getStartTime());
            maxTime = Integer.valueOf(list.get(0).getEndTime());
        }
        return ReturnUtils.Success(getTimeList(minTime,maxTime));
    }

    /**
     * 计算可用时间节点
     * @param minTime
     * @param maxTime
     * @return
     */
    private static List<Map<String,Object>> getTimeList(int minTime, int maxTime){
        List<Map<String,Object>> res = new ArrayList<>();
        int time = 8;
        for (int i = 0; i <= 16 ; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("time",time+":00");
            // 已预约的时间、过期时间、五点之后不可以预约
            if (time >= minTime && time <= maxTime || LocalDateTime.now().getHour() >= time || LocalDateTime.now().getHour() >= 23) {
                map.put("flag",false);
            }else {
                map.put("flag",true);
            }
            time ++;
            res.add(map);
        }
        return res;
    }


    /**
     * 暂离
     * @param userId
     * @return
     */
    @GetMapping(value = "/leave")
    public Object leave(@RequestParam String userId){
        try {
            TbOrder order = orderService.getOne(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getUserId,userId).eq(TbOrder::getStatus,SysConstant.ORDER_STATUS_SIGN_IN));

            if(order!=null){
                TbSeat seat = seatService.getById(order.getSeatId());
                if (SysConstant.SEAT_STATUS_APPOINTMENT == seat.getSeatStatus()) {
                    // 结束前三十分钟不允许暂离
                    if (Integer.valueOf(order.getEndTime()) - LocalDateTime.now().getHour() == 1 && LocalDateTime.now().getMinute() > 30) {
                        return ReturnUtils.Failure("距离预约结束时间小于三十分钟不能暂离");
                    }else {
                        seat.setSeatStatus(SysConstant.SEAT_STATUS_LEAVE);
                        leaveOf(order.getOrderId());
                        return ReturnUtils.Success(seatService.updateById(seat),"暂离成功");
                    }
                }
            }
            return ReturnUtils.Failure("无可暂离座位");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 退座
     * @param userId
     * @return
     */
    @GetMapping(value = "/quit")
    public Object quit(@RequestParam String userId){
        try {

            TbOrder order = orderService.getOne(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getUserId,userId).eq(TbOrder::getStatus,SysConstant.ORDER_STATUS_SIGN_IN));

            if (order == null) {
                return ReturnUtils.Failure("无可退座位");
            }

            // 开始三十分钟后才能退座
            if (LocalDateTime.now().getHour() >= Integer.valueOf(order.getStartTime()) && LocalDateTime.now().getMinute() >=30) {
                order.setStatus(SysConstant.ORDER_STATUS_CANCEL);
                orderService.updateById(order);

                TbSeat seat = seatService.getById(order.getSeatId());
                seat.setSeatStatus(SysConstant.SEAT_STATUS_USABLE);
                return ReturnUtils.Success(seatService.updateById(seat));
            }

            return ReturnUtils.Failure("请在开始三十分钟后退座");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 取消暂离
     * @param userId
     * @return
     */
    @GetMapping(value = "/leaveCancel")
    public Object leaveCancel(@RequestParam String userId){
        try {
            TbOrder order =
                    orderService.getOne(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getUserId,userId).eq(TbOrder::getStatus,SysConstant.ORDER_STATUS_SIGN_IN));
            if (order == null) {
                return ReturnUtils.Failure("当前无需取消暂离的座位");
            }
            TbSeat seat = seatService.getById(order.getSeatId());
            if (seat.getSeatStatus() == SysConstant.SEAT_STATUS_LEAVE) {
                seat.setSeatStatus(SysConstant.SEAT_STATUS_APPOINTMENT);
                LeaveHandler.removeLeaveThread(order.getOrderId());
                return ReturnUtils.Success(seatService.updateById(seat));
            }
            return ReturnUtils.Failure("当前无需取消暂离的座位");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }


    /**
     * 暂离方法
     * @param orderId
     */
    @Async
    public void leaveOf(int orderId){
        try {
            LeaveHandler.creatLeaveThread(orderId);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}