package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.entity.TbRoom;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbRoomService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教室模块
 * @author ROY
 * @since 2019-11-11
 */
@Slf4j
@RestController
@RequestMapping("/room")
public class TbRoomController {

    @Resource
    private ITbRoomService roomService;

    @Resource
    private ITbSeatService seatService;

    /**
     * 后台新增教室
     * @return
     */
    @PutMapping(value = "/save")
    public Object save(@RequestBody TbRoom tbRoom){
        try {
            tbRoom.setCreateTime(LocalDateTime.now());
            return ReturnUtils.Success(roomService.save(tbRoom));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 后台更新教室信息或状态
     * @param room
     * @return
     */
    @PostMapping(value = "/update")
    public Object update(@RequestBody TbRoom room){
        try {
            return ReturnUtils.Success(roomService.updateById(room));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 后台获取教室列表
     * @return
     */
    @GetMapping(value = "/list")
    public Object list(){
        try {
            return ReturnUtils.Success(roomService.list());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 用户获取教室列表
     * @return
     */
    @GetMapping(value = "/user/list")
    public Object userList(){
        List<Map<String,Object>> res = new ArrayList<>();
        try {
            // 查询可用教室
           roomService.list().forEach(
                   room->{
                       List<TbSeat> seats = seatService.list(Wrappers.<TbSeat>query().lambda().eq(TbSeat::getRoomId,room.getId()));
                       Map<String,Object> map = new HashMap<>();
                       map.put("roomId",room.getId());
                       map.put("roomName",room.getRoomName());
                       map.put("totalSeat",seats.size());
                       map.put("roomStatus",room.getRoomStatus());
                       res.add(map);
                   }
           );
            return ReturnUtils.Success(res);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }
}
