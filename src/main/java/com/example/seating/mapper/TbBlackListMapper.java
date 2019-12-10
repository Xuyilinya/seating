package com.example.seating.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.dto.BlackListPageDTO;
import com.example.seating.entity.TbBlackList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ROY
 * @since 2019-11-25
 */
public interface TbBlackListMapper extends BaseMapper<TbBlackList> {

    /**
     * 黑名单分页
     * @param page
     * @return
     */
    IPage<BlackListPageDTO> pageOf(Page page);
}
