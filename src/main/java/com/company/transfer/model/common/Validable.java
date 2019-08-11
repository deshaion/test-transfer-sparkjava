package com.company.transfer.model.common;

import java.util.Optional;

public interface Validable {
    Optional<ErrorMessage> validate();
}
