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
 * 用户表
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@TableName("tb_user")
@Getter
@Setter
public class TbUser extends Model<TbUser> {

    /**
     * 自增主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 读书证
     */
    private String libraryCard;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
