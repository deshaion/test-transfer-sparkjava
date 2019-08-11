package com.company.transfer;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.dao.impl.AccountsDAOImpl;
import com.company.transfer.dao.impl.TransfersDAOImpl;
import org.junit.Before;
import org.junit.BeforeClass;

public class TransfersTest extends BaseTest {
    private static AccountsDAO accountsDAO;
    private static TransfersDAO transfersDAO;

    @BeforeClass
    public static void setUp() {
        BaseTest.startWebServer();

        accountsDAO = new AccountsDAOImpl(BaseTest.getSql2o());
        transfersDAO = new TransfersDAOImpl(BaseTest.getSql2o());
    }

    @Before
    public void beforeTest() {
        accountsDAO.deleteAll();
        transfersDAO.deleteAll();
    }

    
}
