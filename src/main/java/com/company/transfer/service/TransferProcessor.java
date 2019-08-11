package com.company.transfer.service;

import com.company.transfer.handlers.common.Answer;

public interface TransferProcessor {
    Answer process() throws Exception;
}
