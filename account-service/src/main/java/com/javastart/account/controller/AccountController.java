package com.javastart.account.controller;

import com.javastart.account.controller.dto.AccountRequestDTO;
import com.javastart.account.controller.dto.AccountResponceDTO;
import com.javastart.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public AccountResponceDTO getAccount(@PathVariable Long accountId) {
        return new AccountResponceDTO(accountService.getAccountById(accountId));
    }

    @PostMapping("/")
    public Long createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(accountRequestDTO.getName(), accountRequestDTO.getEmail(), accountRequestDTO.getPhone(), accountRequestDTO.getBills());
    }

    @PutMapping("/{accountId}")
    public AccountResponceDTO updateAccount(@PathVariable Long accountId, @RequestBody AccountRequestDTO accountRequestDTO) {
        return new AccountResponceDTO(accountService.updateAccount(accountId, accountRequestDTO.getName(), accountRequestDTO.getEmail(), accountRequestDTO.getPhone(), accountRequestDTO.getBills()));
    }

    @DeleteMapping("/{accountId}")
    public AccountResponceDTO deleteAccount(@PathVariable Long accountId) {
        return new AccountResponceDTO(accountService.deleteAccount(accountId));
    }

}
