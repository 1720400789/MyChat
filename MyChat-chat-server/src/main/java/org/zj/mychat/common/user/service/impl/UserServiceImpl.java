package org.zj.mychat.common.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zj.mychat.common.common.annotation.RedissonLock;
import org.zj.mychat.common.common.event.UserBlackEvent;
import org.zj.mychat.common.common.event.UserRegisterEvent;
import org.zj.mychat.common.common.utils.AssertUtil;
import org.zj.mychat.common.user.dao.BlackDao;
import org.zj.mychat.common.user.dao.ItemConfigDao;
import org.zj.mychat.common.user.dao.UserBackpackDao;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.*;
import org.zj.mychat.common.user.domain.enums.BlackTypeEnum;
import org.zj.mychat.common.user.domain.enums.ItemEnum;
import org.zj.mychat.common.user.domain.enums.ItemTypeEnum;
import org.zj.mychat.common.user.domain.vo.req.BlackReq;
import org.zj.mychat.common.user.domain.vo.resp.BadgeResp;
import org.zj.mychat.common.user.domain.vo.resp.UserInfoResp;
import org.zj.mychat.common.user.service.UserService;
import org.zj.mychat.common.user.service.adapter.UserAdapter;
import org.zj.mychat.common.user.service.cache.ItemCache;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Autowired
    private BlackDao blackDao;

    /**
     * Spring 提供的事件发送者
     */
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 用户注册接口
     * @param insert 待注册的用户
     * @return 标识
     */
    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        // 用户注册的事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
        return insert.getId();
    }

    /**
     * 获取用户信息
     * @param uid 用户 id
     * @return 用户信息实体类
     */
    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    /**
     * 用户更改名字
     * @param uid 用户 id
     * @param name 需要更改的名字
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个");
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不够了哦，请等后续活动吧");
        // 使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) {
            // 改名
            userDao.modifyName(uid, name);
        }
    }

    /**
     * 用户徽章预览
     * @param uid 用户 id
     * @return 徽章集合
     */
    @Override
    @Cacheable // 本地缓存
    public List<BadgeResp> badges(Long uid) {
        // 查询所有的徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 查询用户当前佩戴的徽章
        User user = userDao.getById(uid);

        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadges(Long uid, Long itemId) {
        // 确保佩戴的徽章存在
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem, "还没有获得这个徽章");
        // 确保这个物品是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "只有徽章才能佩戴");
        userDao.wearingBadge(uid, itemId);
    }

    /**
     * 根据 uid 拉黑用户
     * @param req 请求体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black user = Black.builder()
                .type(BlackTypeEnum.UID.getType())
                .target(uid.toString())
                .build();
        blackDao.save(user);

        User byId = userDao.getById(uid);
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));

        // 推送
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    private void blackIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return ;
        }
        Black insert = Black.builder()
                .type(BlackTypeEnum.IP.getType())
                .target(ip)
                .build();
        blackDao.save(insert);
    }
}
