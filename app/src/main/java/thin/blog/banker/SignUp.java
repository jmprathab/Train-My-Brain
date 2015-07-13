package thin.blog.banker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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


public class SignUp extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private int server_success;
    private String server_message;
    private SharedPreferences user_data;
    private SharedPreferences.Editor editor;
    private EditText name, email, password, mobileNumber;
    private Button createAccount;
    private String user_input_name, user_input_email, user_input_password, user_input_mobile_number;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_input_name = name.getText().toString();
                user_input_email = email.getText().toString();
                user_input_password = password.getText().toString();
                user_input_mobile_number = mobileNumber.getText().toString();
                server_success = 0;
                server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
                if (user_input_email.contentEquals("") || user_input_password.contentEquals("") || user_input_name.contentEquals("") || user_input_mobile_number.contentEquals("")) {
                    createToast("Fill in all the fields", Toast.LENGTH_SHORT);
                } else {
                    RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
                    Map<String, String> formData = new HashMap<>();
                    formData.put("name", user_input_name);
                    formData.put("email", user_input_email);
                    formData.put("password", user_input_password);
                    formData.put("mobile_number", user_input_mobile_number);

                    final CustomRequest request = new CustomRequest(Request.Method.POST, ApplicationHelper.SIGNUP, formData, new Response.Listener<JSONObject>() {
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
                    request.setTag(ApplicationHelper.SIGNUP);
                    progressDialog = ApplicationHelper.createProgressDialog(SignUp.this, false, "Please wait...\nContacting Server");
                    progressDialog.show();
                    request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(request);
                }
            }
        });
    }

    private void jsonParser(JSONObject response) {
        try {
            server_success = response.getInt("success");
            server_message = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        if (server_success == 1) {
            editor.putString(ApplicationHelper.USER_DATA_EMAIL, user_input_email);
            editor.putString(ApplicationHelper.USER_DATA_PASSWORD, user_input_password);
            editor.putString(ApplicationHelper.USER_DATA_NAME, user_input_name);
            editor.putString(ApplicationHelper.USER_DATA_MOBILE_NUMBER, user_input_mobile_number);
            editor.putBoolean(ApplicationHelper.SUCCESSFUL_REGISTRATION_HISTORY, true);
            editor.apply();
            progressDialog.dismiss();
            builder = ApplicationHelper.createAlertDialog(SignUp.this, "Successfully Registered", false, android.R.drawable.ic_dialog_info, server_message);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();

        } else {
            progressDialog.dismiss();
            builder = ApplicationHelper.createAlertDialog(SignUp.this, "Cannot Register", false, android.R.drawable.stat_notify_error, server_message);
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

    private void initialize() {
        name = (EditText) findViewById(R.id.cname);
        email = (EditText) findViewById(R.id.cemail);
        password = (EditText) findViewById(R.id.cpassword);
        mobileNumber = (EditText) findViewById(R.id.cmobile_number);
        createAccount = (Button) findViewById(R.id.create_account);
        user_data = getSharedPreferences(ApplicationHelper.SHARED_PREFS_USER_DATA, Context.MODE_PRIVATE);
        editor = user_data.edit();
        user_input_mobile_number = user_input_password = user_input_name = user_input_email = "";
        server_success = 0;
        server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
    }

    public void resendConfirmation(View v) {
        startActivity(new Intent(SignUp.this, ResendConfirmation.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        name.setText("");
        email.setText("");
        password.setText("");
        mobileNumber.setText("");
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
}
