package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.controller.auth.Handler;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.data.Transaction;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.request.TopupBalanceRequest;
import id.wantox86.ewallet.model.response.HandlerResponse;
import id.wantox86.ewallet.util.DateTimeHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.apache.commons.lang3.RandomStringUtils;
import rx.Observable;

import java.util.Date;

import static id.wantox86.ewallet.Constants.*;

/**
 * Created by wawan on 17/12/21.
 */
public class TopupBalanceController implements Handler {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private TransactionDataStore transactionDataStore;
    private Gson gson;

    public TopupBalanceController(TransactionDataStore transactionDataStore, Gson gson) {
        this.transactionDataStore = transactionDataStore;
        this.gson = gson;
    }

    @Override
    public Observable<HandlerResponse> handle(RoutingContext routingContext, UserToken userToken) {
        TopupBalanceRequest request = getRequestParam(routingContext.getBodyAsString());
        if (request != null
                && request.getAmount() != null
                && request.getAmount() < 10000000) {

            return transactionDataStore.topupBalance(buildTransaction(userToken, request))
                    .map(result -> {
                        if (result == 1) {
                            return new HandlerResponse(HttpResponseStatus.NO_CONTENT, null);
                        }
                        return new HandlerResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
                    });
        } else {
            return Observable.just(new HandlerResponse(HttpResponseStatus.BAD_REQUEST, null));
        }
    }

    private TopupBalanceRequest getRequestParam(String request) {
        try {
            return gson.fromJson(request, TopupBalanceRequest.class);
        } catch (Exception e) {
            logger.error("cant parse request", request, e);
            return null;
        }
    }

    private Transaction buildTransaction(UserToken userToken, TopupBalanceRequest request) {
        Transaction transaction = new Transaction();

        Date createDate = new Date();
        transaction.setUsername(userToken.getUser());
        transaction.setSide(SIDE_CREDIT);
        transaction.setCreateTs(createDate);
        transaction.setTransactionType(TYPE_TOPUP);
        transaction.setAmount(request.getAmount());
        transaction.setCounterParty(userToken.getUser());
        transaction.setTransactionId(generateTransactionId());
        transaction.setRefId(transaction.getTransactionId());
        transaction.setCreateDate(DateTimeHelper.getDateOnly(createDate));

        return transaction;
    }

    private String generateTransactionId() {
        return RandomStringUtils.randomAlphanumeric(30);
    }
}