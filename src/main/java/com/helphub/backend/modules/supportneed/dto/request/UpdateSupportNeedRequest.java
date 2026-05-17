package com.helphub.backend.modules.supportneed.dto.request;

import com.helphub.backend.common.enums.SupportNeedUnit;
import com.helphub.backend.common.enums.SupportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateSupportNeedRequest {

    @NotNull(message = "Support type is required")
    private SupportType supportType;

    @NotBlank(message = "Need name is required")
    @Size(max = 100, message = "Need name must not exceed 100 characters")
    private String needName;

    @NotNull(message = "Unit is required")
    private SupportNeedUnit unit;

    @NotNull(message = "Required quantity is required")
    @DecimalMin(value = "0.01", message = "Required quantity must be greater than 0")
    private BigDecimal requiredQuantity;
}