package com.company.transfer;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.impl.AccountsDAOImpl;
import com.company.transfer.model.Account;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class AccountsTest extends BaseTest {

    private static AccountsDAO accountsDAO;

    @BeforeClass
    public static void setUp() {
        BaseTest.startWebServer();

        accountsDAO = new AccountsDAOImpl(BaseTest.getSql2o());
    }

    @Before
    public void beforeTest() {
        accountsDAO.hardDeleteAll();
    }

    @Test
    public void testAccountsCreating() throws IOException {
        Account account = createAccount("Peter", 100);

        Account expectedAccount = Account.builder()
                .id(0L)
                .name("Peter")
                .balance(BigDecimal.valueOf(100))
                .active(true)
                .build();

        assertThat(accountWithoutCreatedDate(account), is(expectedAccount));

        HttpResponse response = HttpRequest.get(API_ACCOUNTS).send();

        List<Account> expectedList = new ArrayList<>();
        expectedList.add(expectedAccount);

        assertThat(response.statusCode(), is(200));
        assertThat(response.contentType(), is("application/json"));
        assertThat(readAccountList(response.body()), is(expectedList));
    }

    @Test
    public void testGetEmptyListOfAccounts() throws IOException {
        HttpResponse response = HttpRequest.get(API_ACCOUNTS).send();

        assertThat(response.statusCode(), is(200));
        assertThat(response.contentType(), is("application/json"));
        assertThat(readAccountList(response.body()), is(Collections.emptyList()));
    }

    @Test
    public void postEmpty() {
        HttpResponse response = HttpRequest.post(API_ACCOUNTS).body("{}").send();

        assertThat(response.statusCode(), is(400));
    }

    @Test
    public void postWrongBalance() {
        HttpResponse response = HttpRequest.post(API_ACCOUNTS).body("{\"name\":\"Peter\", \"balance\":\"balance\"}").send();

        assertThat(response.statusCode(), is(400));
    }

}