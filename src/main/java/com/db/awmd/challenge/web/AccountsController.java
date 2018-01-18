package com.db.awmd.challenge.web;

import java.math.BigDecimal;
import java.text.MessageFormat;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountsTransferDTO;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.exception.TransactionInProgressException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  private final NotificationService notificationService;

  @Value("${withdraw-message}")
  String withdrawalMessage;
  @Value("${deposit-message}")
  String depositMessage;

  @Autowired
  public AccountsController(AccountsService accountsService, NotificationService notificationService) {
    this.accountsService = accountsService;
    this.notificationService = notificationService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
      this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }

  @PostMapping(path = "/transfer")
  public ResponseEntity<Object> transferAmount(@RequestBody AccountsTransferDTO accountTransferDTO) {
    log.info("Initiating transfer from acccount {} to {} of amount {}", accountTransferDTO.getFromAccountId(),
        accountTransferDTO.getToAccountId(), accountTransferDTO.getAmount());
    ResponseEntity<Object> responseEntity;
    try {
      Account fromAccount = this.accountsService.getAccount(accountTransferDTO.getFromAccountId());
      Account toAccount = this.accountsService.getAccount(accountTransferDTO.getToAccountId());
      this.accountsService.transferAmount(fromAccount, toAccount, accountTransferDTO.getAmount());
      notifyAccountOwners(fromAccount, toAccount, accountTransferDTO.getAmount());
      responseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);
    } catch (TransactionInProgressException te) {
      responseEntity = new ResponseEntity<>(te.getMessage(), HttpStatus.CONFLICT);
    } catch (NegativeBalanceException ne) {
      responseEntity = new ResponseEntity<>(ne.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return responseEntity;
  }

  private void notifyAccountOwners(Account fromAccount, Account toAccount, BigDecimal amount) {
    this.notificationService.notifyAboutTransfer(fromAccount,
        MessageFormat.format(withdrawalMessage, String.valueOf(amount)));
    this.notificationService.notifyAboutTransfer(toAccount,
        MessageFormat.format(depositMessage, String.valueOf(amount)));
  }

}
