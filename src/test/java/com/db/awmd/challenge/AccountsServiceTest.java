package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.exception.TransactionInProgressException;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Mock
  private Account mockAccount;

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  public void transferAmountTest() {

    Account fromAccount = new Account("Id-fAcc", new BigDecimal(300));
    this.accountsService.createAccount(fromAccount);

    Account toAccount = new Account("Id-tAcc", new BigDecimal(300));
    this.accountsService.createAccount(toAccount);
    this.accountsService.transferAmount(fromAccount, toAccount, new BigDecimal(100));
    assertThat(this.accountsService.getAccount("Id-fAcc").getBalance()).isEqualTo(new BigDecimal(200));
    assertThat(this.accountsService.getAccount("Id-tAcc").getBalance()).isEqualTo(new BigDecimal(400));

  }

  @Test
  public void transferAmountNegativeAmountException() {
    Account fromAccount = new Account("Id-fAcc2", new BigDecimal(300));
    this.accountsService.createAccount(fromAccount);

    Account toAccount = new Account("Id-tAcc2", new BigDecimal(300));
    this.accountsService.createAccount(toAccount);
    try {
      this.accountsService.transferAmount(fromAccount, toAccount, new BigDecimal(1000));
      fail("Should have failed when transfering amount higher than balance");
    } catch (NegativeBalanceException ne) {
      assertThat(ne.getMessage()).isEqualTo("Amount greater than balance can not be transferred");
    }
  }

  @Test
  public void transferWithdrawlAccountTransactionInprogressException() throws Exception {
    Account toAccount = new Account(Math.random() + " tId ", new BigDecimal("200"));
    doThrow(new TransactionInProgressException("Transaction already in progress")).when(mockAccount)
        .withdraw(new BigDecimal(100));
    try {
      this.accountsService.transferAmount(mockAccount, toAccount, new BigDecimal(100));
      fail("Should have failed when transaction in progress for fromAccount");
    } catch (TransactionInProgressException tpe) {
      assertThat(tpe.getMessage()).isEqualTo("Transaction already in progress");
    }
  }

  @Test
  public void transferDepositAccountTransactionInprogressException() throws Exception {
    Account fromAccount = new Account(Math.random() + " tId ", new BigDecimal("200"));
    doThrow(new TransactionInProgressException("Transaction already in progress")).when(mockAccount)
        .deposit(new BigDecimal(100));
    try {
      this.accountsService.transferAmount(fromAccount,mockAccount, new BigDecimal(100));
      fail("Should have failed when transaction in progress for fromAccount");
    } catch (TransactionInProgressException tpe) {
      assertThat(tpe.getMessage()).isEqualTo("Transaction already in progress");
    }
  }

}
