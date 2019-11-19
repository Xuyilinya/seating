package com.example.seating.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 教室表
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@TableName("tb_room")
@Getter
@Setter
public class TbRoom extends Model<TbRoom> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer roomId;

    /**
     * 教室名称
     */
    private String roomName;

    /**
     * 教室状态 0：不可用；1：可用
     */
    private Integer roomStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
