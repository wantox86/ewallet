package id.wantox86.ewallet.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wawan on 17/12/21.
 */
public class TransferRequest {
    @SerializedName("to_username")
    private String toUsername;
    private Double amount;

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
