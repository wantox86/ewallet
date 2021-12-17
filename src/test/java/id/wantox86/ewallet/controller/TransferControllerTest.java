package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.request.TransferRequest;
import id.wantox86.ewallet.model.response.HandlerResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
/**
 * Created by wawan on 18/12/21.
 */
public class TransferControllerTest {

    private Datastore datastore;
    private TransactionDataStore transactionDataStore;
    private RoutingContext routingContext;
    private Gson gson = new Gson();

    private String USER_SENDER = "wawan";
    private String USER_RECEIVER = "setiawan";
    private String TOKEN = "token";

    private TransferController transferController;
    @Before
    public void setUp() throws Exception {
        datastore = mock(Datastore.class);
        transactionDataStore = mock(TransactionDataStore.class);
        routingContext = mock(RoutingContext.class);
        transferController = new TransferController(datastore, transactionDataStore, gson);
    }

    @Test
    public void testInsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setAmount(10000.0);
        request.setToUsername(USER_RECEIVER);

        when(datastore.getUserBalance(USER_SENDER)).thenReturn(Observable.just(0.0));
        when(datastore.getToken(USER_RECEIVER)).thenReturn(Observable.just(TOKEN));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = transferController.handle(routingContext, getUserTokenSender())
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.BAD_REQUEST.code(), handlerResponse.getStatus().code());
    }

    @Test
    public void testSenderAndReceiverEquals() {
        TransferRequest request = new TransferRequest();
        request.setAmount(10000.0);
        request.setToUsername(USER_SENDER);

        when(datastore.getUserBalance(USER_SENDER)).thenReturn(Observable.just(0.0));
        when(datastore.getToken(USER_SENDER)).thenReturn(Observable.just(TOKEN));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = transferController.handle(routingContext, getUserTokenSender())
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.BAD_REQUEST.code(), handlerResponse.getStatus().code());
    }

    @Test
    public void testDestinationUserNotFound() {
        TransferRequest request = new TransferRequest();
        request.setAmount(10000.0);
        request.setToUsername(USER_RECEIVER);

        when(datastore.getUserBalance(USER_SENDER)).thenReturn(Observable.just(20000.0));
        when(datastore.getToken(USER_RECEIVER)).thenReturn(Observable.just(null));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = transferController.handle(routingContext, getUserTokenSender())
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.NOT_FOUND.code(), handlerResponse.getStatus().code());
    }

    @Test
    public void testTransferSuccess() {
        TransferRequest request = new TransferRequest();
        request.setAmount(10000.0);
        request.setToUsername(USER_RECEIVER);

        when(datastore.getUserBalance(USER_SENDER)).thenReturn(Observable.just(20000.0));
        when(datastore.getToken(USER_RECEIVER)).thenReturn(Observable.just(TOKEN));
        when(transactionDataStore.transfer(any(), any(), any())).thenReturn(Observable.just(1));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = transferController.handle(routingContext, getUserTokenSender())
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.NO_CONTENT.code(), handlerResponse.getStatus().code());
    }

    private UserToken getUserTokenSender() {
        UserToken userToken = new UserToken();
        userToken.setUser(USER_SENDER);
        userToken.setToken(TOKEN);

        return userToken;
    }
}
