package id.wantox86.ewallet.database.mapper;

import com.github.davidmoten.rx.jdbc.ResultSetMapper;
import id.wantox86.ewallet.model.data.UserToken;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wawan on 17/12/21.
 */
public class UserTokenMapper implements ResultSetMapper<UserToken> {
    @Override
    public UserToken call(ResultSet rs) throws SQLException {
        UserToken userToken = new UserToken();
        userToken.setUser(rs.getString("username"));
        userToken.setToken(rs.getString("token"));

        return userToken;
    }
}
