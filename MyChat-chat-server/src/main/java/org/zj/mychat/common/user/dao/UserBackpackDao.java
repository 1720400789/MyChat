package org.zj.mychat.common.user.dao;

import org.zj.mychat.common.common.domain.enums.YesOrNoEnum;
import org.zj.mychat.common.user.domain.entity.UserBackpack;
import org.zj.mychat.common.user.mapper.UserBackpackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-13
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    /**
     * 查询改名卡的次数
     * @param uid 用户 id
     * @param itemId
     * @return
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }

    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1")
                .one();
    }

    /**
     * 乐观更新改名卡
     */
    public boolean useItem(UserBackpack item) {
        return lambdaUpdate()
                .eq(UserBackpack::getId, item.getId())
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus, YesOrNoEnum.YES.getStatus())
                .update();
    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getStatus, YesOrNoEnum.YES.getStatus())
                .in(UserBackpack::getItemId, itemId)
                .list();
    }
}
