package org.zj.mychat.common.websocket.domain.vo.resp;

import lombok.Data;

@Data
public class WSBaseResp<T> {
    /**
     * @see org.zj.mychat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;

    private T data;
}
