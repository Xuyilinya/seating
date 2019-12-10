package com.example.seating.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.dto.OrderPageDTO;
import com.example.seating.entity.TbOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 预约记录表 服务类
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
public interface ITbOrderService extends IService<TbOrder> {

    IPage<OrderPageDTO> pageOf(Page page);
}
