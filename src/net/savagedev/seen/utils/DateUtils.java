package net.savagedev.seen.utils;

import java.util.Date;

public class DateUtils {
    public String formatPlayTime(long playTime, TimeLengthFormat timeFormat) {
        long[] times = new long[6];

        times[0] = playTime / 20 % 60; // Seconds
        times[1] = playTime / (20 * 60) % 60; // Minutes
        times[2] = playTime / (20 * 3600) % 24; // Hours
        times[3] = playTime / (20 * 86400) % 30; // Days
        times[4] = playTime / (20 * 86400 * 30) % 12; // Months
        times[5] = playTime / (20 * 86400 * 365); // Years

        return this.formatTimes(times, timeFormat);
    }

    public String formatDateDiff(Date dateFrom, Date dateTo, TimeLengthFormat timeFormat) {
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        if (from == to) {
            return "now";
        }

        boolean future = dateTo.after(dateFrom);

        long difference = future ? to - from : from - to;
        long[] times = new long[6];

        times[0] = difference / 1000 % 60; // Seconds
        times[1] = difference / (1000 * 60) % 60; // Minutes
        times[2] = difference / (1000 * 3600) % 24; // Hours
        times[3] = difference / (1000 * 86400) % 30; // Days
        times[4] = difference / (1000 * 86400 * 30L) % 12; // Months
        times[5] = difference / (1000 * 86400 * 365L); // Years

        return this.formatTimes(times, timeFormat);
    }

    private String formatTimes(long[] times, TimeLengthFormat format) {
        StringBuilder builder = new StringBuilder();
        String[] names = format.getTimeFormat();

        for (int i = times.length - 1; i >= 0; i--) {
            long time = times[i];

            if (time <= 0) {
                continue;
            }

            String name = names[i];

            if (time > 1 && format == TimeLengthFormat.LONG) {
                name = name + "s";
            }

            builder.append(" ").append(time).append(format == TimeLengthFormat.LONG ? " " : "").append(name);
        }

        return builder.toString().trim();
    }

    public enum TimeLengthFormat {
        LONG("second", "minute", "hour", "day", "month", "year"),
        SHORT("s", "m", "h", "d", "m", "y");

        private String[] timeFormat;

        TimeLengthFormat(String... timeFormat) {
            this.timeFormat = timeFormat;
        }

        public String[] getTimeFormat() {
            return this.timeFormat;
        }
    }
}
