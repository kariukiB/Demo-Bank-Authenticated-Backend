package com.msme.bank.utils;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE ="001";
    public static final String ACCOUNT_EXISTS_MESSAGE  ="This user already has an account";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE= "Account successfully created.";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with provided account number does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS ="User account found";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Deposit transaction posted success ";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient funds for this transaction";
    public static final String ACCOUNT_DEBITED_SUCCESS = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Withdraw transaction success";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer transaction posted success";
    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int minimum  = 100000;
        int maximum = 999999;
        String sol = "0020";

        int randomNumber = (int) Math.floor(Math.random() * (maximum - minimum + 1) + minimum);
        String year = String.valueOf(currentYear);
        String randomNumber1 = String.valueOf(randomNumber);
        return sol + year + randomNumber1;

    }

}
