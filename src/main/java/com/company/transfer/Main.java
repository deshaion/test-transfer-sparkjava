package com.company.transfer;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.dao.impl.AccountsDAOImpl;
import com.company.transfer.dao.impl.TransfersDAOImpl;
import com.company.transfer.handlers.*;
import org.sql2o.Sql2o;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(3000);

        Sql2o sql2o = new Sql2o("jdbc:hsqldb:mem:test?shutdown=true", "SA", "");

        AccountsDAO accountsDAO = new AccountsDAOImpl(sql2o);

        get("/accounts", new GetAccountsHandler(accountsDAO));
        get("/accounts/:id", new GetSingleAccountHandler(accountsDAO));
        post("/accounts", new PostAccountsHandler(accountsDAO));

        TransfersDAO transfersDAO = new TransfersDAOImpl(sql2o);

        post("/transfers", new PostTransfersHandler(transfersDAO, accountsDAO));
        get("/transfers", new GetTransfersHandler(transfersDAO));
    }
}
