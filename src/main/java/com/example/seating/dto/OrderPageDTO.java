package com.example.seating.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPageDTO {
    private String id;
    private String seatName;
    private String startTime;
    private String endTime;
    private String userName;
    private Integer status;
    private String createTime;
}
