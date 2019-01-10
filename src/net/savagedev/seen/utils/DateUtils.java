package net.savagedev.seen.utils;

import java.util.Calendar;

public class DateUtils {
    private int preferredAccuracy;

    public DateUtils(int preferredAccuracy) {
        this.preferredAccuracy = preferredAccuracy;
    }

    public void setPreferredAccuracy(int accuracy) {
        this.preferredAccuracy = accuracy;
    }

    public String formatPlayTime(long playTime) {
        String[] names = new String[]{"second", "minute", "hour", "day", "month", "year"};
        long[] times = new long[6];

        times[0] = playTime / 20 % 60; // Seconds
        times[1] = playTime / (20 * 60) % 60; // Minutes
        times[2] = playTime / (20 * 3600) % 24; // Hours
        times[3] = playTime / (20 * 86400) % 30; // Days
        times[4] = playTime / (20 * 86400 * 30) % 12; // Months
        times[5] = playTime / (20 * 86400 * 365); // Years

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = times.length - 1; i >= 0; i--) {
            long time = times[i];

            if (time <= 0)
                continue;

            String name = names[i];
            if (time > 1)
                name = name + "s";

            stringBuilder.append(" ").append(time).append(" ").append(name);
        }

        return stringBuilder.toString().trim();
    }

    public String formatPlayTimePT(long playTime) {
        String[] names = new String[]{"s", "m", "h", "d", "m", "y"};
        long[] times = new long[6];

        times[0] = playTime / 20 % 60; // Seconds
        times[1] = playTime / (20 * 60) % 60; // Minutes
        times[2] = playTime / (20 * 3600) % 24; // Hours
        times[3] = playTime / (20 * 86400) % 30; // Days
        times[4] = playTime / (20 * 86400 * 30) % 12; // Months
        times[5] = playTime / (20 * 86400 * 365); // Years

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = times.length - 1; i >= 0; i--) {
            long time = times[i];

            if (time <= 0)
                continue;

            stringBuilder.append(" ").append(time).append(names[i]);
        }

        return stringBuilder.toString().trim();
    }

    public String formatDateDiff(Calendar dateFrom, Calendar dateTo) {
        boolean future = false;
        if (dateTo.equals(dateFrom))
            return "now";

        if (dateTo.after(dateFrom))
            future = true;

        StringBuilder sb = new StringBuilder();

        int[] types = new int[]{1, 2, 5, 11, 12, 13};
        String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
        int accuracy = 0;

        for (int i = 0; i < types.length && accuracy <= (this.preferredAccuracy > names.length ? names.length : this.preferredAccuracy); ++i) {
            int diff = 0;

            long savedDate;
            for (savedDate = dateFrom.getTimeInMillis(); future && !dateFrom.after(dateTo) || !future && !dateFrom.before(dateTo); ++diff) {
                savedDate = dateFrom.getTimeInMillis();
                dateFrom.add(types[i], future ? 1 : -1);
            }

            --diff;
            dateFrom.setTimeInMillis(savedDate);

            if (diff > 0) {
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
                ++accuracy;
            }
        }

        return sb.length() == 0 ? "now" : sb.toString().trim();
    }
}
