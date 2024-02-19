package org.zj.mychat.common.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * ip 详情实体类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpDetail implements Serializable {

    private String ip;

    private String isp;

    private String ispId;

    private String city;

    private String cityId;

    private String country;

    private String countryId;

    private String region;

    private String regionId;
}
