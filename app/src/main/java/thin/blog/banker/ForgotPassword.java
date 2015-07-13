package thin.blog.banker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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


public class ForgotPassword extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String user_input_email;
    private String user_input_new_password;
    private String user_input_mobile_number;
    private EditText email, mobile_number, new_password;
    private int server_success;
    private String server_message;
    private Button resetPassword;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_input_email = email.getText().toString();
                user_input_mobile_number = mobile_number.getText().toString();
                user_input_new_password = new_password.getText().toString();
                server_success = 0;
                server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
                if (user_input_email.contentEquals("") || user_input_new_password.contentEquals("") || user_input_mobile_number.contentEquals("")) {
                    createToast("Fill in all the fields", Toast.LENGTH_SHORT);
                } else {
                    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
                    Map<String, String> formData = new HashMap<>();
                    formData.put("email", user_input_email);
                    formData.put("new_password", user_input_new_password);
                    formData.put("mobile_number", user_input_mobile_number);

                    final CustomRequest request = new CustomRequest(Request.Method.POST, ApplicationHelper.FORGOT_PASSWORD, formData, new Response.Listener<JSONObject>() {
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
                    request.setTag(ApplicationHelper.FORGOT_PASSWORD);
                    progressDialog = ApplicationHelper.createProgressDialog(ForgotPassword.this, false, "Please wait...\nContacting Server");
                    progressDialog.show();
                    request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(request);
                }
            }
        });

    }

    private void finalDecision() {
        if (server_success == 1) {
            progressDialog.dismiss();
            builder = ApplicationHelper.createAlertDialog(ForgotPassword.this, "Password Changed", false, android.R.drawable.ic_dialog_info, server_message);
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
            builder = ApplicationHelper.createAlertDialog(ForgotPassword.this, "Cannot Reset Password", false, android.R.drawable.stat_notify_error, server_message);
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

    private void jsonParser(JSONObject response) {
        try {
            server_success = response.getInt("success");
            server_message = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        email = (EditText) findViewById(R.id.femail);
        mobile_number = (EditText) findViewById(R.id.fmobile_number);
        new_password = (EditText) findViewById(R.id.fnew_password);
        resetPassword = (Button) findViewById(R.id.forgot_password_commit);
        user_input_email = "";
        user_input_mobile_number = "";
        user_input_new_password = "";
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
        finish();
    }
}
