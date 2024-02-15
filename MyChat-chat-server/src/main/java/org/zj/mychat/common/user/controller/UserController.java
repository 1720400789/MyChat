package org.zj.mychat.common.user.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zj.mychat.common.common.domain.dto.RequestInfo;
import org.zj.mychat.common.common.domain.vo.resp.ApiResult;
import org.zj.mychat.common.common.utils.RequestHolder;
import org.zj.mychat.common.user.domain.vo.resp.UserInfoResp;

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

    @GetMapping("/userinfo")
    @ApiOperation("获取用户个人信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        RequestInfo requestInfo = RequestHolder.get();
        return null;
    }
}

