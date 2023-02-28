package com.javastart.deposit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javastart.deposit.controller.dto.DepositResponceDTO;
import com.javastart.deposit.entity.Deposit;
import com.javastart.deposit.exception.DepositServiceException;
import com.javastart.deposit.repository.DepositRepository;
import com.javastart.deposit.rest.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class DepositService {

    private final DepositRepository depositRepository;

    private final AccountServiceClient accountServiceClient;

    private final BillServiceClient billServiceClient;

    private final RabbitTemplate rabbitTemplate;

    private static final String TOPIC_EXCHANGE_DEPOSIT = "js.deposit.notify.exchange";
    private static final String ROUTING_KEY_DEPOSIT = "js.key.deposit";

    @Autowired
    public DepositService(DepositRepository depositRepository, AccountServiceClient accountServiceClient, BillServiceClient billServiceClient, RabbitTemplate rabbitTemplate) {
        this.depositRepository = depositRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DepositResponceDTO deposit(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null && billId == null) {
            throw new DepositServiceException("Account is null and bill is null");
        }
        if (billId != null) {
            BillResponceDto billResponceDto = billServiceClient.getBillById(billId);
            BillRequestDto billRequestDto = new BillRequestDto();
            billRequestDto.setAccountId(billResponceDto.getAccountId());
            billRequestDto.setCreationDate(billResponceDto.getCreationDate());
            billRequestDto.setDefault(billResponceDto.isDefault());
            billRequestDto.setOverdraftEnabled(billResponceDto.getOverdraftEnabled());
            billRequestDto.setAmount(billResponceDto.getAmount().add(amount));

            billServiceClient.update(billId, billRequestDto);

            AccountResponceDTO accountResponceDTO = accountServiceClient.getAccountById(billResponceDto.getAccountId());

            depositRepository.save(new Deposit(amount, billId, OffsetDateTime.now(), accountResponceDTO.getEmail()));

            DepositResponceDTO depositResponceDTO = new DepositResponceDTO(amount, accountResponceDTO.getEmail());

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT, objectMapper.writeValueAsString(depositResponceDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new DepositServiceException("Can not send message to RabbitMQ");
            }

            return depositResponceDTO;
        }
        BillResponceDto defaultBill = getDefaultBill(accountId);
        BillRequestDto billRequestDto = createBillRequest(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequestDto);
        AccountResponceDTO account = accountServiceClient.getAccountById(accountId);
        depositRepository.save(new Deposit(amount, defaultBill.getBillId(), OffsetDateTime.now(), account.getEmail()));
        return createResponce(amount, account);
    }

    private DepositResponceDTO createResponce(BigDecimal amount, AccountResponceDTO accountResponceDTO) {
        DepositResponceDTO depositResponceDTO = new DepositResponceDTO(amount, accountResponceDTO.getEmail());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT, objectMapper.writeValueAsString(depositResponceDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DepositServiceException("Can not send message to RabbitMQ");
        }

        return depositResponceDTO;
    }

    private BillRequestDto createBillRequest(BigDecimal amount, BillResponceDto billResponceDto) {
        BillRequestDto billRequestDto = new BillRequestDto();
        billRequestDto.setAccountId(billResponceDto.getAccountId());
        billRequestDto.setCreationDate(billResponceDto.getCreationDate());
        billRequestDto.setDefault(billResponceDto.isDefault());
        billRequestDto.setOverdraftEnabled(billResponceDto.getOverdraftEnabled());
        billRequestDto.setAmount(billResponceDto.getAmount().add(amount));
        return billRequestDto;
    }

    private BillResponceDto getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId).stream().filter(BillResponceDto::isDefault).findAny().orElseThrow(() -> new DepositServiceException("Unable to find default bill for account " + accountId));
    }

}
