package id.wantox86.ewallet.controller;

import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.gson.Gson;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.data.Transaction;
import id.wantox86.ewallet.model.data.TransactionDebitSummary;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.request.TransferRequest;
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
public class TransferController implements Handler {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Datastore datastore;
    private TransactionDataStore transactionDataStore;
    private Gson gson;

    public TransferController(Datastore datastore, TransactionDataStore transactionDataStore, Gson gson) {
        this.transactionDataStore = transactionDataStore;
        this.datastore = datastore;
        this.gson = gson;
    }

    @Override
    public Observable<HandlerResponse> handle(RoutingContext routingContext, UserToken userToken) {
        TransferRequest request = getRequestParam(routingContext.getBodyAsString());
        String sender = userToken.getUser();
        String receiver = request.getToUsername();
        Double amount = request.getAmount();
        return Observable.zip(
                datastore.getUserBalance(sender),
                datastore.getToken(receiver),
                (balance, tokenReceiver) -> new Tuple2<Double, String>(balance, tokenReceiver)
        ).concatMap(validation -> {
            Double balance = validation.value1();
            String userReceiver = validation.value2();
            if (amount > balance || sender.equals(receiver)) {
                return Observable.just(new HandlerResponse(HttpResponseStatus.BAD_REQUEST, null));
            } else if (userReceiver == null) {
                return Observable.just(new HandlerResponse(HttpResponseStatus.NOT_FOUND, null));
            }
            return processTransfer(sender, receiver, amount);
        });
    }

    private TransferRequest getRequestParam(String request) {
        try {
            return gson.fromJson(request, TransferRequest.class);
        } catch (Exception e) {
            logger.error("cant parse request", request, e);
            return null;
        }
    }

    private Observable<HandlerResponse> processTransfer(String sender, String receiver, Double amount) {
        Date createDate = new Date();

        String refId = generateId();
        Transaction debit = buildTransaction(sender, receiver, amount, SIDE_DEBIT, createDate, refId);
        Transaction credit = buildTransaction(receiver, sender, amount, SIDE_CREDIT, createDate, refId);
        TransactionDebitSummary debitSummary = buildTrxDebitSummary(sender, amount, createDate);

        return transactionDataStore.transfer(debit, credit, debitSummary)
                .map(result -> {
                    if (result > 0) {
                        return new HandlerResponse(HttpResponseStatus.NO_CONTENT, null);
                    }
                    return new HandlerResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
                });
    }

    private Transaction buildTransaction(String username, String counterParty, Double amount, String side, Date createDate, String refId) {
        Transaction transaction = new Transaction();

        transaction.setUsername(username);
        transaction.setSide(side);
        transaction.setCreateTs(createDate);
        transaction.setTransactionType(TYPE_TRANSFER);
        transaction.setAmount(amount);
        transaction.setCounterParty(counterParty);
        transaction.setTransactionId(generateId());
        transaction.setRefId(refId);
        transaction.setCreateDate(DateTimeHelper.getDateOnly(createDate));

        return transaction;
    }

    private TransactionDebitSummary buildTrxDebitSummary(String username, Double amount, Date date) {
        TransactionDebitSummary summary = new TransactionDebitSummary();
        summary.setUsername(username);
        summary.setAmount(amount);
        summary.setPeriodDate(DateTimeHelper.getDateOnly(date));
        summary.setPeriodMonth(DateTimeHelper.getMonthYear(date));

        return summary;
    }

    private String generateId() {
        return RandomStringUtils.randomAlphanumeric(30);
    }
}
