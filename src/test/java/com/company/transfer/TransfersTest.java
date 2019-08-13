package com.company.transfer;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.dao.impl.AccountsDAOImpl;
import com.company.transfer.dao.impl.TransfersDAOImpl;
import com.company.transfer.model.Account;
import com.company.transfer.model.Transfer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
        accountsDAO.hardDeleteAll();
        transfersDAO.hardDeleteAll();
    }

    @Test
    public void basicTest() throws IOException {
        Account accountA = createAccount("Peter", 100);
        Account accountB = createAccount("Lucas", 50);

        Transfer transfer = createTransfer("req_1", accountA, accountB, 40, HTTP_CREATED);

        assertThat(transfer.errorCode, is(nullValue()));

        checkBalance(accountA, 60);
        checkBalance(accountB, 90);
    }

    @Test
    public void transferTheSameAccount() throws IOException {
        Account accountA = createAccount("Peter", 100);

        Transfer transfer = createTransfer("req_1", accountA, accountA, 40, HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("SourceAndTargetTheSame"));
        assertThat(transfer.getErrorMessage(), is("Source and target must be different"));
    }

    @Test
    public void transferEmptyRequestId() throws IOException {
        Transfer transfer = createTransfer("{\"sourceAccountId\":\"0\", \"amount\":\"40\"}", HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("emptyRequestId"));
        assertThat(transfer.getErrorMessage(), is("Request ID parameter can't be empty for avoiding duplication"));
    }

    @Test
    public void transferWithoutTarget() throws IOException {
        Account accountA = createAccount("Peter", 100);

        Transfer transfer = createTransfer("{\"requestId\": \"req_1\", \"sourceAccountId\":\"" + accountA.getId() + "\", \"amount\":\"40\"}", HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("emptyTarget"));
        assertThat(transfer.getErrorMessage(), is("Target account can't be empty"));
    }

    @Test
    public void transferEmptySource() throws IOException {
        Account accountA = createAccount("Peter", 100);

        Transfer transfer = createTransfer("{\"requestId\": \"req_1\", \"targetAccountId\":\"" + accountA.getId() + "\", \"amount\":\"40\"}", HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("emptySource"));
        assertThat(transfer.getErrorMessage(), is("Source account can't be empty"));
    }

    @Test
    public void transferEmptyAmount() throws IOException {
        Account accountA = createAccount("Peter", 100);
        Account accountB = createAccount("Lucas", 100);

        Transfer transfer = createTransfer("{\"requestId\": \"req_1\", \"sourceAccountId\":\"" + accountA.getId() + "\", \"targetAccountId\":\"" + accountB.getId() +
                "\", \"amount\":\"0\"}", HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("invalidAmount"));
        assertThat(transfer.getErrorMessage(), is("Amount of transfer must be more than 0"));
    }

    @Test
    public void checkDuplication() throws IOException {
        Account accountA = createAccount("Peter", 100);
        Account accountB = createAccount("Lucas", 50);

        createTransfer("req_1", accountA, accountB, 40, HTTP_CREATED);
        Transfer transfer = createTransfer("req_1", accountA, accountB, 40, HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("DuplicatedRequest"));
        assertThat(transfer.getErrorMessage(), is("The request with the same ID has already been processed."));
    }

    @Test
    public void sourceAccountNotFound() throws IOException {
        Account accountA = Account.builder().id(10L).build();
        Account accountB = createAccount("Lucas", 50);

        Transfer transfer = createTransfer("req_1", accountA, accountB, 40, HTTP_NOT_FOUND);

        assertThat(transfer.getErrorCode(), is("AccountNotFound"));
        assertThat(transfer.getErrorMessage(), is("The account with ID 10 is not found."));
    }

    @Test
    public void targetAccountNotFound() throws IOException {
        Account accountA = createAccount("Peter", 100);
        Account accountB = Account.builder().id(11L).build();

        Transfer transfer = createTransfer("req_1", accountA, accountB, 40, HTTP_NOT_FOUND);

        assertThat(transfer.getErrorCode(), is("AccountNotFound"));
        assertThat(transfer.getErrorMessage(), is("The account with ID 11 is not found."));
    }

    @Test
    public void checkNotEnoughMoney() throws IOException {
        Account accountA = createAccount("Peter", 100);
        Account accountB = createAccount("Lucas", 50);

        Transfer transfer = createTransfer("req_1", accountA, accountB, 200, HTTP_BAD_REQUEST);

        assertThat(transfer.getErrorCode(), is("NotEnoughMoney"));
        assertThat(transfer.getErrorMessage(), is("Account " + accountA.getId() + " has no enough money on the balance"));
    }

    @Test
    public void checkDeadLock() throws IOException, InterruptedException {
        Account accountA = createAccount("Peter", 1000);
        Account accountB = createAccount("Lucas", 500);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Random random = new Random();

        AtomicInteger balanceA = new AtomicInteger(1000);
        AtomicInteger balanceB = new AtomicInteger(500);

        AtomicInteger reqId = new AtomicInteger(0);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                try {
                    int amount = 1 + random.nextInt(5);
                    Account firstAcc;
                    Account secondAcc;

                    if (random.nextBoolean()) {
                        firstAcc = accountA;
                        secondAcc = accountB;

                        balanceA.updateAndGet(x -> x - amount);
                        balanceB.updateAndGet(x -> x + amount);
                    } else {
                        firstAcc = accountB;
                        secondAcc = accountA;

                        balanceA.updateAndGet(x -> x + amount);
                        balanceB.updateAndGet(x -> x - amount);
                    }

                    countDownLatch.await();
                    createTransfer(String.valueOf(reqId.getAndIncrement()), firstAcc, secondAcc, amount, HTTP_CREATED);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        countDownLatch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);

        checkBalance(accountA, balanceA.get());
        checkBalance(accountB, balanceB.get());
    }
}
