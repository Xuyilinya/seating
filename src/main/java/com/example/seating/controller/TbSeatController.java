package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.contstant.SysConstant;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.ReturnUtils;
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
        for (int i = 0; i <= 10 ; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("time",time+":00");
            // 已预约的时间、过期时间、五点之后不可以预约
            if (time >= minTime && time <= maxTime || LocalDateTime.now().getHour() >= time || LocalDateTime.now().getHour() >= 17) {
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
     *
     * 批量生成座位
     * @return
     */
    @GetMapping(value = "/batchSave")
    public Object batchSave(int count,int roomId){
        try {
            List<TbSeat> seats = new ArrayList<>();
            for (int i = 1; i <count ; i++) {
                TbSeat seat = new TbSeat();
                seat.setRoomId(roomId);
                String name = i+"";
                if (name.length() < 2) {
                    seat.setSeatName("A0"+name);
                }
                seat.setSeatName("A"+name);
                seats.add(seat);
            }
            return ReturnUtils.Success(seatService.saveBatch(seats));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
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
            if (order == null) {
                return ReturnUtils.Failure("无可暂离座位");
            }

            // 结束前三十分钟不允许暂离
            if(Integer.valueOf(order.getEndTime()) > LocalDateTime.now().getHour() && LocalDateTime.now().getMinute() < 30){
                TbSeat seat = seatService.getById(order.getSeatId());
                seat.setSeatStatus(SysConstant.SEAT_STATUS_LEAVE);
                leaveOf(seat.getSeatId());
                return ReturnUtils.Success(seatService.updateById(seat));
            }

            return ReturnUtils.Failure("距离预约结束时间小于三十分钟不能暂离");
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

            TbOrder order = orderService.getOne(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getUserId,userId).eq(TbOrder::getStatus,1));

            if (order == null) {
                return ReturnUtils.Failure("无可退座位");
            }

            // 开始三十分钟后才能退座
            if (LocalDateTime.now().getHour() > Integer.valueOf(order.getStartTime()) ||
            LocalDateTime.now().getHour() == Integer.valueOf(order.getStartTime()) && LocalDateTime.now().getMinute() >=30
            ) {
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

            seat.setSeatStatus(SysConstant.SEAT_STATUS_APPOINTMENT);
            return ReturnUtils.Success(seatService.updateById(seat));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }


    /**
     * 暂离方法
     * @param seatId
     */
    @Async
    public void leaveOf(int seatId){
        try {
            new Thread(new SeatLeaveThread(seatId)).start();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 启用线程去自动更新暂离状态
     */
    public class SeatLeaveThread implements Runnable{
        private int seatId;

         SeatLeaveThread(int seatId) {
            this.seatId = seatId;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(30*60*1000);
                // 更新座位状态
                TbSeat seat = seatService.getById(seatId);
                seat.setSeatStatus(SysConstant.SEAT_STATUS_APPOINTMENT);
                seatService.updateById(seat);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
    }
}
