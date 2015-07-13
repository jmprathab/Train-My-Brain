package thin.blog.banker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class Startup extends AppCompatActivity {
    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        SharedPreferences sharedPreferences = getSharedPreferences(ApplicationHelper.SHARED_PREFS_USER_DATA, Context.MODE_PRIVATE);
        login = sharedPreferences.getBoolean(ApplicationHelper.SUCCESSFUL_LOGIN_HISTORY, false);
        if (login) {
            startActivity(new Intent(Startup.this, StartQuiz.class));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(Startup.this, Login.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
