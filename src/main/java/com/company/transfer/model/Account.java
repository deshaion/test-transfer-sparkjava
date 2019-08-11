package com.company.transfer.model;

import com.company.transfer.model.common.ErrorMessage;
import com.company.transfer.model.common.Validable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@ToString
@NoArgsConstructor
public class Account extends ErrorMessage implements Validable {
    private Long id;
    private String name;
    private LocalDateTime created;
    private BigDecimal balance;
    private Boolean active;

    @Builder
    public Account(String errorCode, String errorMessage, Long id, String name, LocalDateTime created, BigDecimal balance, Boolean active) {
        super(errorCode, errorMessage);
        this.id = id;
        this.name = name;
        this.created = created;
        this.balance = balance;
        this.active = active;
    }

    @Override
    public Optional<ErrorMessage> validate() {
        if (name == null || name.isEmpty()) {
            return Optional.of(
                    ErrorMessage.errorBuilder()
                            .errorCode("emptyName")
                            .errorMessage("Account's name can't empty")
                            .build());
        }

        return Optional.empty();
    }

    // Don't forget after generating equals method that for comparing BigDecimal values is better to use compareTo method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (!id.equals(account.id)) return false;
        if (name != null ? !name.equals(account.name) : account.name != null) return false;
        if (created != null ? !created.equals(account.created) : account.created != null) return false;
        if (balance != null ? balance.compareTo(account.balance) != 0 : account.balance != null) return false;
        return active != null ? active.equals(account.active) : account.active == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        return result;
    }

}
