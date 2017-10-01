package tal.managerexpenses.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "managerExpenses.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "Users";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String PHONE = "Phone";
    private static final String CODE = "Code";
    private static final String FIRSTTIME = "FirstTime";
    private static final String UPDATE_CONSTANT = "updateConstanItemsThisMonth";

    private static final String TABLE_CONSTANS_EXPENSES = "ConstansExpenses";

    private static final String TABLE_EXPENSES = "Expenses";
    private static final String DESCRIPTION = "Description";
    private static final String EXPENSE_AMOUNT = "ExpenseAmount";
    private static final String CATEGORY = "Category";
    private static final String DATE = "Date";

    private static final String TABLE_SETTING = "Setting";
    private static final String LIMITDAILY = "LimitDaily";
    private static final String LIMITMONTHLY = "LimitMonthly";
    private static final String LIMITWEEKLY = "LimitWeekly";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void deleteDB(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String selectQueryUser = String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR, %s VARCHAR, %s VARCHAR, %s VARCHAR, %s VARCHAR, %s VARCHAR, %s VARCHAR, %s VARCHAR);", TABLE_USER, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, PHONE, CODE, FIRSTTIME, UPDATE_CONSTANT);
        String selectQueryRegularExpense = String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR, %s VARCHAR, %s INTEGER, %s VARCHAR, %s VARCHAR);", TABLE_EXPENSES, USERNAME, DESCRIPTION, EXPENSE_AMOUNT, CATEGORY, DATE);
        String selectQuerySetting = String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR, %s VARCHAR, %s VARCHAR, %s VARCHAR);", TABLE_SETTING, USERNAME, LIMITDAILY, LIMITWEEKLY, LIMITMONTHLY);
        String selectQueryConstantExpense = String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR, %s VARCHAR, %s INTEGER, %s VARCHAR);", TABLE_CONSTANS_EXPENSES, USERNAME, DESCRIPTION, EXPENSE_AMOUNT, CATEGORY);
        database.execSQL(selectQueryUser);
        database.execSQL(selectQueryRegularExpense);
        database.execSQL(selectQueryConstantExpense);
        database.execSQL(selectQuerySetting);
    }

    public boolean getInfoIfUsernameExist(String username) {
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;
        long result;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where Username='%s';", TABLE_USER, username);
        cursor = db.rawQuery(selectQuery, null);
        result = cursor.getCount();
        cursor.close();
        db.close();
        return result > 0 ? true : false;
    }

    public User getUserByOnlyUsername(String username) {
        User user = null;
        int totalColumn;
        SQLiteDatabase db;
        String selectQuery;
        long result;
        Cursor cursor;
        db = this.getReadableDatabase();

        selectQuery = String.format("select * from %s where %s='%s';", TABLE_USER, USERNAME, username);
        cursor = db.rawQuery(selectQuery, null);
        result = cursor.getCount();
        if (result == 1) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                user = new User();
                totalColumn = cursor.getColumnCount();
                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i).equals(FIRST_NAME)) {
                        user.setFirstName(cursor.getString(i));
                    } else if (i == 0) {
                        user.setId(cursor.getInt(i));
                    } else if (cursor.getColumnName(i).equals(LAST_NAME)) {
                        user.setLastName(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(USERNAME)) {
                        user.setUsername(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(PASSWORD)) {
                        user.setPassword(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(PHONE)) {
                        user.setPhone(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(CODE)) {
                        user.setCode(cursor.getString(i));
                    }

                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db;
        ContentValues contentValues;
        long resultInsert;

        db = this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put(FIRST_NAME, user.getFirstName());
        contentValues.put(LAST_NAME, user.getLastName());
        contentValues.put(USERNAME, user.getUsername());
        contentValues.put(PASSWORD, user.getPassword());
        contentValues.put(PHONE, user.getPhone());
        contentValues.put(CODE, user.getCode());
        contentValues.put(FIRSTTIME, user.getFirstTime());
        contentValues.put(UPDATE_CONSTANT, user.getUpdateConstanItemsThisMonth());
        resultInsert = db.update(TABLE_USER, contentValues, "id = " + user.getId(), null);
        db.close();
        return resultInsert > 0;
    }

    public User getUser(String username, String password) {
        User user = null;
        int totalColumn;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;
        long result;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where %s='%s' And %s='%s';", TABLE_USER, USERNAME, username, PASSWORD, password);
        cursor = db.rawQuery(selectQuery, null);
        result = cursor.getCount();
        if (result == 1) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                user = new User();
                totalColumn = cursor.getColumnCount();
                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i).equals(FIRST_NAME)) {
                        user.setFirstName(cursor.getString(i));
                    } else if (i == 0) {
                        user.setId(cursor.getInt(i));
                    } else if (cursor.getColumnName(i).equals(LAST_NAME)) {
                        user.setLastName(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(USERNAME)) {
                        user.setUsername(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(PASSWORD)) {
                        user.setPassword(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(PHONE)) {
                        user.setPhone(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(CODE)) {
                        user.setCode(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(FIRSTTIME)) {
                        user.setFirstTime(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(UPDATE_CONSTANT)) {
                        user.setUpdateConstanItemsThisMonth(cursor.getInt(i));
                    }
                }

                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return user;
    }

    public Setting getSetting(String username) {
        Setting setting = null;
        int totalColumn;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where %s='%s';", TABLE_SETTING, USERNAME, username);
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            setting = new Setting();
            setting.setUsername(cursor.getString(1));
            setting.setLimitDail(cursor.getString(2));
            setting.setLimitWeekly(cursor.getString(3));
            setting.setLimitMonthly(cursor.getString(4));
        }

        cursor.close();
        db.close();

        return setting;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db;
        ContentValues contentValues;
        Cursor cursor;
        String selectQuery;
        long resultBeforeInsert;
        long resultInsert;

        db = this.getWritableDatabase();
        selectQuery = String.format("select * from %s", TABLE_USER);
        cursor = db.rawQuery(selectQuery, null);
        resultBeforeInsert = cursor.getCount();
        cursor.close();
        contentValues = new ContentValues();

        contentValues.put(FIRST_NAME, user.getFirstName());
        contentValues.put(LAST_NAME, user.getLastName());
        contentValues.put(USERNAME, user.getUsername());
        contentValues.put(PASSWORD, user.getPassword());
        contentValues.put(PHONE, user.getPhone());
        contentValues.put(CODE, user.getCode());
        contentValues.put(FIRSTTIME, user.getFirstTime());
        contentValues.put(UPDATE_CONSTANT, user.getUpdateConstanItemsThisMonth());
        resultInsert = db.insert(TABLE_USER, null, contentValues);
        db.close();

        return resultInsert == resultBeforeInsert + 1;
    }

    public JSONArray findExpensesBetweenDates(String username, String fromDate, String toDate) {
        int totalColumn;
        JSONObject rowObject;
        JSONArray resultSet = null;
        SQLiteDatabase db;
        Cursor cursor;
        String selectQuery;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where %s='%s' AND %s BETWEEN '%s' AND '%s';", TABLE_EXPENSES, USERNAME, username, DATE, fromDate, toDate);
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            resultSet = new JSONArray();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                totalColumn = cursor.getColumnCount();
                rowObject = new JSONObject();
                for (int i = 0; i < totalColumn; i++) {
                    try {
                        if (i == 0) {
                            rowObject.put(cursor.getColumnName(i), cursor.getInt(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                resultSet.put(rowObject);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return resultSet;
    }

    public long deleteExpense(Integer id) {
        SQLiteDatabase db;
        long result;

        db = this.getWritableDatabase();
        result = db.delete(TABLE_EXPENSES, "id = ? ", new String[]{Integer.toString(id)});
        db.close();
        return result;
    }

    public boolean updateItem(Item item) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues contentValues;
        long result;

        contentValues = new ContentValues();
        contentValues.put(USERNAME, item.getUsername());
        contentValues.put(DESCRIPTION, item.getDescription());
        contentValues.put(EXPENSE_AMOUNT, item.getExpenseAmount());
        contentValues.put(CATEGORY, item.getCategory());
        contentValues.put(DATE, item.getDate());
        result = db.update(TABLE_EXPENSES, contentValues, "id = ? ", new String[]{Integer.toString(item.getId())});
        return result > 0;
    }

    public JSONArray getAllExpensesFromUser(String username) {
        int totalColumn;
        JSONObject rowObject;
        JSONArray resultSet;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where %s='%s'ORDER BY %s Desc;", TABLE_EXPENSES, USERNAME, username, DATE);
        cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            totalColumn = cursor.getColumnCount();
            rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                try {
                    if (i == 0) {
                        rowObject.put(cursor.getColumnName(i), cursor.getInt(i));
                    } else {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return resultSet;
    }

    public boolean insertExpense(Item item) {
        SQLiteDatabase db;
        ContentValues contentValues;
        Cursor cursor;
        String selectQuery;
        long resultBeforeInsert;
        long result;

        db = this.getWritableDatabase();
        selectQuery = String.format("select * from %s", TABLE_EXPENSES);
        cursor = db.rawQuery(selectQuery, null);
        resultBeforeInsert = cursor.getCount();
        cursor.close();

        contentValues = new ContentValues();
        contentValues.put(USERNAME, item.getUsername());
        contentValues.put(DESCRIPTION, item.getDescription());
        contentValues.put(EXPENSE_AMOUNT, item.getExpenseAmount());
        contentValues.put(CATEGORY, item.getCategory());
        contentValues.put(DATE, item.getDate());
        result = db.insert(TABLE_EXPENSES, null, contentValues);
        db.close();

        return result > resultBeforeInsert;
    }

    public JSONArray getLastItem(String username) {
        int totalColumn;
        JSONObject rowObject;
        JSONArray resultSet;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = String.format("SELECT * FROM Expenses WHERE id=(SELECT max(id) FROM Expenses) And %s='%s';", USERNAME, username);
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        resultSet = new JSONArray();
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            while (cursor.isAfterLast() == false) {
                totalColumn = cursor.getColumnCount();
                rowObject = new JSONObject();
                for (int i = 0; i < totalColumn; i++) {
                    try {
                        if (i == 0) {
                            rowObject.put(cursor.getColumnName(i), cursor.getInt(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                resultSet.put(rowObject);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return resultSet;
    }

    public boolean insertSetting(String username, String daily, String weekly, String monthly) {
        SQLiteDatabase db = this.getWritableDatabase();
        long j = db.delete(TABLE_SETTING, USERNAME + " = ? ", new String[]{username});

        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(LIMITDAILY, daily);
        contentValues.put(LIMITWEEKLY, weekly);
        contentValues.put(LIMITMONTHLY, monthly);
        long resultInsert = db.insert(TABLE_SETTING, null, contentValues);
        db.close();

        return resultInsert > 0;
    }

    public int getSumExpensesBetweenDates(String username, String fromDay, String toDate) {
        int resultSum;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;

        db = this.getReadableDatabase();
        selectQuery = String.format("select sum(ExpenseAmount) as sumExpenses from %s where %s='%s' AND %s BETWEEN '%s' AND '%s';", TABLE_EXPENSES, USERNAME, username, DATE, fromDay, toDate);
        cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        resultSum = cursor.getInt(0);
        cursor.close();
        db.close();
        return resultSum;
    }

    public int getSumExpensesByOption(String username, String option, String fromDate, String toDate) {
        int resultSum;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;

        db = this.getReadableDatabase();
        selectQuery = String.format("select sum(ExpenseAmount) as sumExpenses from %s where %s='%s' AND %s='%s' AND %s BETWEEN '%s' AND '%s';", TABLE_EXPENSES, USERNAME, username, CATEGORY, option, DATE, fromDate, toDate);
        cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        resultSum = cursor.getInt(0);
        cursor.close();
        db.close();
        return resultSum;
    }

    public JSONArray getAllConstansExpensesFromUser(String username) {
        int totalColumn;
        JSONObject rowObject;
        JSONArray resultSet;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where %s='%s'ORDER BY id;", TABLE_CONSTANS_EXPENSES, USERNAME, username);
        cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            totalColumn = cursor.getColumnCount();
            rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                try {
                    if (i == 0) {
                        rowObject.put(cursor.getColumnName(i), cursor.getInt(i));
                    } else {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return resultSet;
    }

    public long deleteConstansExpense(Integer id) {
        SQLiteDatabase db;
        long result;

        db = this.getWritableDatabase();
        result = db.delete(TABLE_CONSTANS_EXPENSES, "id = ? ", new String[]{Integer.toString(id)});
        db.close();
        return result;
    }

    public boolean insertConstantExpense(ConstantItem item) {
        SQLiteDatabase db;
        ContentValues contentValues;
        Cursor cursor;
        String selectQuery;
        long resultBeforeInsert;
        long result;

        db = this.getWritableDatabase();
        selectQuery = String.format("select * from %s", TABLE_CONSTANS_EXPENSES);
        cursor = db.rawQuery(selectQuery, null);
        resultBeforeInsert = cursor.getCount();
        cursor.close();

        contentValues = new ContentValues();
        contentValues.put(USERNAME, item.getUsername());
        contentValues.put(DESCRIPTION, item.getDescription());
        contentValues.put(EXPENSE_AMOUNT, item.getExpenseAmount());
        contentValues.put(CATEGORY, item.getCategory());
        result = db.insert(TABLE_CONSTANS_EXPENSES, null, contentValues);
        db.close();

        return result > resultBeforeInsert;
    }

    public List<ConstantItem> getListOfAllConstantItems(String username) {
        int totalColumn;
        List<ConstantItem> items = null;
        ConstantItem item = null;
        SQLiteDatabase db;
        String selectQuery;
        Cursor cursor;

        db = this.getReadableDatabase();
        selectQuery = String.format("select * from %s where %s='%s'ORDER BY id;", TABLE_CONSTANS_EXPENSES, USERNAME, username);
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            items = new ArrayList<ConstantItem>();
            while (cursor.isAfterLast() == false) {
                totalColumn = cursor.getColumnCount();
                item = new ConstantItem();
                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i).equals(EXPENSE_AMOUNT)) {
                        item.setExpenseAmount(cursor.getInt(i));
                    }  else if (cursor.getColumnName(i).equals(DESCRIPTION)) {
                        item.setDescription(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(USERNAME)) {
                        item.setUsername(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equals(CATEGORY)) {
                        item.setCategory(cursor.getString(i));
                    }else if (cursor.getColumnName(i).equals("id")) {
                        item.setId(cursor.getInt(i));
                    }
                }

                items.add(item);
                cursor.moveToNext();
            }
        }


        cursor.close();
        db.close();
        return items;
    }

    public JSONArray getLastConstantItem(String username) {
        int totalColumn;
        JSONObject rowObject;
        JSONArray resultSet;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = String.format("SELECT * FROM ConstansExpenses WHERE id=(SELECT max(id) FROM ConstansExpenses) And %s='%s';", USERNAME, username);
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        resultSet = new JSONArray();
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            while (cursor.isAfterLast() == false) {
                totalColumn = cursor.getColumnCount();
                rowObject = new JSONObject();
                for (int i = 0; i < totalColumn; i++) {
                    try {
                        if (i == 0) {
                            rowObject.put(cursor.getColumnName(i), cursor.getInt(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                resultSet.put(rowObject);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return resultSet;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSTANS_EXPENSES);
        onCreate(db);
    }
}
