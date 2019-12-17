package com.jgendeavors.roomnotes.util;

import android.content.Context;

import com.jgendeavors.roomnotes.R;

import java.util.Calendar;
import java.util.Locale;

public class Util {

    /**
     * Displays the given time (millis) as a String via format String resources and java.util.Calendar.
     *
     * The style parameter indicates the style used by Calendar.getDisplayName() and should be
     * either Calendar.LONG (to get e.g. "December") or Calendar.SHORT (to get e.g. "Dec").
     *
     * @param context
     * @param millis
     * @param style either Calendar.LONG or Calendar.SHORT
     * @return
     */
    public static String getTimeAsString(Context context, long millis, int style) {
        final long millisPerDay = 1000 * 60 * 60 * 24;
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.setTimeInMillis(millis);

        String result = "";
        if (currentTime - millis < millisPerDay) {
            // millis represents a time within the last 24 hours, return a time like "8:43 PM"
            int hour = calendar.get(Calendar.HOUR);
            if (hour == 0) hour = 12;
            int minute = calendar.get(Calendar.MINUTE);
            String ampm = calendar.getDisplayName(Calendar.AM_PM, style, Locale.getDefault());

            result = context.getString(R.string.timestamp_hour_format, hour, minute, ampm);
        } else {
            // millis represents a time longer than 24 hours ago, return a date like "December 13, 2019"
            String month = calendar.getDisplayName(Calendar.MONTH, style, Locale.getDefault());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            result = context.getString(R.string.timestamp_day_format, month, day, year);
        }
        return result;
    }
}
