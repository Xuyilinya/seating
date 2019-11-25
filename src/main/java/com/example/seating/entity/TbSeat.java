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
 * 
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@Getter
@Setter
@TableName("tb_seat")
public class TbSeat extends Model<TbSeat> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer seatId;

    /**
     * 座位名称
     */
    private String seatName;

    /**
     * 座位状态 0:禁用；1：可用；2：已预约；3：暂离;4：有人
     */
    private Integer seatStatus;

    /**
     * 教室编号
     */
    private Integer roomId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
