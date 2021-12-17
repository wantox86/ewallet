package id.wantox86.ewallet.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wawan on 17/12/21.
 */
public class DateTimeHelper {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyyMM");

    public static Integer getDateOnly(Date date){
        return new Integer(dateFormatter.format(date));
    }

    public static Integer getMonthYear(Date date){
        return new Integer(monthFormatter.format(date));
    }
}
