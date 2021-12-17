package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.response.HandlerResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

/**
 * Created by wawan on 17/12/21.
 */
public class GetTopTransactionController implements Handler{
    private Datastore datastore;
    private Gson gson;

    public GetTopTransactionController(Datastore datastore, Gson gson) {
        this.datastore = datastore;
        this.gson = gson;
    }

    @Override
    public Observable<HandlerResponse> handle(RoutingContext routingContext, UserToken userToken) {
        return datastore.getTopTransaction(userToken.getUser())
                .map(transactions -> {
                    return new HandlerResponse(HttpResponseStatus.OK, gson.toJson(transactions));
                });
    }
}
