package com.company.transfer.handlers;

import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.handlers.common.AbstractRequestHandler;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.model.Transfer;

import java.util.Map;

public class PostTransfersHandler extends AbstractRequestHandler<Transfer> {
    private TransfersDAO transfersDAO;

    public PostTransfersHandler(TransfersDAO transfersDAO) {
        super(Transfer.class);

        this.transfersDAO = transfersDAO;
    }

    @Override
    public Answer process(Transfer value, Map<String, String> urlParams) {
        return null;
    }
}
