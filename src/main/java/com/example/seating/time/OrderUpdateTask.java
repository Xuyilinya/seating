package com.example.seating.time;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.contstant.SysConstant;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbBlackListService;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.service.ITbUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@EnableScheduling
@Component
public class OrderUpdateTask {

    @Resource
    private ITbUserService userService;

    @Resource
    private ITbOrderService orderService;

    @Resource
    private ITbSeatService seatService;

    @Resource
    private ITbBlackListService blackListService;

    /**
     * 自动更新预约单，觉得更新频率太慢可适当调快
     * 5分钟执行一次
     */
    @Scheduled(fixedRate = 5*60*1000)
    public void orderUpdate(){
        log.info(">>>>>>>>>>>>>>>>>【开始更新预约单】<<<<<<<<<<<<<<<<<");
        List<TbOrder> tbOrderList =
                orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getStatus, SysConstant.ORDER_STATUS_NOT_SIGN_IN));

        // 超过开始时间十五分钟视为逾期
        tbOrderList.forEach(
                order -> {
                    if (LocalDateTime.now().getHour() >= Integer.valueOf(order.getStartTime()) && LocalDateTime.now().getMinute() >= 15) {
                        order.setStatus(SysConstant.ORDER_STATUS_OVERDUE);
                        orderService.updateById(order);

                        //释放座位
                        TbSeat seat = seatService.getById(order.getSeatId());
                        seat.setSeatStatus(SysConstant.SEAT_STATUS_USABLE);
                        seatService.updateById(seat);
                    }
                }
        );
        log.info(">>>>>>>>>>>>>>>>>【更新完成】<<<<<<<<<<<<<<<<<");
    }


    /**
     * 自动更新座位状态
     */
    @Scheduled(fixedRate = 60*1000)
    public void seatUpdate(){
        // 获取所有已签到的预约单，如果过了结束时间则释放座位
        List<TbOrder> tbOrders =
                orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getStatus,2));

        tbOrders.forEach(
                order -> {
                    if (LocalDateTime.now().getHour() <= Integer.valueOf(order.getEndTime())){
                        TbSeat seat = seatService.getById(order.getSeatId());
                        seat.setSeatStatus(SysConstant.SEAT_STATUS_USABLE);
                        seatService.updateById(seat);
                    }
                }
        );
    }
}
