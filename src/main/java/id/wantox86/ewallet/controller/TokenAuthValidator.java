package id.wantox86.ewallet.controller;

import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.model.data.UserToken;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wawan on 17/12/21.
 */
public class TokenAuthValidator {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Map<String, UserToken> userTokenMap = new HashMap<>();
    private Handler handler;
    private Datastore datastore;

    public TokenAuthValidator(Handler handler, Datastore datastore) {
        this.handler = handler;
        this.datastore = datastore;
    }

    public void handle(final RoutingContext routingContext) {
        try {
            String token = routingContext.request().headers().get("Authorization");
            UserToken userToken = getUserToken(token);
            if (userToken != null) {
                handler.handle(routingContext, userToken)
                        .subscribe(handlerResponse -> {
                            routingContext.response()
                                    .putHeader("Content-Type", "application/json")
                                    .setStatusCode(handlerResponse.getStatus().code())
                                    .end(handlerResponse.getPayload() != null ? handlerResponse.getPayload() : "");
                        });
            } else {
                routingContext.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
                        .end();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            routingContext.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .end();
        }
    }

    private UserToken getUserToken(String token) {
        UserToken userTokenInMemory = userTokenMap.get(token);

        if (userTokenInMemory == null) {
            UserToken userTokenInDB = datastore.getUserToken(token).toBlocking().firstOrDefault(null);
            if (userTokenInDB != null) {
                userTokenMap.put(userTokenInDB.getToken(), userTokenInDB);
            }
            return userTokenInDB;
        }
        return userTokenInMemory;
    }
}
