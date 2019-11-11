package com.example.seating.service.impl;

import com.example.seating.entity.TbUser;
import com.example.seating.mapper.TbUserMapper;
import com.example.seating.service.ITbUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ROY
 * @since 2019-11-11
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements ITbUserService {

}
