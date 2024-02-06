package org.zj.mychat.common.websocket.domain.vo.req;

import lombok.Data;

/**
 * 前后端协作的协议基础类
 */
@Data
public class WSBaseReq {
    /**
     * @see org.zj.mychat.common.websocket.domain.enums.WSReqTypeEnum
     */
    private Integer type;

    /**
     * 协作数据
     */
    private String data;
}
