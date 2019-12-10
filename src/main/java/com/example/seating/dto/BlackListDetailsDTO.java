package com.example.seating.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlackListDetailsDTO {
    private String userName;
    private String createTime;
    private String expectEndTime;
    private List<OrderDTO> orders;


    @Getter
    @Setter
    public static class OrderDTO{
        private String seatName;
        private String startTime;
        private String endTime;
        private Integer status;
    }
}
