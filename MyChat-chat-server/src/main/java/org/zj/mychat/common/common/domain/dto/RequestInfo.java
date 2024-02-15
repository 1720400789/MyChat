package org.zj.mychat.common.common.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 用户请求信息
 */
@Data
@Builder
public class RequestInfo {

    /**
     * 用户的 uid
     */
    private Long uid;

    /**
     * 用户登录的 ip 地址
     */
    private String ip;
}
