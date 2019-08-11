package com.company.transfer.model;

import com.company.transfer.handlers.common.ErrorMessage;

import java.util.Optional;

public interface Validable {
    Optional<ErrorMessage> validate();
}
