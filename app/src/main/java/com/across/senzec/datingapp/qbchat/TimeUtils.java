package com.across.senzec.datingapp.qbchat;

/**
 * Created by senzec on 3/8/17.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    private TimeUtils() {
    }

    public static String getTime(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }
    public static String getDate1(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDateTime(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy, hh:mm aa", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static long getDateAsHeaderId(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(milliseconds)));
    }
}
