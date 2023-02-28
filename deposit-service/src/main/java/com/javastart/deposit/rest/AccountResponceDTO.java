package com.javastart.deposit.rest;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponceDTO {

    private Long accountId;

    private String email;

    private String name;

    private String phone;

    private List<Long> bills;

    private OffsetDateTime creationDate;


}
