package thin.blog.banker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class Result extends AppCompatActivity {
    private int error_flag;
    private TextView total, marks, error;
    private Button okay;
    private ViewGroup layout;
    private Toolbar toolbar;
    private Intent intent;
    private String userAnswer;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private int server_success;
    private String server_message, marks_obtained, total_questions, errors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        layout.setVisibility(View.INVISIBLE);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
        final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        formData.put("answer", userAnswer);
        Log.d("user_data", userAnswer);
        final CustomRequest request = new CustomRequest(Request.Method.POST, ApplicationHelper.RESULT, formData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
                finalDecision();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finalDecision();
            }
        });
        request.setTag(ApplicationHelper.RESULT);
        progressDialog = ApplicationHelper.createProgressDialog(Result.this, false, "Please wait...\nCalculating Marks");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                request.cancel();
                finish();
                return;
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(6000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void finalDecision() {
        progressDialog.dismiss();
        if (server_success == 1) {
            marks.setText(marks_obtained);
            total.setText(total_questions);
            if (error_flag == 1) {
                layout.setVisibility(View.VISIBLE);
                error.setVisibility(View.VISIBLE);
                error.setText(errors);
                createToast("Thank you for taking the quiz :-)", Toast.LENGTH_LONG);
            }
        } else if (server_success == 0) {
            builder = ApplicationHelper.createAlertDialog(Result.this, "Cannot Calculate Result", false, android.R.drawable.stat_notify_error, server_message);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                    return;
                }
            });
            alertDialog = builder.create();
            alertDialog.show();

        } else if (server_success == 2) {
            builder = ApplicationHelper.createAlertDialog(Result.this, "Cannot Calculate Result", false, android.R.drawable.stat_notify_error, server_message);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                    return;
                }
            });
            alertDialog = builder.create();
            alertDialog.show();

        }
    }

    private void jsonParser(JSONObject response) {
        try {
            server_success = response.getInt("success");
            if (server_success == 0) {
                server_message = response.getString("message");
                finalDecision();
            } else if (server_success == 1) {
                marks_obtained = response.getString("marks");
                total_questions = response.getString("total_questions");
                error_flag = response.getInt("error_flag");
                errors = response.getString("");
                finalDecision();
            } else if (server_success == 2) {
                server_message = response.getString("message");
                finalDecision();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        layout = (ViewGroup) findViewById(R.id.result_layout);
        intent = getIntent();
        userAnswer = intent.getStringExtra(ApplicationHelper.USER_ANSWERS);
        total = (TextView) findViewById(R.id.total);
        marks = (TextView) findViewById(R.id.marks);
        error = (TextView) findViewById(R.id.error);
        okay = (Button) findViewById(R.id.okay);
        server_success = 0;
        server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
        total_questions = marks_obtained = errors = "";
        error_flag = 0;
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
