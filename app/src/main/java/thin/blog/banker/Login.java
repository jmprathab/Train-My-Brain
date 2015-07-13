package thin.blog.banker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import network.CustomRequest;
import network.VolleySingleton;

public class Login extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private String user_input_email, user_input_password, user_data_user_id;
    private SharedPreferences login_history;
    private SharedPreferences.Editor editor;
    private EditText email, password;
    private Button login;
    private String server_message;
    private int server_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();//XML References
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_input_email = email.getText().toString();
                user_input_password = password.getText().toString();
                server_success = 0;
                server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
                if (user_input_email.contentEquals("") || user_input_password.contentEquals("")) {
                    createToast("Fill in all the fields", Toast.LENGTH_SHORT);
                } else {
                    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
                    Map<String, String> formData = new HashMap<>();
                    formData.put("email", user_input_email);
                    formData.put("password", user_input_password);

                    final CustomRequest request = new CustomRequest(Request.Method.POST, ApplicationHelper.LOGIN, formData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            jsonParser(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            finalDecision();
                        }
                    });
                    request.setTag(ApplicationHelper.LOGIN);
                    request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    progressDialog = ApplicationHelper.createProgressDialog(Login.this, true, "Please wait...\nContacting Server");
                    progressDialog.show();
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            request.cancel();
                        }
                    });
                    requestQueue.add(request);
                }
            }
        });
    }

    private void jsonParser(JSONObject response) {
        try {
            server_success = response.getInt("success");
            server_message = response.getString("message");
            user_data_user_id = response.getString("user_id");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        if (server_success == 1) {
            editor.putString(ApplicationHelper.USER_DATA_USER_ID, user_data_user_id);
            editor.putString(ApplicationHelper.USER_DATA_EMAIL, user_input_email);
            editor.putString(ApplicationHelper.USER_DATA_PASSWORD, user_input_password);
            editor.putBoolean(ApplicationHelper.SUCCESSFUL_LOGIN_HISTORY, true);
            editor.apply();
            progressDialog.dismiss();
            startActivity(new Intent(Login.this, StartQuiz.class));
            finish();
        } else {
            progressDialog.dismiss();
            builder = ApplicationHelper.createAlertDialog(Login.this, "Cannot Login", false, android.R.drawable.stat_notify_error, server_message);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void createAccount(View v) {
        startActivity(new Intent(Login.this, SignUp.class));
    }


    public void forgotPassword(View v) {
        startActivity(new Intent(Login.this, ForgotPassword.class));
    }


    private void initialize() {
        email = (EditText) findViewById(R.id.lemail);
        password = (EditText) findViewById(R.id.lpassword);
        login = (Button) findViewById(R.id.login);
        login_history = getSharedPreferences(ApplicationHelper.SHARED_PREFS_USER_DATA, Context.MODE_PRIVATE);
        editor = login_history.edit();
        user_input_email = "";
        user_data_user_id = user_input_password = "";
        server_success = 0;
        server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
    }

    private void createToast(String message, int duration) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }


    @Override
    protected void onPause() {

        super.onPause();
        email.setText("");
        password.setText("");
    }
}
