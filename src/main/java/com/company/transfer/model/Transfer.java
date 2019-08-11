package com.company.transfer.model;

import com.company.transfer.handlers.common.ErrorMessage;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transfer implements Validable {
    private Long requestId;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private LocalDateTime created;

    @Override
    public Optional<ErrorMessage> validate() {
        if (sourceAccountId == null || sourceAccountId < 0) {
            return Optional.of(
                    ErrorMessage.builder()
                            .errorCode("emptySource")
                            .errorMessage("Source account can't be empty")
                            .build());
        }

        if (targetAccountId == null || targetAccountId < 0) {
            return Optional.of(
                    ErrorMessage.builder()
                            .errorCode("emptyTarget")
                            .errorMessage("Target account can't be empty")
                            .build());
        }

        if (targetAccountId.equals(sourceAccountId)) {
            return Optional.of(
                    ErrorMessage.builder()
                            .errorCode("SourceAndTargetTheSame")
                            .errorMessage("Source and target must be different")
                            .build());
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of(
                    ErrorMessage.builder()
                            .errorCode("invalidAmount")
                            .errorMessage("Amount of transfer must be more than 0")
                            .build());
        }

        return Optional.empty();
    }

    // Don't forget after generating equals method that for comparing BigDecimal values is better to use compareTo method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        if (sourceAccountId != transfer.sourceAccountId) return false;
        if (targetAccountId != transfer.targetAccountId) return false;
        if (requestId != null ? !requestId.equals(transfer.requestId) : transfer.requestId != null) return false;
        if (amount != null ? amount.compareTo(transfer.amount) != 0 : transfer.amount != null) return false;
        return created != null ? created.equals(transfer.created) : transfer.created == null;
    }

    @Override
    public int hashCode() {
        int result = requestId != null ? requestId.hashCode() : 0;
        result = 31 * result + (int) (sourceAccountId ^ (sourceAccountId >>> 32));
        result = 31 * result + (int) (targetAccountId ^ (targetAccountId >>> 32));
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

}
