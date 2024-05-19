package com.msme.bank.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response <T>{
    private Integer statusCode;
    private String message;
    private T entity;
}
