package id.wantox86.ewallet.controller.auth;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * Created by wawan on 18/12/21.
 */
public class NoAuthController {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Handler handler;

    public NoAuthController(Handler handler) {
        this.handler = handler;
    }

    public void handle(final RoutingContext routingContext) {
        try {
            handler.handle(routingContext, null)
                    .subscribe(handlerResponse -> {
                        routingContext.response()
                                .putHeader("Content-Type", "application/json")
                                .setStatusCode(handlerResponse.getStatus().code())
                                .end(handlerResponse.getPayload() != null ? handlerResponse.getPayload() : "");
                    });
        } catch (Exception e) {
            logger.error("Exception", e);
            routingContext.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .end();
        }
    }
}
