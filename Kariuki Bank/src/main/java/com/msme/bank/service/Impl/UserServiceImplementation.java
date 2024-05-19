package com.msme.bank.service.Impl;

import com.msme.bank.config.JwtTokenProvider;
import com.msme.bank.dto.*;
import com.msme.bank.entity.Role;
import com.msme.bank.entity.User;
import com.msme.bank.repository.UserRepository;
import com.msme.bank.service.EmailService;
import com.msme.bank.service.TransactionService;
import com.msme.bank.service.UserService;
import com.msme.bank.utils.AccountUtils;
import com.msme.bank.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final TransactionService transactionService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImplementation(UserRepository userRepository, EmailService emailService, TransactionService transactionService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.transactionService = transactionService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Response<Object> createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())){
           return Response.builder()
                   .statusCode(HttpStatus.FORBIDDEN.value())
                   .message("User with email " + userRequest.getEmail() + " exists")
                   .entity(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhone(userRequest.getAlternativePhone())
                .status("ACTIVE")
                .role(Role.ROLE_ADMIN)
                .build();
        User savedUser = userRepository.save(newUser);
        //Sending Email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation At Kariuki and Sons Bank Pvt Ltd")
                .messageBody("Congratulations, your account with the details below has been created successfully.\n Your account details:\n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getOtherName() + " " +
                        savedUser.getLastName() + "\n AccountNumber: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlerts(emailDetails);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account created successfully")
                .entity(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName()+ " " + savedUser.getOtherName())
                        .build())
                .build();


    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        EmailDetails loginAlert = EmailDetails.builder()
                .recipient(loginDto.getEmail())
                .subject("New Login Alert!")
                .messageBody("Dear customer, \n There was a new login to your account! \n If you initiated this request you can ignore this email\n Otherwise contact your bank immediately!")
                .build();
        emailService.sendEmailAlerts(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Successful!")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse accountEnquiry(String name) {
       Optional<User> user = userRepository.findByFirstName(name);
       try{
           if (user.isEmpty()){
               return BankResponse.builder()
                       .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                       .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                       .build();

           }
           User foundUser = user.get();
           return BankResponse.builder()
                   .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                   .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                   .accountInfo(AccountInfo.builder()
                           .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + foundUser.getOtherName())
                           .accountNumber(foundUser.getAccountNumber())
                           .accountBalance(foundUser.getAccountBalance())
                           .build())
                   .build();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    @Override
    public Response<Object> balanceEnquiry(EnquiryRequest request) {
        //Checking if provided account exists
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return Response.builder()
                    .message("Account number " + request.getAccountNumber() + " not found!")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .entity(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account balance retrieved successfully")
                .entity(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " +  foundUser.getOtherName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //Saving the transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("Credit")
                .build();
        transactionService.saveTransaction(transactionDto);
        //Sending email alert for credit transaction
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userToCredit.getEmail())
                .subject("Deposit Transaction Alert!")
                .messageBody("Greetings Dear " + userToCredit.getFirstName() +"\n The sum of Ksh " + request.getAmount() + " has been credited to your account " + request.getAccountNumber())
                .build();
        emailService.sendEmailAlerts(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountNumber(request.getAccountNumber())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
       BigInteger availableBalance  = (userToDebit.getAccountBalance().toBigInteger());
       BigInteger amountToDebit = (request.getAmount().toBigInteger());

       if (availableBalance.intValue() < amountToDebit.intValue()){
           return BankResponse.builder()
                   .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                   .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                   .accountInfo(null)
                   .build();
       }
       else {
           userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
           userRepository.save(userToDebit);

           //Saving the transaction
           TransactionDto transactionDto = TransactionDto.builder()
                   .accountNumber(userToDebit.getAccountNumber())
                   .amount(request.getAmount())
                   .transactionType("Debit")
                   .build();
           transactionService.saveTransaction(transactionDto);

           return BankResponse.builder()
                   .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                   .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                   .accountInfo(AccountInfo.builder()
                           .accountName(userToDebit.getFirstName() + " " + userToDebit.getOtherName() + " " + userToDebit.getLastName())
                           .accountNumber(request.getAccountNumber())
                           .accountBalance(userToDebit.getAccountBalance())
                           .build())
                   .build();
       }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {

        boolean isBeneficiaryAccountExist = userRepository.existsByAccountNumber(request.getBeneficiaryAccount());
        if (!isBeneficiaryAccountExist){
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(request.getCustomerAccount());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        String sourceName = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("Debit Alert")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("Greetings dear " + sourceAccountUser.getFirstName() + ",\n The sum of " + request.getAmount() + "has been drawn from your account! The amount has been credited to " + request.getBeneficiaryAccount()+"\n Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlerts(debitAlert);


        User beneficiaryAccountUser = userRepository.findByAccountNumber(request.getBeneficiaryAccount());
        beneficiaryAccountUser.setAccountBalance(beneficiaryAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(beneficiaryAccountUser);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(beneficiaryAccountUser.getEmail())
                .messageBody("Greetings dear " + beneficiaryAccountUser.getFirstName() + ",\n The sum of " + request.getAmount() + "has been credited to your account from " +sourceName + "!\n Your current balance is " + beneficiaryAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlerts(creditAlert);

        //Saving the transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(beneficiaryAccountUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("Transfer")
                .build();
        transactionService.saveTransaction(transactionDto);



        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();



    }
}
