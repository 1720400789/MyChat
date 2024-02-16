package org.zj.mychat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyNameReq {

    @ApiModelProperty("用户名")
    @NotBlank
    @Length(max = 6, message = "用户名不可以取太长哦")
    private String name;

    @NotNull(message = "别忘记传 id")
    private Integer id;
}
