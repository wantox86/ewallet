package id.wantox86.ewallet.model.response;

/**
 * Created by wawan on 17/12/21.
 */
public class GetTopTransactionResponse {
    private String username;
    private Double amount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
