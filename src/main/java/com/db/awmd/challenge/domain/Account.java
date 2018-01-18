package com.db.awmd.challenge.domain;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.exception.TransactionInProgressException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId, @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  public void withdraw(BigDecimal amount) {
    Lock lock = new ReentrantLock();
    if (!lock.tryLock())
      throw new TransactionInProgressException(
          "There is already a transaction in progress on payee Account, Please try again");
    else {
      if (this.getBalance().compareTo(amount) <= 0)
        throw new NegativeBalanceException("Amount greater than balance can not be transferred");
      this.setBalance(this.getBalance().subtract(amount));
    }
  }

  public void deposit(BigDecimal amount) {
    Lock lock = new ReentrantLock();
    if (!lock.tryLock())
      throw new TransactionInProgressException(
          "There is already a transaction in progress on Beneficiary Account, Please try again");
    else
      this.setBalance(this.getBalance().add(amount));
  }
}
