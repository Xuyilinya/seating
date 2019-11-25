package com.example.seating.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 黑名单
 * </p>
 *
 * @author ROY
 * @since 2019-11-25
 */
@TableName("tb_black_list")
@Getter
@Setter
public class TbBlackList extends Model<TbBlackList> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 本次黑名单对应逾期订单编号
     */
    @TableField("order_id")
    private String orderIds;

    /**
     * 黑名单预期结束时间
     */
    @TableField("expect_end_time")
    private LocalDateTime expectEndTime;


    /**
     * 黑名单实际结束时间
     */
    @TableField("actually_end_time")
    private LocalDateTime actuallyEndTime;


    /**
     * 黑名单状态：0：无效；1：生效
     */
    private Integer status;

    /**
     * 结束状态：0：自动结束；1：系统管理员结束
     */
    private Integer endStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
