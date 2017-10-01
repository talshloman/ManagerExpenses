package tal.managerexpenses.model;

import android.telephony.SmsManager;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ActionsApp {
    private DatabaseHelper db;
    private User user = null;
    private boolean existUser = true;
    private SmsManager sms = null;
    private OverLimit checkOverLimit = null;
    private Setting setting = null;
    private JSONArray listConstantItemsInJsonArray = null;
    private JSONArray listItemsInJsonArray = null;
    private String shekel = "₪";

    public ActionsApp(DatabaseHelper database) {
        this.db = database;
    }

    /**
     * When registering to an application, the function checks whether the username that the user has entered is exist or not.
     *
     * @param username
     * @return
     */
    @JavascriptInterface
    public boolean checkUsernameInDB(String username) {
        existUser = db.getInfoIfUsernameExist(username);
        return existUser;
    }

    @JavascriptInterface
    public String getCoin() {
        return shekel;
    }

    /**
     * Function that check if username exist in db, update code for reset password and send the code to user.
     *
     * @param username
     * @return
     */
    @JavascriptInterface
    public boolean sendCodeToUser(String username) {
        int code;
        String codeStr = null;
        Random rand;
        boolean result = false;
        user = db.getUserByOnlyUsername(username);
        if (user != null) {
            rand = new Random();
            code = rand.nextInt(10000);
            codeStr = String.valueOf(code);
            user.setCode(codeStr);
            result = db.updateUser(user);
            if (result == true) {
                if (sms == null) {
                    sms = SmsManager.getDefault();
                }
                sms.sendTextMessage(user.getPhone(), null, "Your code is: " + code, null, null);
            }
        }

        return result;
    }

    public boolean savePassword(String newPassword, String code) {
        boolean result = false;

        if (user.getCode().equals(code)) {
            user.setPassword(newPassword);
            result = db.updateUser(user);
        }

        return result;
    }

    /**
     * Check if the username and password exist in DB and if exist take the setting user from DB and connect to the app.
     *
     * @param username
     * @param password
     * @return
     */
    @JavascriptInterface
    public boolean login(String username, String password) {
        boolean result = false;
        user = db.getUser(username, password);
        List<ConstantItem> constantItems;
        Item item = null;
        if (user != null) {
            if (user.getFirstTime() == "0") {
                setting = db.getSetting(user.getUsername());
            }

            if(user.getUpdateConstanItemsThisMonth() == 0)
            {
                if (isFirstDayInMonth() == true) {
                    constantItems = db.getListOfAllConstantItems(user.getUsername());
                    if(constantItems != null)
                    {
                        for(ConstantItem oneItem : constantItems)
                        {
                            item = new Item();
                            item.setCategory(oneItem.getCategory());
                            item.setUsername(oneItem.getUsername());
                            item.setDescription(oneItem.getDescription());
                            item.setExpenseAmount(oneItem.getExpenseAmount());
                            item.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            db.insertExpense(item);
                        }
                    }

                    user.setUpdateConstanItemsThisMonth(1);
                    db.updateUser(user);
                } else {
                    if (isLastDayInMonth() == true) {
                        user.setUpdateConstanItemsThisMonth(0);
                        db.updateUser(user);
                    }
                }
            }

            result = true;
        }


        return result;
    }

    private boolean isFirstDayInMonth() {
        boolean result = false;
        Calendar calendar = Calendar.getInstance();
        int currentday = calendar.get(Calendar.DAY_OF_MONTH);
        if (currentday == 29) {
            result = true;
        }

        return result;
    }

    private boolean isLastDayInMonth() {
        boolean result = false;
        Calendar cal = Calendar.getInstance();
        int currentday = cal.get(Calendar.DAY_OF_MONTH);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (lastDay == currentday) {
            result = true;
        }

        return result;
    }

    /**
     * The function check if the user login to the app in the first time.
     *
     * @return
     */
    @JavascriptInterface
    public boolean isFirstTimeLogin() {
        boolean result = false;
        if (user != null) {
            if (user.getFirstTime().equals("1")) {
                user.setFirstTime("0");
                db.updateUser(user);
                result = true;
            }
        }

        return result;
    }

    /**
     * Adds the user who will sign up for the app.
     *
     * @param username
     * @param firstName
     * @param lastName
     * @param password
     * @param phone
     * @return
     */
    @JavascriptInterface
    public boolean addUser(String username, String firstName, String lastName, String password, String phone) {
        boolean result = false;

        if (existUser == false) {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setPhone(phone);
            user.setPassword(password);
            user.setFirstTime("1");
            user.setCode("");
            user.setUpdateConstanItemsThisMonth(0);
            result = db.insertUser(user);
        }

        return result;
    }

    /**
     * get all expenses of this month.
     *
     * @return
     */
    @JavascriptInterface
    public String getAllExpensesInCurrentMonth() {
        SimpleDateFormat dateFormat;
        Calendar calendar;
        Calendar monthDate;
        String todayStr;
        int firstDayInMonth;
        String monthDateStr;
        String result = null;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        todayStr = dateFormat.format(calendar.getTime());
        firstDayInMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        monthDate = Calendar.getInstance();
        monthDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), firstDayInMonth, 0, 0);
        monthDateStr = dateFormat.format(monthDate.getTime());
        if (user != null) {
            listItemsInJsonArray = db.findExpensesBetweenDates(user.getUsername(), monthDateStr, todayStr);
            if (listItemsInJsonArray != null) {
                result = listItemsInJsonArray.toString();
            }
        }

        return result;
    }

    /**
     * Deletes the item from the DB by ID.
     *
     * @param id
     */
    @JavascriptInterface
    public void deleteItemFromItems(String id) {
        db.deleteExpense(Integer.parseInt(id));
        //listItemsInJsonArray = db.getAllExpensesFromUser(user.getUsername());
    }

    /**
     * Function that check the inputs and update the item in DB.
     *
     * @param id
     * @param description
     * @param sumAmount
     * @param category
     * @param date
     * @return
     */
    @JavascriptInterface
    public boolean updateItem(String id, String description, String sumAmount, String category, String date) {
        boolean result = false;
        Item item;
        if (user != null) {
            item = new Item();
            item.setUsername(user.getUsername());
            item.setCategory(category);
            item.setDate(date);
            item.setDescription(description);
            item.setExpenseAmount(Integer.parseInt(sumAmount));
            item.setId(Integer.parseInt(id));
            result = db.updateItem(item);
            //listItemsInJsonArray = db.getAllExpensesFromUser(user.getUsername());
            if (checkIfDateIsInCurrentMonth(date) == true) {
                if (setting == null) {
                    setting = db.getSetting(user.getUsername());
                }
                if (setting != null) {
                    if (checkOverLimit == null) {
                        checkOverLimit = new OverLimit();
                    }
                    checkOverLimit.initialization(db, user, setting);
                    Thread t = new Thread(checkOverLimit);
                    t.start();
                }


            }
        }
        return result;
    }

    /**
     * Check the input date from the item that user want to add or update if the date is the current month.
     *
     * @param dateStr
     * @return
     */
    private boolean checkIfDateIsInCurrentMonth(String dateStr) {
        boolean result = false;
        Calendar cal;
        Calendar checkCal;
        SimpleDateFormat dateFormat;

        try {
            cal = Calendar.getInstance();
            checkCal = Calendar.getInstance();
            cal.setTime(new Date());
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            checkCal.setTime(dateFormat.parse(dateStr));
            if (cal.get(Calendar.YEAR) == checkCal.get(Calendar.YEAR)) {
                if (cal.get(Calendar.MONTH) == checkCal.get(Calendar.MONTH)) {
                    result = true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * If the user connected, getting all items from the DB.
     *
     * @return
     */
    @JavascriptInterface
    public String getAllExpenses() {
        String result = null;
        if (user != null) {
            listItemsInJsonArray = db.getAllExpensesFromUser(user.getUsername());
        }

        if (listItemsInJsonArray != null) {
            result = listItemsInJsonArray.toString();
        }
        return result;
    }

    /**
     * Get setting of the user that connected from the DB.
     *
     * @return
     * @throws JSONException
     */
    @JavascriptInterface
    public String getSettingOfUser() {
        JSONObject rowObject = null;
        String resultSetting = null;
        if (setting == null && user != null) {
            setting = db.getSetting(user.getUsername());
        }

        if (setting != null) {
            rowObject = new JSONObject();
            try {
                rowObject.put("limitDaily", setting.getLimitDaily());
                rowObject.put("limitWeekly", setting.getLimitWeekly());
                rowObject.put("limitMonthly", setting.getLimitMonthly());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (rowObject != null) {
            resultSetting = rowObject.toString();
        }

        return resultSetting;
    }

    /**
     * Disconnecting from app.
     */
    @JavascriptInterface
    public void logout() {
        user = null;
        db = null;
        setting = null;
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    /**
     * Function that check the inputs and add the item of expense to DB.
     *
     * @param description
     * @param sumAmount
     * @param category
     * @param date
     * @return
     */
    @JavascriptInterface
    public String addExpenseToDb(String description, String sumAmount, String category, String date) {
        Item item = null;
        String result = null;
        if (user != null) {
            item = new Item();
            item.setUsername(user.getUsername());
            item.setCategory(category);
            item.setDate(date);
            item.setDescription(description);
            item.setExpenseAmount(Integer.parseInt(sumAmount));
            if (db.insertExpense(item) == true) {
                try {
                    if (checkIfDateIsInCurrentMonth(date) == true) {
                        listItemsInJsonArray = db.getLastItem(user.getUsername());
                        result = listItemsInJsonArray.getJSONObject(0).toString();
                        if (setting == null) {
                            setting = db.getSetting(user.getUsername());
                        }
                        if (setting != null) {
                            if (checkOverLimit == null) {
                                checkOverLimit = new OverLimit();
                            }
                            checkOverLimit.initialization(db, user, setting);
                            Thread t = new Thread(checkOverLimit);
                            t.start();
                        }
                    } else {
                        result = "moveOn";
                    }
                } catch (JSONException e) {
                    result = null;
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * Saves user settings.
     *
     * @param daily
     * @param weekly
     * @param monthly
     * @return
     */
    @JavascriptInterface
    public boolean saveSettingOfUser(String daily, String weekly, String monthly) {
        boolean result = false;

        if (user != null) {
            result = db.insertSetting(user.getUsername(), daily, weekly, monthly);
            if (result == true) {
                if (setting == null) {
                    setting = new Setting();
                }

                setting.setLimitDail(daily);
                setting.setLimitWeekly(weekly);
                setting.setLimitMonthly(monthly);
            }
        }

        return result;
    }

    /**
     * Get the expenses each month for 6 months.
     *
     * @param monthArray
     * @return
     */
    @JavascriptInterface
    public String getValuesMonths(String monthArray) {
        JSONArray jsonArray;
        JSONArray resultArray = null;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        int firstDay;
        int lastDay;
        Calendar firstDate;
        Calendar lastDate;
        String firstDateStr;
        String lastDateStr;
        if (user != null) {
            try {
                resultArray = new JSONArray();
                jsonArray = new JSONArray(monthArray);
                calendar = Calendar.getInstance();
                firstDate = Calendar.getInstance();
                lastDate = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                for (int i = 0; i < jsonArray.length(); i++) {
                    calendar.setTime(dateFormat.parse(jsonArray.getString(i)));
                    firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    firstDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), firstDay, 0, 0);
                    lastDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), lastDay, 0, 0);
                    firstDateStr = dateFormat.format(firstDate.getTime());
                    lastDateStr = dateFormat.format(lastDate.getTime());
                    resultArray.put(i, db.getSumExpensesBetweenDates(user.getUsername(), firstDateStr, lastDateStr));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return resultArray.toString();
    }

    /**
     * Get sum of all expenses by the option category.
     *
     * @return
     */
    @JavascriptInterface
    public String getValuesExpensesOfOptionChoice() {
        int i = 0;
        SimpleDateFormat dateFormat;
        Calendar calendar;
        Calendar monthDate;
        String todayStr;
        int firstDayInMonth;
        String monthDateStr;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        todayStr = dateFormat.format(calendar.getTime());
        firstDayInMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        monthDate = Calendar.getInstance();
        monthDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), firstDayInMonth, 0, 0);
        monthDateStr = dateFormat.format(monthDate.getTime());

        String[] allOptions = {"food", "home", "pleasures", "shopping", "another"};
        JSONArray resultArray = null;
        resultArray = new JSONArray();
        for (String option : allOptions) {
            try {
                resultArray.put(i, db.getSumExpensesByOption(user.getUsername(), option, monthDateStr, todayStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }

        return resultArray.toString();
    }

    /**
     * get all expenses of this month.
     *
     * @return
     */
    @JavascriptInterface
    public String getAllConstantExpenses() {
        String result = null;
        if (user != null) {
            listConstantItemsInJsonArray = db.getAllConstansExpensesFromUser(user.getUsername());
        }

        if (listConstantItemsInJsonArray != null) {
            result = listConstantItemsInJsonArray.toString();
        }
        return result;
    }

    /**
     * Deletes the item from the DB by ID.
     *
     * @param id
     */
    @JavascriptInterface
    public void deleteItemFromConstansItems(String id) {
        db.deleteConstansExpense(Integer.parseInt(id));
    }

    /**
     * Function that check the inputs and add the item of expense to DB.
     *
     * @param description
     * @param sumAmount
     * @param category
     * @return
     */
    @JavascriptInterface
    public String addConstantExpenseToDb(String description, String sumAmount, String category) {
        ConstantItem item = null;
        String result = null;
        if (user != null) {
            item = new ConstantItem();
            item.setUsername(user.getUsername());
            item.setCategory(category);
            item.setDescription(description);
            item.setExpenseAmount(Integer.parseInt(sumAmount));
            if (db.insertConstantExpense(item) == true) {
                try {
                    listConstantItemsInJsonArray = db.getLastConstantItem(user.getUsername());
                    result = listConstantItemsInJsonArray.getJSONObject(0).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
