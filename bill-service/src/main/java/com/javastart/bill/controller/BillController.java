package com.javastart.bill.controller;

import com.javastart.bill.controller.dto.BillRequestDto;
import com.javastart.bill.controller.dto.BillResponceDto;
import com.javastart.bill.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BillController {

    private final BillService billService;

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{billId}")
    public BillResponceDto getBill(@PathVariable Long billId) {
        return new BillResponceDto(billService.getBillById(billId));
    }

    @PostMapping("/")
    public Long createBill(@RequestBody BillRequestDto billRequestDto) {
        return billService.createBill(billRequestDto.getAccountId(), billRequestDto.getAmount(), billRequestDto.isDefault(), billRequestDto.getOverdraftEnabled());
    }

    @PutMapping("/{billId}")
    public BillResponceDto updateBill(@PathVariable Long billId, @RequestBody BillRequestDto billRequestDto) {
        return new BillResponceDto(billService.updateBill(billId, billRequestDto.getAccountId(), billRequestDto.getAmount(), billRequestDto.isDefault(), billRequestDto.getOverdraftEnabled()));
    }

    @DeleteMapping("/{billId}")
    public BillResponceDto deleteBill(@PathVariable Long billId) {
        return new BillResponceDto(billService.deleteBill(billId));
    }

    @GetMapping("/account/{accountId}")
    public List<BillResponceDto> getBillsByAccountId(@PathVariable Long accountId) {
        return billService.getBillsByAccountId(accountId).stream().map(BillResponceDto::new).collect(Collectors.toList());
    }
}
