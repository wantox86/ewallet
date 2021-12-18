package id.wantox86.ewallet.controller;

import com.google.gson.Gson;
import id.wantox86.ewallet.controller.auth.Handler;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.response.HandlerResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.ext.web.RoutingContext;
import static id.wantox86.ewallet.Constants.*;
import rx.Observable;

/**
 * Created by wawan on 17/12/21.
 */
public class GetTopUserController implements Handler {
    private Datastore datastore;
    private Gson gson;

    public GetTopUserController(Datastore datastore, Gson gson) {
        this.datastore = datastore;
        this.gson = gson;
    }

    @Override
    public Observable<HandlerResponse> handle(RoutingContext routingContext, UserToken userToken) {
        return datastore.getTopUser(getMode(routingContext))
                .map(transactions -> {
                    return new HandlerResponse(HttpResponseStatus.OK, gson.toJson(transactions));
                });
    }

    private String getMode(RoutingContext routingContext) {
        if (routingContext.request().getParam(PARAM_MODE) != null
                && (routingContext.request().getParam(PARAM_MODE).equals(TOP_USER_MODE_DAILY)
                || routingContext.request().getParam(PARAM_MODE).equals(TOP_USER_MODE_MONTHLY)
                || routingContext.request().getParam(PARAM_MODE).equals(TOP_USER_MODE_ALL))) {
            return routingContext.request().getParam(PARAM_MODE);
        }
        return TOP_USER_MODE_ALL;
    }
}
