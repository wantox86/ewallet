package id.wantox86.ewallet.database;

import id.wantox86.ewallet.model.data.Transaction;
import id.wantox86.ewallet.model.data.TransactionDebitSummary;
import id.wantox86.ewallet.model.data.UserToken;
import id.wantox86.ewallet.model.response.GetTopTransactionResponse;
import id.wantox86.ewallet.model.response.GetTopUserResponse;
import rx.Observable;

import java.util.List;


/**
 * Created by wawan on 17/12/21.
 */
public interface Datastore {
    Observable<Boolean> beginTransaction();
    Observable<Boolean> commitTransaction(Observable<?> depends);
    Observable<Boolean> rollbackTransaction();
    Observable<String> getToken(String username);
    Observable<UserToken> getUserToken(String token);
    Observable<Integer> insertUser(Observable<?> depends, String username, String token);
    Observable<Integer> insertUserBalance(Observable<?> depends, String username, Double amount);
    Observable<Double> getUserBalance(String username);
    Observable<Integer> updateBalance(Observable<?> depends, String username, Double amount);
    Observable<Integer> insertTransaction(Observable<?> depends, Transaction transaction);
    Observable<Integer> upsertTrxDebitSummary(Observable<?> depends, TransactionDebitSummary transactionDebitSummary);
    Observable<List<GetTopTransactionResponse>> getTopTransaction(String username);
    Observable<List<GetTopUserResponse>> getTopUser(String mode);
}
