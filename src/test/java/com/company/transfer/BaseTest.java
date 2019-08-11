package com.company.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.sql2o.Sql2o;

import static spark.Spark.awaitInitialization;

abstract class BaseTest {
    protected ObjectMapper objectMapper;

    public BaseTest() {
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
}
