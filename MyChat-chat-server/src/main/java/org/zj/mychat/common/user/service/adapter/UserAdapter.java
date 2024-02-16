package org.zj.mychat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.vo.resp.UserInfoResp;

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
}
