package com.example.seating.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 预约记录表
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@Getter
@Setter
@TableName("tb_order")
public class TbOrder extends Model<TbOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 预约人编号
     */
    private Integer userId;

    /**
     * 座位编号
     */
    private Integer seatId;

    /**
     * 预约开始时间
     */
    private String startTime;

    /**
     * 预约结束时间
     */
    private String endTime;

    /**
     * 是否签到 0:否；1：是
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime creatTime;
}
