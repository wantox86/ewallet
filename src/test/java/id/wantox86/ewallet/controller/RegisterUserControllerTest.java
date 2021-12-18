package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.database.TransactionDataStore;
import id.wantox86.ewallet.model.request.RegisterRequest;
import id.wantox86.ewallet.model.response.HandlerResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by wawan on 18/12/21.
 */
public class RegisterUserControllerTest {

    private TransactionDataStore transactionDataStore;
    private Datastore datastore;
    private RoutingContext routingContext;
    private Gson gson = new Gson();

    private String USER = "wawan";
    private String TOKEN = "token";

    private RegisterUserController registerUserController;
    @Before
    public void setUp() throws Exception {
        transactionDataStore = mock(TransactionDataStore.class);
        datastore = mock(Datastore.class);
        routingContext = mock(RoutingContext.class);
        registerUserController = new RegisterUserController(gson, datastore, transactionDataStore);
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(USER);

        when(datastore.getToken(USER)).thenReturn(Observable.just(null));
        when(transactionDataStore.insertUser(anyString(), anyString())).thenReturn(Observable.just(1));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = registerUserController.handle(routingContext, null)
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.OK.code(), handlerResponse.getStatus().code());
    }

    @Test
    public void testRegisterAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(USER);

        when(datastore.getToken(USER)).thenReturn(Observable.just(TOKEN));
        when(routingContext.getBodyAsString()).thenReturn(gson.toJson(request));

        HandlerResponse handlerResponse = registerUserController.handle(routingContext, null)
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.CONFLICT.code(), handlerResponse.getStatus().code());
    }

    @Test
    public void testRegisterBadRequest() {
        when(datastore.getToken(USER)).thenReturn(Observable.just(TOKEN));
        when(routingContext.getBodyAsString()).thenReturn(null);

        HandlerResponse handlerResponse = registerUserController.handle(routingContext, null)
                .toBlocking().firstOrDefault(null);

        assertEquals(HttpResponseStatus.BAD_REQUEST.code(), handlerResponse.getStatus().code());
    }
}
