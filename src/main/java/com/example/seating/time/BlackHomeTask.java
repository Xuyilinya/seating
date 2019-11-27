package com.example.seating.time;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.seating.contstant.SysConstant;
import com.example.seating.entity.TbBlackList;
import com.example.seating.entity.TbOrder;
import com.example.seating.entity.TbUser;
import com.example.seating.service.ITbBlackListService;
import com.example.seating.service.ITbOrderService;
import com.example.seating.service.ITbUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@Component
@Slf4j
public class BlackHomeTask {

    @Resource
    private ITbBlackListService tbBlackListService;

    @Resource
    private ITbUserService userService;

    @Resource
    private ITbOrderService orderService;

    /**
     * 释放小黑屋
     * tip:会有时间差，自己调试，最高误差10分钟
     */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void release() {
        try {
            log.info(">>>>>>>>>>>>>>>>>>查询可释放用户<<<<<<<<<<<<<<<<<<<<<<<");
            tbBlackListService.list(Wrappers.<TbBlackList>query().lambda().eq(TbBlackList::getStatus, SysConstant.BLACK_LIST_STATUS_VALID)).forEach(
                    b -> {
                        if (LocalDateTime.now().isAfter(b.getExpectEndTime())) {
                            b.setStatus(SysConstant.BLACK_LIST_STATUS_INVALID);
                            b.setEndStatus(SysConstant.BLACK_LIST_END_STATUS_AUTO);
                            b.setActuallyEndTime(LocalDateTime.now());
                            tbBlackListService.updateById(b);

                            TbUser user = userService.getById(b.getUserId());
                            user.setStatus(SysConstant.USER_STATUS_ENABLE);
                            userService.updateById(user);
                        }
                    }
            );
            log.info(">>>>>>>>>>>>>>>>>>查询完成<<<<<<<<<<<<<<<<<<<<<<<");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 巡视逾期大于三次的直接拉入小黑屋
     */
    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void capture() {
        try {
            log.info(">>>>>>>>>>>>>>>>>>滴！滴！滴！滴！滴！开始巡查<<<<<<<<<<<<<<<<<<<<<<<<<");
            userService.list().forEach(
                    user -> {
                        List<TbOrder> overdueCount;
                        // 用户之前是否被拉入黑名单
                        List<TbBlackList> blackHistory =
                                tbBlackListService.list(Wrappers.<TbBlackList>query().lambda().eq(TbBlackList::getUserId, user.getUserId()).orderByDesc(TbBlackList::getCreateTime));

                        if (blackHistory.size() > 0) {
                            LocalDateTime time;
                            TbBlackList history = blackHistory.get(0);

                            // 判断当次黑名单是否是系统管理员结束
                            if (history.getEndStatus() == 1) {
                                time = history.getActuallyEndTime();
                            } else {
                                time = history.getExpectEndTime();
                            }
                            overdueCount = orderService.list(Wrappers.<TbOrder>query().lambda()
                                    .eq(TbOrder::getUserId, user.getUserId())
                                    .eq(TbOrder::getStatus, SysConstant.ORDER_STATUS_OVERDUE)
                                    .between(TbOrder::getCreatTime, time, LocalDateTime.now()));

                        } else {
                            overdueCount = orderService.list(Wrappers.<TbOrder>query().lambda()
                                    .eq(TbOrder::getUserId, user.getUserId())
                                    .eq(TbOrder::getStatus, SysConstant.ORDER_STATUS_OVERDUE));

                        }

                        if (overdueCount.size() >= 3) {
                            List<String> orderIds = overdueCount.stream().map(order -> String.valueOf(order.getOrderId())).collect(Collectors.toList());

                            //更新用户状态
                            user.setStatus(SysConstant.USER_STATUS_BLACK_LIST);
                            userService.updateById(user);

                            // 创建黑名单记录
                            TbBlackList blackList = new TbBlackList();
                            blackList.setUserId(user.getUserId());
                            blackList.setExpectEndTime(LocalDateTime.now().minus(-29, ChronoUnit.DAYS));
                            blackList.setOrderIds(String.join(",", orderIds));
                            blackList.setCreateTime(LocalDateTime.now());
                            tbBlackListService.save(blackList);

                            log.info(">>>>>>>>>>>>发现用户："+user.getName()+"逾期记录达到峰值，拉入小黑屋暴打三十天！<<<<<<<<<<<<<<<<<");
                        }
                    }
            );
            log.info(">>>>>>>>>>>>>>>>>>滴！滴！滴！滴！滴！巡查结束<<<<<<<<<<<<<<<<<<<<<<<<<");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
