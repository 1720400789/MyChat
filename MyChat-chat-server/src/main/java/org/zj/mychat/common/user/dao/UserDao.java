package org.zj.mychat.common.user.dao;

import org.zj.mychat.common.common.domain.enums.YesOrNoEnum;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-07
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    /**
     * 获取用户信息
     * @param openId 用户标识
     * @return 用户信息
     */
    public User getByOpenId(String openId) {
        return lambdaQuery()
                .eq(User::getOpenId, openId)
                .one();
    }

    /**
     * 获取用户信息
     * @param name 用户名
     * @return 用户信息
     */
    public User getByName(String name) {
        return lambdaQuery()
                .eq(User::getName, name)
                .one();
    }

    /**
     * 更改用户名
     * @param uid 用户 id
     * @param name 需要更改的用户名
     */
    public void modifyName(Long uid, String name) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getName, name)
                .update();
    }

    /**
     * 佩戴徽章
     * @param uid 用户 id
     * @param itemId 物品 id
     */
    public void wearingBadge(Long uid, Long itemId) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getItemId, itemId)
                .update();
    }

    /**
     * 拉黑用户
     * @param id 待拉黑的用户 id
     */
    public void invalidUid(Long id) {
        lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, YesOrNoEnum.YES.getStatus())
                .update();
    }
}
