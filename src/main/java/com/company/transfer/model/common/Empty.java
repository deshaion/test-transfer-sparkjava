package com.company.transfer.model.common;

import java.util.Optional;

public class Empty implements Validable {

    @Override
    public Optional<ErrorMessage> validate() {
        return Optional.empty();
    }
}
