package com.javastart.notification.service;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositResponceDTO {

    private BigDecimal amount;

    private String mail;


}
