package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AreaResponse extends BaseDTO {
    private Long tenantId;
    private Long parentId;
    private String areaType;
    private String code;
    private String name;
    private Integer sortNo;
    private String status;
    private java.util.List<AreaResponse> children;
}
