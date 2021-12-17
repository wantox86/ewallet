package id.wantox86.ewallet.database;

import com.github.davidmoten.rx.jdbc.Database;
import id.wantox86.ewallet.database.mapper.TopTransactionMapper;
import id.wantox86.ewallet.database.mapper.TopUserMapper;
import id.wantox86.ewallet.database.mapper.UserTokenMapper;
import id.wantox86.ewallet.model.data.Transaction;
import id.wantox86.ewallet.model.data.TransactionDebitSummary;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.response.GetTopTransactionResponse;
import id.wantox86.ewallet.model.response.GetTopUserResponse;
import static id.wantox86.ewallet.Constants.*;
import id.wantox86.ewallet.util.DateTimeHelper;
import rx.Observable;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by wawan on 17/12/21.
 */
public class DatastoreImpl implements Datastore {
    private Database database;

    private UserTokenMapper userTokenMapper = new UserTokenMapper();
    private TopTransactionMapper topTransactionMapper = new TopTransactionMapper();
    private TopUserMapper topUserMapper = new TopUserMapper();

    public DatastoreImpl(Database database) {
        this.database = database;
    }

    @Override
    public Observable<Boolean> beginTransaction() {
        return database.beginTransaction();
    }

    @Override
    public Observable<Boolean> commitTransaction(Observable<?> depends) {
        return database.commit(depends);
    }

    @Override
    public Observable<Boolean> rollbackTransaction() {
        return database.rollback();
    }

    @Override
    public Observable<String> getToken(String username) {
        return database.select("SELECT token FROM users where username = ?")
                .parameter(username)
                .get(rs -> {
                    return rs.getString("token");
                })
                .defaultIfEmpty(null);
    }

    @Override
    public Observable<UserToken> getUserToken(String token) {
        return database.select("SELECT username, token FROM users where token = ?")
                .parameter(token)
                .get(userTokenMapper)
                .defaultIfEmpty(null);
    }

    @Override
    public Observable<Integer> insertUser(Observable<?> depends, String username, String token) {
        return database
                .update("insert into users (username,token) values (:username, :token)")
                .dependsOn(depends)
                .parameter("username", username)
                .parameter("token", token)
                .count();
    }

    @Override
    public Observable<Integer> insertUserBalance(Observable<?> depends, String username, Double amount) {
        return database.update("insert into user_balances (username, balance) values (:username, :amount)")
                .dependsOn(depends)
                .parameter("username", username)
                .parameter("amount", amount)
                .count();
    }

    @Override
    public Observable<Double> getUserBalance(String username) {
        return database.select("SELECT balance FROM user_balances where username = :username")
                .parameter("username", username)
                .get(rs -> {
                    return rs.getDouble("balance");
                })
                .defaultIfEmpty(0.0);
    }

    @Override
    public Observable<Integer> updateBalance(Observable<?> depends, String username, Double amount) {
        return database.update("update user_balances set balance = balance + :amount where username = :username")
                .dependsOn(depends)
                .parameter("amount", amount)
                .parameter("username", username)
                .count();
    }

    @Override
    public Observable<Integer> insertTransaction(Observable<?> depends, Transaction transaction) {
        return database.update("insert into transactions (username, transaction_type, transaction_id, ref_id, counter_party, create_ts, create_date, side, amount) " +
                "values (:username, :trxType, :trxId, :refId, :counterParty, :createTs, :createDate, :side, :amount);")
                .dependsOn(depends)
                .parameter("username", transaction.getUsername())
                .parameter("trxType", transaction.getTransactionType())
                .parameter("trxId", transaction.getTransactionId())
                .parameter("refId", transaction.getRefId())
                .parameter("counterParty", transaction.getCounterParty())
                .parameter("createTs", new Timestamp(transaction.getCreateTs().getTime()))
                .parameter("createDate", transaction.getCreateDate())
                .parameter("side", transaction.getSide())
                .parameter("amount", transaction.getAmount())

                .count();
    }

    @Override
    public Observable<Integer> upsertTrxDebitSummary(Observable<?> depends, TransactionDebitSummary transactionDebitSummary) {
        return database.update("insert into transaction_debit_summary(username, period_date, period_month, amount) " +
                "values (:username, :periodDate, :periodMonth, :amount) " +
                "on duplicate key update amount = amount + :amount")
                .dependsOn(depends)
                .parameter("username", transactionDebitSummary.getUsername())
                .parameter("periodDate", transactionDebitSummary.getPeriodDate())
                .parameter("periodMonth", transactionDebitSummary.getPeriodMonth())
                .parameter("amount", transactionDebitSummary.getAmount())
                .count();
    }

    @Override
    public Observable<List<GetTopTransactionResponse>> getTopTransaction(String username) {
        return database.select("select counter_party, side, amount from transactions where username = :username order by create_ts desc limit 10;")
                .parameter("username", username)
                .get(topTransactionMapper)
                .toList()
                .defaultIfEmpty(Collections.emptyList());
    }

    @Override
    public Observable<List<GetTopUserResponse>> getTopUser(String mode) {
        return database.select(getQueryTopUser(mode))
                .parameter("period", getPeriodTopUser(mode))
                .get(topUserMapper)
                .toList()
                .defaultIfEmpty(Collections.emptyList());
    }

    private String getQueryTopUser(String mode) {
        if (mode.equals(TOP_USER_MODE_ALL)) {
            return "select username, sum(amount) as transacted_value from transaction_debit_summary where :period = :period group by username order by transacted_value desc limit 10;";
        } else if (mode.equals(TOP_USER_MODE_DAILY)) {
            return "select username, sum(amount) as transacted_value from transaction_debit_summary where period_date = :period group by username order by transacted_value desc limit 10;";
        } else {
            return "select username, sum(amount) as transacted_value from transaction_debit_summary where period_month = :period group by username order by transacted_value desc limit 10;";
        }
    }

    private int getPeriodTopUser(String mode) {
        if (mode.equals(TOP_USER_MODE_MONTHLY)) {
            return DateTimeHelper.getMonthYear(new Date());
        }
        return DateTimeHelper.getDateOnly(new Date());
    }
}
