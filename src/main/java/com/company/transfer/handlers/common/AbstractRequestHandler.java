package com.company.transfer.handlers.common;

import com.company.transfer.model.common.Empty;
import com.company.transfer.model.common.ErrorMessage;
import com.company.transfer.model.common.Validable;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractRequestHandler<V extends Validable> implements RequestHandler<V>, Route {
    private Class<V> valueClass;

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int SERVER_ERROR = 500;

    public AbstractRequestHandler(Class<V> valueClass){
        this.valueClass = valueClass;
    }

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(data);
        } catch (IOException e){
            throw new RuntimeException("", e);
        }
    }

    @Override
    public Object handle(Request request, Response response) {
        try {
            V value = null;
            if (valueClass != Empty.class) {
                ObjectMapper objectMapper = new ObjectMapper();
                value = objectMapper.readValue(request.body(), valueClass);
            }

            Map<String, String> urlParams = request.params();
            Answer answer = validateAndProcess(value, urlParams);

            response.status(answer.getCode());
            response.type("application/json");
            response.body(answer.getBody());
            return answer.getBody();
        } catch (JsonMappingException e) {
            String body = dataToJson(ErrorMessage.errorBuilder()
                    .errorCode("InputParserError")
                    .errorMessage(e.getMessage())
                    .build());

            response.status(HTTP_BAD_REQUEST);
            response.type("application/json");
            response.body(body);
            return body;
        } catch (Exception e) {
            String body = dataToJson(ErrorMessage.errorBuilder()
                    .errorCode("ServerError")
                    .errorMessage(e.getMessage())
                    .build());

            response.status(SERVER_ERROR);
            response.type("application/json");
            response.body(e.getMessage());
            return body;
        }
    }

    private Answer validateAndProcess(V value, Map<String, String> urlParams) {
        return validate(value).map(message -> new Answer(HTTP_BAD_REQUEST, dataToJson(message)))
                .orElseGet(() -> process(value, urlParams));
    }

    private Optional<ErrorMessage> validate(V value) {
        if (value == null) {
            return Optional.empty();
        }
        return value.validate();
    }
}
