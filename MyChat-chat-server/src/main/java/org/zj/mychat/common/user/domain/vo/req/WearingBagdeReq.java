package org.zj.mychat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 */
@Data
public class WearingBagdeReq {

    @ApiModelProperty("徽章 id")
    @NotNull
    private Long itemId;
}
