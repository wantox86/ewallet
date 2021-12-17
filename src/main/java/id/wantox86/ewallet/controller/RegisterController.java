package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.request.RegisterRequest;
import id.wantox86.ewallet.model.response.RegisterResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by wawan on 16/12/21.
 */
public class RegisterController implements Handler<RoutingContext> {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Gson gson;
    private Datastore datastore;
    private TransactionDataStore transactionDataStore;

    public RegisterController(Gson gson, Datastore datastore, TransactionDataStore transactionDataStore) {
        this.gson = gson;
        this.datastore = datastore;
        this.transactionDataStore = transactionDataStore;
    }

    @Override
    public void handle(RoutingContext event) {
        try {
            RegisterRequest requestParam = getRequestParam(event.getBodyAsString());

            logger.info("request", requestParam);
            if (requestParam != null && requestParam.getUsername() != null) {
                String userToken = datastore.getToken(requestParam.getUsername())
                        .toBlocking().firstOrDefault(null);
                if (userToken == null) {
                    String token = generateToken();
                    transactionDataStore.insertUser(requestParam.getUsername(), token)
                            .subscribe(result -> {
                                if (result > 0) {
                                    RegisterResponse response = buildResponse(token);
                                    processResponse(event, HttpResponseStatus.OK, response);
                                } else {
                                    processResponse(event, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
                                }
                            });
                } else {
                    processResponse(event, HttpResponseStatus.CONFLICT, null);
                }
            } else {
                processResponse(event, HttpResponseStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("exception", e);
            processResponse(event, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private RegisterResponse buildResponse(String token) {
        RegisterResponse response = new RegisterResponse();
        response.setToken(token);

        return response;
    }

    private RegisterRequest getRequestParam(String request) {
        try {
            return gson.fromJson(request, RegisterRequest.class);
        } catch (Exception e) {
            logger.error("cant parse request", request, e);
            return null;
        }
    }

    private void processResponse(RoutingContext event, HttpResponseStatus status, RegisterResponse response) {
        event.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(status.code())
                .end(response != null ? gson.toJson(response) : "");
    }

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(40);
    }
}
