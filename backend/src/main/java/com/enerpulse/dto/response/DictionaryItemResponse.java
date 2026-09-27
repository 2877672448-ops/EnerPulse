package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryItemResponse extends BaseDTO {
    private Long tenantId;
    private Long typeId;
    private String code;
    private String name;
    private String unit;
    private Integer sortNo;
    private String status;
}
