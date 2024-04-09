package com.msme.bank.controller;

import com.itextpdf.text.DocumentException;
import com.msme.bank.entity.Transaction;
import com.msme.bank.service.Impl.BankStatement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Statement Generator API")
@RequestMapping("/get-statement")
public class TransactionController {
    private BankStatement bankStatement;

    @GetMapping("/")
    public List<Transaction> generateStatement(@RequestParam String accountNumber,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber,startDate,endDate);
    }
}
