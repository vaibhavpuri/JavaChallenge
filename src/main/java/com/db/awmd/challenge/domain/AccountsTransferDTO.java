package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class AccountsTransferDTO {
  @NotNull
  @NotEmpty
  private String fromAccountId;

  @NotNull
  @NotEmpty
  private String toAccountId;

  @NotNull
  @Min(value = 1, message = "Transfer Amount should be greater than zero.")
  private BigDecimal amount;

  
  public AccountsTransferDTO() {
    super();
  }

  public AccountsTransferDTO(String fromAccountId, String toAccountId, BigDecimal amount) {
    super();
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.amount = amount;
  }

  public String getFromAccountId() {
    return fromAccountId;
  }

  public String getToAccountId() {
    return toAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((amount == null) ? 0 : amount.hashCode());
    result = prime * result + ((fromAccountId == null) ? 0 : fromAccountId.hashCode());
    result = prime * result + ((toAccountId == null) ? 0 : toAccountId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AccountsTransferDTO other = (AccountsTransferDTO) obj;
    if (amount == null) {
      if (other.amount != null)
        return false;
    } else if (!amount.equals(other.amount))
      return false;
    if (fromAccountId == null) {
      if (other.fromAccountId != null)
        return false;
    } else if (!fromAccountId.equals(other.fromAccountId))
      return false;
    if (toAccountId == null) {
      if (other.toAccountId != null)
        return false;
    } else if (!toAccountId.equals(other.toAccountId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AccountsTransferDTO [fromAccountId=" + fromAccountId + ", toAccountId=" + toAccountId + ", amount=" + amount
        + "]";
  }

}
