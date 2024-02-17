package org.zj.mychat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.zj.mychat.common.common.domain.enums.YesOrNoEnum;
import org.zj.mychat.common.user.domain.entity.ItemConfig;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.entity.UserBackpack;
import org.zj.mychat.common.user.domain.vo.resp.BadgeResp;
import org.zj.mychat.common.user.domain.vo.resp.UserInfoResp;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter {

    public static User buildUserSave(String openId) {
        return User.builder()
                .openId(openId)
                .build();
    }

    public static User buildAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        return User.builder()
                .id(uid)
                .name(userInfo.getNickname())
                .avatar(userInfo.getHeadImgUrl())
                .build();
    }

    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp vo = new UserInfoResp();
        BeanUtil.copyProperties(user, vo);
        vo.setId(user.getId());
        vo.setModifyNameChance(modifyNameCount);
        return vo;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(itemConfig -> {
                    BadgeResp resp = new BadgeResp();
                    BeanUtil.copyProperties(itemConfig, resp);
                    resp.setObtain(obtainItemSet.contains(itemConfig.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    resp.setWearing(Objects.equals(itemConfig.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return resp;
                }).sorted(
                        Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                                .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());
    }
}
