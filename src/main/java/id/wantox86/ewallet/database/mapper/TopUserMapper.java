package id.wantox86.ewallet.database.mapper;

import com.github.davidmoten.rx.jdbc.ResultSetMapper;
import id.wantox86.ewallet.model.response.GetTopUserResponse;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by wawan on 17/12/21.
 */
public class TopUserMapper implements ResultSetMapper<GetTopUserResponse> {
    @Override
    public GetTopUserResponse call(ResultSet rs) throws SQLException {
        GetTopUserResponse response = new GetTopUserResponse();
        response.setUsername(rs.getString("username"));
        response.setTransactedValue(rs.getDouble("transacted_value"));
        return response;
    }
}

