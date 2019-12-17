package com.example.seating.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.contstant.SysConstant;
import com.example.seating.dto.RoomInsertDTO;
import com.example.seating.entity.TbRoom;
import com.example.seating.entity.TbSeat;
import com.example.seating.service.ITbRoomService;
import com.example.seating.service.ITbSeatService;
import com.example.seating.utils.ReturnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public Object save(@RequestBody RoomInsertDTO dto){
        try {
            TbRoom room = new TbRoom();
            room.setRoomName(dto.getRoomName());
            room.setCreateTime(LocalDateTime.now());
            roomService.save(room);

            return ReturnUtils.Success(batchSaveSeat(dto.getSeatTotal(),room.getRoomId()));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 批量生成座位
     * @param count
     * @param roomId
     * @return
     */
    private boolean batchSaveSeat(int count,int roomId){
        try {
            List<TbSeat> seats = new ArrayList<>();
            for (int i = 1; i < count+1 ; i++) {
                TbSeat seat = new TbSeat();
                seat.setRoomId(roomId);
                String name = i+"";
                if (name.length() < 2) {
                    seat.setSeatName("A0"+name);
                }
                seat.setSeatName("A"+name);
                seat.setCreateTime(LocalDateTime.now());
                seats.add(seat);
            }
            return seatService.saveBatch(seats);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
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
    @GetMapping(value = "/page")
    public Object page(@RequestParam int current,@RequestParam int size){
        try {
            return ReturnUtils.Success(roomService.page(new Page<>(current,size)));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ReturnUtils.Failure();
        }
    }

    /**
     * 教室详情
     * @param roomId
     * @return
     */
    @GetMapping(value = "/details/{roomId}")
    public Object details(@PathVariable("roomId")String roomId){
        Map<String,Object> res = new HashMap<>();
        try {
            TbRoom room = roomService.getById(roomId);
            if (room == null) {
                return ReturnUtils.ParamsInvalid();
            }
            res.put("roomName",room.getRoomName());
            res.put("roomStatus",room.getRoomStatus());
            res.put("createTime",room.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            ArrayList<Map<String,Object>> seatList = new ArrayList<>();
            seatService.list(Wrappers.<TbSeat>query().lambda().eq(TbSeat::getRoomId,roomId)).forEach(
                    seat -> {
                        Map<String,Object> map = new HashMap<>();
                        map.put("seatId",seat.getSeatId());
                        map.put("seatName",seat.getSeatName());
                        map.put("seatStatus", seat.getSeatStatus());
                        seatList.add(map);
                    }
            );
            res.put("seatList",seatList);
            return ReturnUtils.Success(res);
        }catch (Exception e){
            log.error(e.getMessage());
            return ReturnUtils.Failure();
        }
    }

    /**
     * 移除自习室和自习室的所有座位
     * @param roomId
     * @return
     */
    @DeleteMapping("/remove/{roomId}")
    public Object remove(@PathVariable("roomId")String roomId){
        return ReturnUtils.Success(roomService.removeById(roomId) && seatService.remove(Wrappers.<TbSeat>query().lambda().eq(TbSeat::getRoomId,roomId)));
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
           roomService.list(Wrappers.<TbRoom>query().lambda().eq(TbRoom::getRoomStatus, SysConstant.ROOM_STATUS_USABLE)).forEach(
                   room->{
                       List<TbSeat> seats = seatService.list(Wrappers.<TbSeat>query().lambda().eq(TbSeat::getRoomId,room.getRoomId()));
                       Map<String,Object> map = new HashMap<>();
                       map.put("roomId",room.getRoomId());
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
