package com.company.transfer.handlers;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.handlers.common.AbstractRequestHandler;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.model.Transfer;
import com.company.transfer.model.common.ErrorMessage;
import com.company.transfer.service.TransferProcessorImpl;

import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class PostTransfersHandler extends AbstractRequestHandler<Transfer> {
    private TransfersDAO transfersDAO;
    private AccountsDAO accountsDAO;

    public PostTransfersHandler(TransfersDAO transfersDAO, AccountsDAO accountsDAO) {
        super(Transfer.class);

        this.transfersDAO = transfersDAO;
        this.accountsDAO = accountsDAO;
    }

    @Override
    public Answer process(Transfer transfer, Map<String, String> urlParams) {
        if (transfersDAO.isTransferExist(transfer.getRequestId())) {
            return new Answer(HTTP_BAD_REQUEST,
                        ErrorMessage.errorBuilder()
                                    .errorCode("DuplicatedRequest")
                                    .errorMessage("The request with the same ID has already been processed.")
                                    .build()
            );
        }

        try {
            return TransferProcessorImpl.builder()
                    .accountsDAO(accountsDAO)
                    .transfersDAO(transfersDAO)
                    .transfer(transfer)
                    .build()
                    .process();
        } catch (Exception e) {
            return new Answer(HTTP_INTERNAL_ERROR,
                    ErrorMessage.errorBuilder()
                            .errorCode("TransferProcessingError")
                            .errorMessage("Error was occured during transfer processing. Please contact us.")
                            .build()
            );
        }
    }

}
