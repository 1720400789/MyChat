package org.zj.mychat.common.user.service;

import org.zj.mychat.common.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zj.mychat.common.user.domain.enums.IdepotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-13
 */
public interface UserBackpackService {

    /**
     * 给用户发放一个物体时的幂等设计
     * @param uid 用户 id
     * @param itemId 物品 id
     * @param idepotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdepotentEnum idepotentEnum, String businessId);

}
