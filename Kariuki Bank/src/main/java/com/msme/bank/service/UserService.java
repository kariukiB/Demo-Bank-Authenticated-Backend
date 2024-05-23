package com.msme.bank.service;

import com.msme.bank.dto.*;
import com.msme.bank.utils.Response;

public interface UserService {
   Response<Object> createAccount(UserRequest userRequest);
   Response<Object> balanceEnquiry(EnquiryRequest request);
   String nameEnquiry(EnquiryRequest request);
   BankResponse creditAccount(CreditDebitRequest request);
   BankResponse debitAccount(CreditDebitRequest request);
   BankResponse transfer(TransferRequest request);
   BankResponse login(LoginDto loginDto);
   Response accountEnquiry(String name);
}
