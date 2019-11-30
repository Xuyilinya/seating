package com.example.seating.service.impl;

import com.example.seating.entity.TbSeat;
import com.example.seating.mapper.TbSeatMapper;
import com.example.seating.service.ITbSeatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@Service("seatService")
public class TbSeatServiceImpl extends ServiceImpl<TbSeatMapper, TbSeat> implements ITbSeatService {

}
