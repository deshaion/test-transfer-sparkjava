package com.company.transfer;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.impl.AccountsDAOImpl;
import com.company.transfer.model.Account;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.stream.Collectors;

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
        accountsDAO.deleteAll();
    }

    @Test
    public void testAccountsCreating() throws IOException {
        HttpResponse response = HttpRequest.post("http://localhost:3000/accounts").body("{\"name\":\"Peter\", \"balance\":\"100\"}").send();

        Account expectedAccount = Account.builder()
                .id(0L)
                .name("Peter")
                .balance(BigDecimal.valueOf(100))
                .active(true)
                .build();

        assertThat(response.statusCode(), is(200));
        assertThat(response.contentType(), is("application/json"));
        assertThat(readAccount(response.body()), is(expectedAccount));

        response = HttpRequest.get("http://localhost:3000/accounts").send();

        List<Account> expectedList = new ArrayList<>();
        expectedList.add(expectedAccount);

        assertThat(response.statusCode(), is(200));
        assertThat(response.contentType(), is("application/json"));
        assertThat(readAccountList(response.body()), is(expectedList));
    }

    @Test
    public void testGetEmptyListOfAccounts() throws IOException {
        HttpResponse response = HttpRequest.get("http://localhost:3000/accounts").send();

        assertThat(response.statusCode(), is(200));
        assertThat(response.contentType(), is("application/json"));
        assertThat(readAccountList(response.body()), is(Collections.emptyList()));
    }

    @Test
    public void postEmpty() {
        HttpResponse response = HttpRequest.post("http://localhost:3000/accounts").body("{}").send();

        assertThat(response.statusCode(), is(400));
    }

    @Test
    public void postWrongBalance() {
        HttpResponse response = HttpRequest.post("http://localhost:3000/accounts").body("{\"name\":\"Peter\", \"balance\":\"balance\"}").send();

        assertThat(response.statusCode(), is(400));
    }

    private Account readAccount(String json) throws IOException {
        Account account = objectMapper.readValue(json, Account.class);

        return accountWithoutCreatedDate(account);
    }

    private List<Account> readAccountList(String json) throws IOException {
        List<Account> accountList = objectMapper.readValue(json, new TypeReference<List<Account>>() {});

        return accountList.stream().map(this::accountWithoutCreatedDate).collect(Collectors.toList());
    }

    private Account accountWithoutCreatedDate(Account account) {
        return Account.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .active(account.getActive())
                .build();
    }
}