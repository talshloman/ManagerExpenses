package tal.managerexpenses.model;

import android.telephony.SmsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tal on 28/09/2017.
 */

public class OverLimit implements Runnable {

    Setting setting;
    DatabaseHelper db;
    User user;
    SmsManager sms = null;
    int sunDaily;
    int sumWeekly;
    int sumMonthly;
    int firstDayInWeek;
    int firstDayInMonth;
    String todayStr;
    String anotherDayStr;
    SimpleDateFormat dateFormat;
    Calendar calendar;
    Calendar anotherDate;

    public OverLimit() {
    }

    public void initialization(DatabaseHelper db, User user, Setting setting) {
        this.db = db;
        this.user = user;
        this.setting = setting;
    }

    public void run() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        todayStr = dateFormat.format(calendar.getTime());
        sunDaily = db.getSumExpensesBetweenDates(user.getUsername(), todayStr, todayStr);
        if (sunDaily > Integer.parseInt(setting.getLimitDaily())) {
            sendSms(user.getPhone(), "You over the daily limit.");
        }

        anotherDate = Calendar.getInstance();
        firstDayInWeek = calendar.getActualMinimum(Calendar.DAY_OF_WEEK);
        anotherDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), firstDayInWeek, 0, 0);
        anotherDayStr = dateFormat.format(anotherDate.getTime());
        sumWeekly = db.getSumExpensesBetweenDates(user.getUsername(), anotherDayStr, todayStr);
        if (sumWeekly > Integer.parseInt(setting.getLimitWeekly())) {
            sendSms(user.getPhone(), "You over the weekly limit.");
        }

        firstDayInMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        anotherDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), firstDayInMonth, 0, 0);
        anotherDayStr = dateFormat.format(anotherDate.getTime());
        sumMonthly = db.getSumExpensesBetweenDates(user.getUsername(), anotherDayStr, todayStr);
        if (sumMonthly > Integer.parseInt(setting.getLimitMonthly())) {
            sendSms(user.getPhone(), "You over the monthly limit.");
        }
    }

    private void sendSms(String phone, String text) {
        if (sms == null) {
            sms = SmsManager.getDefault();
        }
        sms.sendTextMessage(phone, null, text, null, null);
    }
}
