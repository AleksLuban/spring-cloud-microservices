package com.javastart.bill.controller.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
public class BillRequestDto {

    private Long accountId;

    private BigDecimal amount;

    private boolean isDefault;

    private OffsetDateTime creationDate;

    private Boolean overdraftEnabled;
}
