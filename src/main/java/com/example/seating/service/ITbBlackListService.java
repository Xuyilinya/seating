package com.example.seating.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seating.dto.BlackListPageDTO;
import com.example.seating.entity.TbBlackList;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ROY
 * @since 2019-11-25
 */
public interface ITbBlackListService extends IService<TbBlackList> {

    /**
     * 黑名单分页
     * @param page
     * @return
     */
    IPage<BlackListPageDTO> pageOf(Page page);
}
