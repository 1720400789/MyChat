package org.zj.mychat.common.user.service;

import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.vo.req.ModifyNameReq;
import org.zj.mychat.common.user.domain.vo.resp.UserInfoResp;

/**
 * <p>
 * 用户表 服务类
 * </p>
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-07
 */
public interface UserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);
}
