package com.company.transfer;

import com.company.transfer.model.Account;
import com.company.transfer.model.Transfer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.sql2o.Sql2o;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static spark.Spark.awaitInitialization;

abstract class BaseTest {
    protected static final String API_ACCOUNTS = "http://localhost:3000/accounts";
    protected static final String API_TRANSFERS = "http://localhost:3000/transfers";

    protected static final int HTTP_SUCCESS = 200;
    protected static final int HTTP_BAD_REQUEST = 400;

    private ObjectMapper objectMapper;

    BaseTest() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    static void startWebServer() {
        Main.main(new String[]{});
        awaitInitialization();
    }

    static Sql2o getSql2o() {
        return new Sql2o("jdbc:hsqldb:mem:test?shutdown=true", "SA", "");
    }

    List<Account> readAccountList(String json) throws IOException {
        List<Account> accountList = objectMapper.readValue(json, new TypeReference<List<Account>>() {});

        return accountList.stream().map(this::accountWithoutCreatedDate).collect(Collectors.toList());
    }

    Account accountWithoutCreatedDate(Account account) {
        return Account.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .active(account.getActive())
                .build();
    }

    Account createAccount(String name, int balance) throws IOException {
        return createAccount(name, BigDecimal.valueOf(balance));
    }

    Account createAccount(String name, BigDecimal balance) throws IOException {
        HttpResponse response = HttpRequest.post(API_ACCOUNTS).body("{\"name\":\"" + name + "\", \"balance\":\"" + balance.toString() + "\"}").send();

        if (response.statusCode() != 200) {
            System.out.println(response);
        }

        assertThat(response.statusCode(), is(200));
        assertThat(response.contentType(), is("application/json"));

        return objectMapper.readValue(response.body(), Account.class);
    }

    Transfer createTransfer(String body, int expectedStatusCode) throws IOException {
        HttpResponse response = HttpRequest.post(API_TRANSFERS).body(body).send();

        System.out.println(response);

        assertThat(response.statusCode(), is(expectedStatusCode));
        assertThat(response.contentType(), is("application/json"));

        return objectMapper.readValue(response.body(), Transfer.class);
    }

    Transfer createTransfer(Account accountA, Account accountB, int amount, int expectedStatusCode) throws IOException {
        return createTransfer(accountA, accountB, BigDecimal.valueOf(amount), expectedStatusCode);
    }

    Transfer createTransfer(Account accountA, Account accountB, BigDecimal amount, int expectedStatusCode) throws IOException {
        return createTransfer("{\"sourceAccountId\":\"" + accountA.getId() + "\", " +
                "\"targetAccountId\":\"" + accountB.getId() + "\", \"amount\":\"" + amount.toString() + "\"}", expectedStatusCode);

    }
}
