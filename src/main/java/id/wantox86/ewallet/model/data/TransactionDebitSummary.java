package id.wantox86.ewallet.model.data;

/**
 * Created by wawan on 17/12/21.
 */
public class TransactionDebitSummary {
    private String username;
    private int periodDate;
    private int periodMonth;
    private Double amount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(int periodDate) {
        this.periodDate = periodDate;
    }

    public int getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(int periodMonth) {
        this.periodMonth = periodMonth;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
