package com.example.seating.handler.thead;

import com.example.seating.contstant.SysConstant;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeaveThread extends Thread {
    private int orderId;

    public LeaveThread(int orderId) {
        this.orderId = orderId;
    }

    /**
     * 暂离方法
     */
    @Override
    public void run() {
        try {
            Thread.sleep(30 * 60*1000);

            // 不取消暂离自动更新座位为可预约状态
            ITbSeatService seatService = SpringUtils.getBean(ITbSeatService.class);
            ITbOrderService orderService = SpringUtils.getBean(ITbOrderService.class);

            TbOrder order =  orderService.getById(orderId);
            TbSeat seat = seatService.getById(order.getSeatId());
            if (SysConstant.SEAT_STATUS_LEAVE == seat.getSeatStatus()) {
                seat.setSeatStatus(SysConstant.SEAT_STATUS_USABLE);
                seatService.updateById(seat);

                order.setStatus(SysConstant.ORDER_STATUS_CANCEL);
                orderService.updateById(order);
            }

        }catch (Exception e){

        }
    }
}
