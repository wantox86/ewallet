package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.request.TopupBalanceRequest;
import id.wantox86.ewallet.model.response.HandlerResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by wawan on 18/12/21.
 */
public class TopupBalanceControllerTest {
    private TransactionDataStore transactionDataStore;
    private RoutingContext routingContext;
    private Gson gson = new Gson();

    private String USER = "wawan";
    private String TOKEN = "token";

    private TopupBalanceController topupBalanceController;
    @Before
    public void setUp() throws Exception {
        transactionDataStore = mock(TransactionDataStore.class);
        routingContext = mock(RoutingContext.class);
        topupBalanceController = new TopupBalanceController(transactionDataStore, gson);
    }

    @Test
    public void testInvalidTopupAmount() {
        TopupBalanceRequest request = new TopupBalanceRequest();
        request.setAmount(10000000.0);

        when(transactionDataStore.topupBalance(any())).thenReturn(Observable.just(1));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = topupBalanceController.handle(routingContext, getUserTokenSender())
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.BAD_REQUEST.code(), handlerResponse.getStatus().code());
    }

    @Test
    public void testTransferSuccess() {
        TopupBalanceRequest request = new TopupBalanceRequest();
        request.setAmount(100000.0);

        when(transactionDataStore.topupBalance(any())).thenReturn(Observable.just(1));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = topupBalanceController.handle(routingContext, getUserTokenSender())
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.NO_CONTENT.code(), handlerResponse.getStatus().code());
    }

    private UserToken getUserTokenSender() {
        UserToken userToken = new UserToken();
        userToken.setUser(USER);
        userToken.setToken(TOKEN);

        return userToken;
    }
}
