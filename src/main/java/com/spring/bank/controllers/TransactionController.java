package com.spring.bank.controllers;


import com.spring.bank.entities.Transaction;
import com.spring.bank.entities.User;
import com.spring.bank.repositories.UserRepo;
import com.spring.bank.services.TransactionService;
import com.spring.bank.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserService userService;

    @PostMapping("/create/{id}")
//    @PreAuthorize("hasAuthority('read')")
//    @PreAuthorize("hasAuthority('read') or hasAuthority('write')")
    public ResponseEntity<String> makeTransactionPost(
            @PathVariable Integer id, @RequestBody Transaction transaction) {
        Boolean hasCreatedTransaction = transactionService.createTransaction(id, transaction);
        if (hasCreatedTransaction) {
            return new ResponseEntity<>("transaction created with pending status", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("a problem occurred", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * @param id          .
     * @param transaction .
     * @return .
     */
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelTransaction(
            @PathVariable Integer id, @RequestBody Transaction transaction) {

        Boolean hasCanceledTransaction = transactionService.cancelTransaction(id, transaction);
        if (hasCanceledTransaction) {
            return new ResponseEntity<>("transaction canceled, success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("problem occurred", HttpStatus.NOT_ACCEPTABLE);
        }

    }

    /**
     * @param id .
     * @return .
     */
    @GetMapping("/accept/{id}")
    public ResponseEntity<List<Transaction>> acceptTransactionsGet(@PathVariable Integer id, Authentication auth) {
        User loggedInUser = userService.loggedInUser(auth);
        if (loggedInUser.getRole().name().equals("ADMIN")) {
            List<Transaction> transactions = transactionService.getAllPendingTransactions(id);
            if (transactions != null) {
                return new ResponseEntity<>(transactions, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * @param id          .
     * @param transaction .
     * @return .
     */
    @PostMapping("/accept/{id}")
    public ResponseEntity<String> acceptTransactionsPost(
            @PathVariable Integer id, @RequestBody Transaction transaction, Authentication auth) {
        User loggedInUser = userService.loggedInUser(auth);
        if (loggedInUser.getRole().name().equals("ADMIN")) {
            Boolean hasAcceptedTransaction = transactionService.acceptTransaction(id, transaction);
            if (hasAcceptedTransaction) {
                return new ResponseEntity<>("approved", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
