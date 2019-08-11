package com.company.transfer.handlers;

import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.handlers.common.AbstractRequestHandler;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.model.common.Empty;

import java.util.Map;

public class GetTransfersHandler extends AbstractRequestHandler<Empty> {

    private TransfersDAO transfersDAO;

    public GetTransfersHandler(TransfersDAO transfersDAO) {
        super(Empty.class);

        this.transfersDAO = transfersDAO;
    }

    @Override
    public Answer process(Empty value, Map<String, String> urlParams) {
        return Answer.ok(dataToJson(transfersDAO.getAll()));
    }
}
