package com.example.seating.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.dto.OrderPageDTO;
import com.example.seating.entity.TbOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 预约记录表 Mapper 接口
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
public interface TbOrderMapper extends BaseMapper<TbOrder> {

    IPage<OrderPageDTO> pageOf(Page page);
}
