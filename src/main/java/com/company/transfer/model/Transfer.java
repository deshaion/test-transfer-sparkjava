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
public class Transfer extends ErrorMessage implements Validable {
    private Long id;
    private String requestId;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private LocalDateTime created;

    @Builder
    public Transfer(String errorCode, String errorMessage, Long id, String requestId, Long sourceAccountId, Long targetAccountId, BigDecimal amount, LocalDateTime created) {
        super(errorCode, errorMessage);
        this.id = id;
        this.requestId = requestId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.created = created;
    }

    @Override
    public Optional<ErrorMessage> validate() {
        if (requestId == null || requestId.isEmpty()) {
            return Optional.of(
                    ErrorMessage.errorBuilder()
                            .errorCode("emptyRequestId")
                            .errorMessage("Request ID parameter can't be empty for avoiding duplication")
                            .build());
        }
        if (sourceAccountId == null || sourceAccountId < 0) {
            return Optional.of(
                    ErrorMessage.errorBuilder()
                            .errorCode("emptySource")
                            .errorMessage("Source account can't be empty")
                            .build());
        }

        if (targetAccountId == null || targetAccountId < 0) {
            return Optional.of(
                    ErrorMessage.errorBuilder()
                            .errorCode("emptyTarget")
                            .errorMessage("Target account can't be empty")
                            .build());
        }

        if (targetAccountId.equals(sourceAccountId)) {
            return Optional.of(
                    ErrorMessage.errorBuilder()
                            .errorCode("SourceAndTargetTheSame")
                            .errorMessage("Source and target must be different")
                            .build());
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of(
                    ErrorMessage.errorBuilder()
                            .errorCode("invalidAmount")
                            .errorMessage("Amount of transfer must be more than 0")
                            .build());
        }

        return Optional.empty();
    }

    // Don't forget after generating equals method that for comparing BigDecimal values is better to use compareTo method
    //if (amount != null ? amount.compareTo(transfer.amount) != 0 : transfer.amount != null) return false;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        if (id != null ? !id.equals(transfer.id) : transfer.id != null) return false;
        if (requestId != null ? !requestId.equals(transfer.requestId) : transfer.requestId != null) return false;
        if (sourceAccountId != null ? !sourceAccountId.equals(transfer.sourceAccountId) : transfer.sourceAccountId != null)
            return false;
        if (targetAccountId != null ? !targetAccountId.equals(transfer.targetAccountId) : transfer.targetAccountId != null)
            return false;
        if (amount != null ? amount.compareTo(transfer.amount) != 0 : transfer.amount != null) return false;
        return created != null ? created.equals(transfer.created) : transfer.created == null;
    }

}
