package org.zj.mychat.common.user.service.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.zj.mychat.common.common.annotation.RedissonLock;
import org.zj.mychat.common.common.domain.enums.YesOrNoEnum;
import org.zj.mychat.common.common.service.LockService;
import org.zj.mychat.common.common.utils.AssertUtil;
import org.zj.mychat.common.user.dao.UserBackpackDao;
import org.zj.mychat.common.user.domain.entity.UserBackpack;
import org.zj.mychat.common.user.domain.enums.IdepotentEnum;
import org.zj.mychat.common.user.service.UserBackpackService;

import java.util.Objects;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-13
 */
@Service
public class UserBackpackServiceImpl implements UserBackpackService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private LockService lockService;

    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    /**
     * 给用户发放一个物体时的幂等设计
     * @param uid 用户 id
     * @param itemId 物品 id
     * @param idepotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    public void acquireItem(Long uid, Long itemId, IdepotentEnum idepotentEnum, String businessId) {
        // 拼接幂等号
        String idempotent = getIdempotent(itemId, idepotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
//        RLock lock = redissonClient.getLock("acquireItem" + idempotent);
//        boolean b = lock.tryLock();
//        AssertUtil.isTrue(b, "请求太频繁了");
//        try {
//            // 通过幂等号查询用户背包中是否已经存在物品了
//            UserBackpack userBackpack = userBackpackDao.getByIdemopotent(idempotent);
//            // 如果物品已经添加了，则直接返回，提示用户物品添加成功
//            if (Objects.nonNull(userBackpack)) {
//                return ;
//            }
//            // 不存在则发放物品
//            UserBackpack insert = UserBackpack.builder()
//                    .uid(uid)
//                    .itemId(itemId)
//                    .status(YesOrNoEnum.NO.getStatus())
//                    .idempotent(idempotent)
//                    .build();
//            userBackpackDao.save(insert);
//        } finally {
//            lock.unlock();
//        }
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        // 通过幂等号查询用户背包中是否已经存在物品了
        UserBackpack userBackpack = userBackpackDao.getByIdemopotent(idempotent);
        // 如果物品已经添加了，则直接返回，提示用户物品添加成功
        if (Objects.nonNull(userBackpack)) {
            return ;
        }
        // 不存在则发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdepotentEnum idepotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idepotentEnum.getType(), businessId);
    }

}
