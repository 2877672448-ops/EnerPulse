package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryTypeResponse extends BaseDTO {
    private Long tenantId;
    private String code;
    private String name;
}
