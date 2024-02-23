package org.zj.mychat.common.user.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zj.mychat.common.common.domain.vo.resp.ApiResult;
import org.zj.mychat.common.common.utils.AssertUtil;
import org.zj.mychat.common.common.utils.RequestHolder;
import org.zj.mychat.common.user.domain.enums.RoleEnum;
import org.zj.mychat.common.user.domain.vo.req.BlackReq;
import org.zj.mychat.common.user.domain.vo.req.ModifyNameReq;
import org.zj.mychat.common.user.domain.vo.req.WearingBagdeReq;
import org.zj.mychat.common.user.domain.vo.resp.BadgeResp;
import org.zj.mychat.common.user.domain.vo.resp.UserInfoResp;
import org.zj.mychat.common.user.service.RoleService;
import org.zj.mychat.common.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-07
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/userinfo")
    @ApiOperation("获取用户个人信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req.getName());
        return ApiResult.success();
    }

    /**
     * 获取徽章列表
     */
    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    /**
     * 佩戴徽章
     */
    @PutMapping("/badges")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadges(@Valid @RequestBody WearingBagdeReq req) {
        userService.wearingBadges(RequestHolder.get().getUid(), req.getItemId());
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "管理员无权限");
        userService.black(req);
        return ApiResult.success();
    }
}

