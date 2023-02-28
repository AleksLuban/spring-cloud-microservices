package com.javastart.account.controller.dto;

import com.javastart.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor

public class AccountResponceDTO {

    private Long accountId;

    private String email;

    private String name;

    private String phone;

    private List<Long> bills;

    private OffsetDateTime creationDate;

    public AccountResponceDTO(Account account) {
        accountId = account.getAccountId();
        email = account.getEmail();
        name = account.getName();
        phone = account.getPhone();
        bills = account.getBills();
        creationDate = account.getCreationDate();
    }
}
