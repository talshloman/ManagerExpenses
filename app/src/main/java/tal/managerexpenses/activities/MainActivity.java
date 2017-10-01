package tal.managerexpenses.activities;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

import tal.managerexpenses.R;
import tal.managerexpenses.model.ActionsApp;
import tal.managerexpenses.model.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    AtomicInteger value = new AtomicInteger(0);
    DatabaseHelper db;
    WebView view;
    ActionsApp doActions;
    boolean wantExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = new DatabaseHelper(this);
        //db.deleteDB(this);
        doActions = new ActionsApp(db);
        view = new WebView(this);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        view.loadDataWithBaseURL("file:///android_asset/JS/Index.js", "javascript", "application/javascript", "UTF-8", null);
        view.loadUrl("file:///android_asset/Index.html");
        view.addJavascriptInterface(doActions, "action");
        setContentView(view);
    }

    @Override
    public void onBackPressed() {
        String pageUrl;
        String isMain;
        if (wantExit) {
            super.onBackPressed();
            return;
        }

        pageUrl = view.getUrl();
        isMain = pageUrl.substring(pageUrl.lastIndexOf("#") + 1);
        if (isMain.equals("main") == false) {
            if(isMain.equals("constantExpense"))
            {
                view.loadUrl("file:///android_asset/Index.html#main");
            }
            else
            {
                if (view.canGoBack()) {
                    view.goBack();
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            wantExit = true;
            Toast.makeText(this, "For Exit please press back again.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    wantExit = false;
                }
            }, 1000);
        }
    }
}
