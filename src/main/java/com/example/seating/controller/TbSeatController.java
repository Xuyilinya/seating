package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        if(StringUtils.isBlank(roomId)) return ReturnUtils.ParamsInvalid();

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
        List<TbOrder> list  = orderService.list(Wrappers.<TbOrder>query().lambda().eq(TbOrder::getSeatId,seatId).orderByAsc(TbOrder::getStartTime));
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

    private static List<Map<String,Object>> getTimeList(int minTime, int maxTime){
        List<Map<String,Object>> res = new ArrayList<>();
        int time = 8;
        for (int i = 0; i <= 10 ; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("time",time+":00");
            if (time>=minTime && time<=maxTime || LocalDateTime.now().getHour() >= time || LocalDateTime.now().getHour() >= 17) {
                map.put("flag",false);
            }else {
                map.put("flag",true);
            }
            time ++;
            res.add(map);
        }
        return res;
    }
}