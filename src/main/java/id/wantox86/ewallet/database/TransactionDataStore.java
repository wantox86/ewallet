package id.wantox86.ewallet.database;


import id.wantox86.ewallet.model.data.Transaction;
import id.wantox86.ewallet.model.data.TransactionDebitSummary;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import rx.Observable;

/**
 * Created by wawan on 17/12/21.
 */
public class TransactionDataStore {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Datastore datastore;

    public TransactionDataStore(Datastore datastore) {
        this.datastore = datastore;
    }

    public Observable<Integer> insertUser(String username, String token) {
        try {
            Observable<Boolean> begin = datastore.beginTransaction();
            Observable<Integer> insertUser = datastore.insertUser(begin, username, token);
            Observable<Integer> insertBalance = datastore.insertUserBalance(insertUser, username, 0.0);
            datastore.commitTransaction(insertBalance)
                    .doOnError( throwable -> {
                        datastore.rollbackTransaction();
                    })
                    .toBlocking().first();
            return Observable.just(1);
        } catch (Exception e) {
            logger.error("insert user failed", e);
            return Observable.just(0);
        }
    }

    public Observable<Integer> topupBalance(Transaction transaction) {
        try {
            Observable<Boolean> begin = datastore.beginTransaction();
            Observable<Integer> updateBalance = datastore.updateBalance(begin, transaction.getUsername(), transaction.getAmount());
            Observable<Integer> insertTrx = datastore.insertTransaction(updateBalance, transaction);
            datastore.commitTransaction(insertTrx)
                    .doOnError( throwable -> {
                        datastore.rollbackTransaction();
                    })
                    .toBlocking().first();
            return Observable.just(1);
        } catch (Exception e) {
            logger.error("topup user failed", e);
            return Observable.just(0);
        }
    }

    public Observable<Integer> transfer(Transaction debit, Transaction credit, TransactionDebitSummary debitSummary) {
        try {
            Observable<Boolean> begin = datastore.beginTransaction();
            Observable<Integer> debitBalance = datastore.updateBalance(begin, debit.getUsername(), debit.getAmount() * -1);
            Observable<Integer> creditBalance = datastore.updateBalance(debitBalance, credit.getUsername(), debit.getAmount());
            Observable<Integer> insertDebit = datastore.insertTransaction(creditBalance, debit);
            Observable<Integer> insertCredit = datastore.insertTransaction(insertDebit, credit);
            Observable<Integer> upsertTrxDebitSummary = datastore.upsertTrxDebitSummary(insertCredit, debitSummary);
            datastore.commitTransaction(upsertTrxDebitSummary)
                    .doOnError( throwable -> {
                        datastore.rollbackTransaction();
                    })
                    .toBlocking().first();
            return Observable.just(1);
        } catch (Exception e) {
            logger.error("transfer user failed", e);
            return Observable.just(0);
        }
    }
}
