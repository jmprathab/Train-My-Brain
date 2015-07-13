package thin.blog.banker;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import network.CustomRequest;
import network.VolleySingleton;

public class ApplicationHelper extends Application {

    public static final String SHARED_PREFS_USER_DATA = "login_history";
    public static final String USER_DATA_EMAIL = "email";
    public static final String USER_DATA_PASSWORD = "password";
    public static final String USER_DATA_NAME = "name";
    public static final String USER_DATA_MOBILE_NUMBER = "password";
    public static final String USER_DATA_USER_ID = "user_id";
    public static final String SUCCESSFUL_LOGIN_HISTORY = "successful_login_history";
    public static final String SUCCESSFUL_REGISTRATION_HISTORY = "successful_registration_history";
    public static final String QUIZ_CATEGORY_CHOICE = "quiz_category_choice";
    public static final String USER_ANSWERS = "answers";
    public static final String NOTIFICATIONS, STARTUP, LOGIN, SIGNUP, FORGOT_PASSWORD, RESEND, QUIZ, RESULT;

    private static final Boolean localhost = false;

    private static final String ADDRESS;
    private static ApplicationHelper sInstance;

    static {
        if (localhost) {
            ADDRESS = "http://192.168.56.1/prathab/";
        } else {
            ADDRESS = "http://www.thin.comyr.com/";
        }
        STARTUP = ADDRESS + "startup.php";
        LOGIN = ADDRESS + "login.php";
        SIGNUP = ADDRESS + "register.php";
        FORGOT_PASSWORD = ADDRESS + "forgotpassword.php";
        RESEND = ADDRESS + "resend.php";
        QUIZ = ADDRESS + "fetchquestions.php";
        RESULT = ADDRESS + "result.php";
        NOTIFICATIONS = ADDRESS + "notifications.php";
    }


    public static ApplicationHelper getMyApplicationInstance() {
        return sInstance;
    }

    public static Context getMyApplicationContext() {
        return sInstance.getApplicationContext();
    }

    public static AlertDialog.Builder createAlertDialog(Context context, String title, boolean cancelable, int icon, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(cancelable);
        builder.setIcon(icon);
        builder.setMessage(message);
        return builder;
    }

    public static ProgressDialog createProgressDialog(Context context, boolean cancelable, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setMessage(message);
        return progressDialog;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
                final CustomRequest request = new CustomRequest(Request.Method.POST, NOTIFICATIONS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int available = response.getInt("available");
                            if (available == 1) {
                                String title = response.getString("title");
                                String ticker = response.getString("ticker");
                                String text = response.getString("text");
                                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
                                Notification notification = new NotificationCompat.Builder(getApplicationContext()).setTicker(ticker).setSmallIcon(R.mipmap.trainmybrainicon).setContentTitle(title).setContentText(text).setContentIntent(pi).setAutoCancel(true).build();
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, notification);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setTag(NOTIFICATIONS);
                request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(request);
            }
        });
        thread.start();
    }

}
