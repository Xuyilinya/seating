package com.example.seating.service.impl;

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

}
