package com.example.seating.time;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.entity.TbOrder;
import com.example.seating.service.ITbOrderService;
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
    private ITbOrderService orderService;

    /**
     * 自动更新预约单，觉得更新频率太慢可适当调快
     * 5分钟执行一次
     */
    @Scheduled(fixedRate = 5*60*1000)
    public void orderUpdate(){
        log.info(">>>>>>>>>>>>>>>>>【开始更新预约单】<<<<<<<<<<<<<<<<<");
        List<TbOrder> tbOrderList =
                orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getStatus,0));

        // 超过开始时间十五分钟视为逾期
        tbOrderList.forEach(
                order -> {
                    if (LocalDateTime.now().getHour() >= Integer.valueOf(order.getStartTime()) && LocalDateTime.now().getMinute() >= 15) {
                        order.setStatus(2);
                        orderService.updateById(order);
                    }
                }
        );

        log.info(">>>>>>>>>>>>>>>>>【更新完成】<<<<<<<<<<<<<<<<<");

    }
}
