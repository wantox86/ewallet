package id.wantox86.ewallet.model.response;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Created by wawan on 17/12/21.
 */
public class HandlerResponse {
    private HttpResponseStatus status;
    private String payload;

    public HandlerResponse(HttpResponseStatus status, String payload) {
        this.status = status;
        this.payload = payload;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
