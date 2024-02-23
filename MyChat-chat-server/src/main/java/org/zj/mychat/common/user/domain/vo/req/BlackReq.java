package org.zj.mychat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 */
@Data
public class BlackReq {

    @ApiModelProperty("拉黑用户的 uid")
    @NotNull
    private Long uid;
}
