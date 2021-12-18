package id.wantox86.ewallet;

import com.github.davidmoten.rx.jdbc.Database;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import id.wantox86.ewallet.controller.*;
import id.wantox86.ewallet.controller.auth.NoAuthController;
import id.wantox86.ewallet.controller.auth.TokenAuthController;
import id.wantox86.ewallet.database.Datastore;
import id.wantox86.ewallet.database.DatastoreImpl;
import id.wantox86.ewallet.database.TransactionDataStore;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;

/**
 * Created by wawan on 16/12/21.
 */
public class Server extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(Server.class.getName());

    @Override
    public void start() throws Exception {
        vertx.createHttpServer()
                .requestHandler(createRouter())
                .listen(8888);
    }

    private Router createRouter() {
        Router router = Router.router(vertx);
        Gson gson = new Gson();
        Database database = createDatabase();
        Datastore datastore = new DatastoreImpl(database);
        TransactionDataStore transactionDataStore = new TransactionDataStore(datastore);

        router.route().handler(BodyHandler.create());

        router.post("/api/v1/ewallet/user/register")
                .handler(new NoAuthController(new RegisterUserController(gson, datastore, transactionDataStore))::handle);

        router.get("/api/v1/ewallet/user/balance")
                .handler(new TokenAuthController(new GetBalanceController(datastore, gson), datastore)::handle);

        router.post("/api/v1/ewallet/user/topup")
                .handler(new TokenAuthController(new TopupBalanceController(transactionDataStore, gson), datastore)::handle);

        router.post("/api/v1/ewallet/transfer")
                .handler(new TokenAuthController(new TransferController(datastore, transactionDataStore, gson), datastore)::handle);

        router.get("/api/v1/ewallet/report/user_transaction")
                .handler(new TokenAuthController(new GetTopTransactionController(datastore, gson), datastore)::handle);

        router.get("/api/v1/ewallet/report/top_user")
                .handler(new TokenAuthController(new GetTopUserController(datastore, gson), datastore)::handle);

        return router;
    }

    public Database createDatabase() {

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/ewallet?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("");
        hikariConfig.setMaximumPoolSize(30);

        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        return Database.fromDataSource(hikariDataSource);
    }

}
