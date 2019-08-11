package com.company.transfer.handlers.common;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class ErrorMessage {
    public String errorCode;
    public String errorMessage;
}
