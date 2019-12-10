package com.example.seating.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.dto.OrderPageDTO;
import com.example.seating.entity.TbOrder;
import com.example.seating.mapper.TbOrderMapper;
import com.example.seating.service.ITbOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 预约记录表 服务实现类
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@Service
public class TbOrderServiceImpl extends ServiceImpl<TbOrderMapper, TbOrder> implements ITbOrderService {
    @Override
    public IPage<OrderPageDTO> pageOf(Page page) {
        return baseMapper.pageOf(page);
    }
}
