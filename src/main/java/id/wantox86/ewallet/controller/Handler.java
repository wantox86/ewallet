package id.wantox86.ewallet.controller;

import id.wantox86.ewallet.model.response.HandlerResponse;
import id.wantox86.ewallet.model.data.UserToken;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

/**
 * Created by wawan on 17/12/21.
 */
public interface Handler {
    Observable<HandlerResponse> handle(RoutingContext routingContext, UserToken userToken);
}
