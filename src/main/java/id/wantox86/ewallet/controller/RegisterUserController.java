package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.controller.auth.Handler;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.request.RegisterRequest;
import id.wantox86.ewallet.model.response.HandlerResponse;
import id.wantox86.ewallet.model.response.RegisterResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.apache.commons.lang3.RandomStringUtils;
import rx.Observable;

/**
 * Created by wawan on 18/12/21.
 */
public class RegisterUserController implements Handler {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Gson gson;
    private Datastore datastore;
    private TransactionDataStore transactionDataStore;

    public RegisterUserController(Gson gson, Datastore datastore, TransactionDataStore transactionDataStore) {
        this.gson = gson;
        this.datastore = datastore;
        this.transactionDataStore = transactionDataStore;
    }

    @Override
    public Observable<HandlerResponse> handle(RoutingContext event, UserToken userToken) {
        RegisterRequest requestParam = getRequestParam(event.getBodyAsString());

        logger.info("request", requestParam);
        if (requestParam != null && requestParam.getUsername() != null) {
            return datastore.getToken(requestParam.getUsername())
                    .concatMap(token -> {
                        if (token == null) {
                            String generatedToken = generateToken();
                            return transactionDataStore.insertUser(requestParam.getUsername(), generatedToken)
                                    .map(result -> {
                                        if (result > 0) {
                                            RegisterResponse response = buildResponse(generatedToken);
                                            return new HandlerResponse(HttpResponseStatus.OK, gson.toJson(response));
                                        } else {
                                            return new HandlerResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
                                        }
                                    });
                        } else {
                            return Observable.just(new HandlerResponse(HttpResponseStatus.CONFLICT, null));
                        }
                    });
        } else {
            return Observable.just(new HandlerResponse(HttpResponseStatus.BAD_REQUEST, null));
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

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(40);
    }
}
