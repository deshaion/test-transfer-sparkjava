package com.company.transfer.model;

import com.company.transfer.handlers.common.ErrorMessage;

import java.util.Optional;

public class Empty implements Validable {

    @Override
    public Optional<ErrorMessage> validate() {
        return Optional.empty();
    }
}
