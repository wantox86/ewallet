package id.wantox86.ewallet.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wawan on 17/12/21.
 */
public class GetTopUserResponse {
    private String username;
    @SerializedName("transacted_value")
    private Double transactedValue;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getTransactedValue() {
        return transactedValue;
    }

    public void setTransactedValue(Double transactedValue) {
        this.transactedValue = transactedValue;
    }
}
