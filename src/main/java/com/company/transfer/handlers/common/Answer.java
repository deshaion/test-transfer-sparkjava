package com.company.transfer.handlers.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder @Getter @ToString
public class Answer {
    private int code;
    private String body;

    public static Answer ok(String body) {
        return Answer.builder().code(200).body(body).build();
    }
}
