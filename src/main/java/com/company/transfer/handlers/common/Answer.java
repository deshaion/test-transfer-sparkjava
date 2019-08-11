package com.company.transfer.handlers.common;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class Answer {
    private int code;
    private Object body;

    public Answer(int code, Object body) {
        this.code = code;
        this.body = body;
    }

    public static Answer ok(Object body) {
        return new Answer(200, body);
    }

    public static Answer created(Object body) {
        return new Answer(201, body);
    }
}
