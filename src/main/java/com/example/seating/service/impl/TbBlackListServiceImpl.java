package com.example.seating.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.dto.BlackListPageDTO;
import com.example.seating.entity.TbBlackList;
import com.example.seating.mapper.TbBlackListMapper;
import com.example.seating.service.ITbBlackListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ROY
 * @since 2019-11-25
 */
@Service
public class TbBlackListServiceImpl extends ServiceImpl<TbBlackListMapper, TbBlackList> implements ITbBlackListService {
    @Override
    public IPage<BlackListPageDTO> pageOf(Page page) {
        return baseMapper.pageOf(page);
    }
}
