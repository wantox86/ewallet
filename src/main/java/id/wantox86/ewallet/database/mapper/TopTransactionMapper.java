package id.wantox86.ewallet.database.mapper;

import com.github.davidmoten.rx.jdbc.ResultSetMapper;
import id.wantox86.ewallet.model.response.GetTopTransactionResponse;
import static id.wantox86.ewallet.Constants.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wawan on 17/12/21.
 */
public class TopTransactionMapper implements ResultSetMapper<GetTopTransactionResponse> {
    @Override
    public GetTopTransactionResponse call(ResultSet rs) throws SQLException {
        GetTopTransactionResponse response = new GetTopTransactionResponse();
        response.setUsername(rs.getString("counter_party"));
        String side = rs.getString("side");
        if (side.equals(SIDE_DEBIT)) {
            response.setAmount(rs.getDouble("amount") * -1);
        } else {
            response.setAmount(rs.getDouble("amount"));
        }

        return response;
    }
}
