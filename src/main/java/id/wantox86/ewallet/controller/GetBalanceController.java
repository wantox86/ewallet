package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.controller.auth.Handler;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.model.response.GetBalanceResponse;
import id.wantox86.ewallet.model.response.HandlerResponse;
import id.wantox86.ewallet.model.data.UserToken;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

/**
 * Created by wawan on 17/12/21.
 */
public class GetBalanceController implements Handler {
    private Datastore datastore;
    private Gson gson;

    public GetBalanceController(Datastore datastore, Gson gson) {
        this.datastore = datastore;
        this.gson = gson;
    }

    @Override
    public Observable<HandlerResponse> handle(RoutingContext routingContext, UserToken userToken) {
        return datastore.getUserBalance(userToken.getUser())
                .map(balance -> {
                    GetBalanceResponse response = new GetBalanceResponse();
                    response.setBalance(balance);
                    return new HandlerResponse(HttpResponseStatus.OK, gson.toJson(response));
                });
    }
}
