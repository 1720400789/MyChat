package org.zj.mychat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoResp {

    /**
     * 用户 id
     */
    @ApiModelProperty("用户id")
    private Long id;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String name;

    /**
     * 用户头像
     */
    @ApiModelProperty("用户头像")
    private String avatar;

    /**
     * 用户性别
     */
    @ApiModelProperty("用户性别")
    private Integer sex;

    /**
     * 用户改名卡剩余次数
     */
    @ApiModelProperty("用户改名卡剩余次数")
    private Integer modifyNameChance;
}
