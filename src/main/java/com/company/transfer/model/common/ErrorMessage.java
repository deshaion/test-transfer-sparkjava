package com.company.transfer.model.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ErrorMessage {
    public String errorCode;
    public String errorMessage;

    @Builder(builderMethodName = "errorBuilder")
    public ErrorMessage(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
