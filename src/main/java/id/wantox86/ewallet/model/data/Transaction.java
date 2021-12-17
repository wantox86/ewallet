package id.wantox86.ewallet.model.data;

import java.util.Date;

/**
 * Created by wawan on 17/12/21.
 */
public class Transaction {
    private String username;
    private String transactionType;
    private String transactionId;
    private String refId;
    private String counterParty;
    private Date createTs;
    private int createDate;
    private String side;
    private Double amount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public void setCounterParty(String counterParty) {
        this.counterParty = counterParty;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public int getCreateDate() {
        return createDate;
    }

    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
