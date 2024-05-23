package com.msme.bank.controller;

import com.msme.bank.dto.*;
import com.msme.bank.service.UserService;
import com.msme.bank.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Account Management APIs")
public class UserController {
    @Autowired
    UserService userService;
    @Operation(
            summary = "Opening a new Account",
            description ="Opening a new account and allocate account number"

    )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 CREATED"
    )
    @PostMapping
    public Response<Object> createAccount(@RequestBody UserRequest userRequest){
      return   userService.createAccount(userRequest);
    }
    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @GetMapping("/get-balance")
    public Response<Object> balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/get-name")
    public String nameEnquiry(@RequestParam String accountNumber){
        return userService.nameEnquiry(new EnquiryRequest(accountNumber));
    }
    @PostMapping("/add-money")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }
    @PostMapping("/remove-money")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        return userService.debitAccount(request);
    }
    @PostMapping("/move-money")
    public BankResponse transfer(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }
    @GetMapping("/account-by-name")
    public Response<Object> findAccount(@RequestParam String name){
        return userService.accountEnquiry(name);
    }
}
