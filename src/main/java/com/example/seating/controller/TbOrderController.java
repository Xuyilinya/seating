package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.entity.TbOrder;
import com.example.seating.service.ITbOrderService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 预约记录表 前端控制器
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class TbOrderController {


    @Resource
    private ITbOrderService orderService;

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
                if (Integer.valueOf(ho.getEndTime()) >= Integer.valueOf(order.getStartTime())) {
                    return ReturnUtils.Failure("时间段预约冲突");
                }
            }


            // 更新教室
            order.setCreatTime(LocalDateTime.now());
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
}
