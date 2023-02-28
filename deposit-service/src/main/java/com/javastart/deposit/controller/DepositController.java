package com.javastart.deposit.controller;

import com.javastart.deposit.controller.dto.DepositRequestDTO;
import com.javastart.deposit.controller.dto.DepositResponceDTO;
import com.javastart.deposit.service.DepositService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/deposits")
    public DepositResponceDTO deposit(@RequestBody DepositRequestDTO requestDTO) {
        return depositService.deposit(requestDTO.getAccountId(), requestDTO.getBillId(), requestDTO.getAmount());
    }
}
