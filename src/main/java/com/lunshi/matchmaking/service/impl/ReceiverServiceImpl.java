package com.lunshi.matchmaking.service.impl;


import com.common.util.GlosseryEnumUtils;
import com.lunshi.matchmaking.domain.ItemInfo;
import com.lunshi.matchmaking.service.ReceiverService;
import com.lunshi.matchmaking.service.dto.ItemTypeEnum;
import com.lunshi.matchmaking.worker.MatchMakingWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 撮合接收通知服务
 */
@Service
public class ReceiverServiceImpl implements ReceiverService {
    List<ItemInfo> sells = new ArrayList<>();

    List<ItemInfo> buys = new ArrayList<>();


    @Resource
    private MatchMakingWorker matchMakingWorker;

    /**
     * 新增交易
     *
     * @param item
     */
    public void addItem(ItemInfo item) {
        ItemTypeEnum typeEnum = GlosseryEnumUtils.getItem(ItemTypeEnum.class, item.getType());
        if (ItemTypeEnum.SELL == typeEnum) {
            sells.add(item);
        }
        if (ItemTypeEnum.BUY == typeEnum) {
            buys.add(item);
        }
        matchMakingWorker.execute();
    }

    /**
     * 执行撮合
     *
     * @param sellList
     */
    public synchronized void doExecute(List<ItemInfo> sellList, List<ItemInfo> buyList) {
        synchronized (sells) {
            sellList.addAll(sells);
            sells.clear();
            synchronized (buys) {
                buyList.addAll(buys);
                buys.clear();
            }
        }
    }
}
