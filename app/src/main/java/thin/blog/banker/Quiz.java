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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataclasses.Answer;
import dataclasses.Question;
import network.CustomRequest;
import network.VolleySingleton;


public class Quiz extends AppCompatActivity {
    private List<Question> questions = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button next, reset;
    private TextView question, message, question_category;
    private ViewGroup layout;
    private RadioGroup rg;
    private RadioButton rb1, rb2, rb3, rb4;
    private Toolbar toolbar;
    private String category;
    private int server_success;
    private String server_message;
    private SharedPreferences login_history;
    private AlertDialog alertDialog;
    private JSONArray data = new JSONArray();
    private int noOfQuestions, count;
    private int currentQuestionNumber;
    private String resultData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        layout.setVisibility(View.INVISIBLE);

        final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        formData.put("user_id", login_history.getString(ApplicationHelper.USER_DATA_USER_ID, ""));
        formData.put("category", category);
        formData.put("limit", "10");

        final CustomRequest request = new CustomRequest(Request.Method.POST, ApplicationHelper.QUIZ, formData, new Response.Listener<JSONObject>() {
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
        request.setTag(ApplicationHelper.QUIZ);
        progressDialog = ApplicationHelper.createProgressDialog(Quiz.this, true, "Please wait...\nFetching Questions");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                request.cancel();
                finish();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    private void jsonParser(JSONObject response) {
        try {
            server_success = response.getInt("success");
            if (server_success != 1) {
                server_message = response.getString("message");
            } else {
                data = response.getJSONArray("data");
                collectData();
            }
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void finalDecision() {
        if (server_success == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Quiz.this);
            builder.setTitle("Error");
            builder.setCancelable(false);
            builder.setIcon(android.R.drawable.stat_notify_error);
            builder.setMessage(server_message);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                    return;
                }
            });
            progressDialog.dismiss();
            alertDialog = builder.create();
            alertDialog.show();
        } else if (server_success == 2) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(Quiz.this);
            builder.setTitle("Login Again");
            builder.setCancelable(false);
            builder.setIcon(android.R.drawable.stat_notify_error);
            builder.setMessage(server_message);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startActivity(new Intent(Quiz.this, Login.class));
                    finish();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
            startActivity(new Intent(Quiz.this, Login.class));
            finish();
        }
    }

    private void collectData() {
        noOfQuestions = data.length();
        for (int i = 0; i < noOfQuestions; i++) {
            try {
                JSONObject questionObject = data.getJSONObject(i);
                Question current = new Question();
                current.setQNO(questionObject.getInt("qno"));
                current.setCategory(questionObject.getString("category"));
                current.setQuestion(questionObject.getString("question"));
                current.setOptionA(questionObject.getString("o1"));
                current.setOptionB(questionObject.getString("o2"));
                current.setOptionC(questionObject.getString("o3"));
                current.setOptionD(questionObject.getString("o4"));
                questions.add(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setQuestions();
        progressDialog.dismiss();
        layout.setVisibility(View.VISIBLE);
        return;
    }

    private boolean setQuestions() {
        rg.clearCheck();
        if (count < noOfQuestions) {
            Question current = questions.get(count);
            currentQuestionNumber = current.getQNO();
            question_category.setText(current.getCategory());
            question.setText(current.getQuestion());
            rb1.setText(current.getOptionA());
            rb2.setText(current.getOptionB());
            rb3.setText(current.getOptionC());
            rb4.setText(current.getOptionD());
            count += 1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
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

    public void accumulateAnswers(View v) {
        String userAnswer;
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.rb1:
                userAnswer = rb1.getText().toString();
                break;
            case R.id.rb2:
                userAnswer = rb2.getText().toString();
                break;
            case R.id.rb3:
                userAnswer = rb3.getText().toString();
                break;
            case R.id.rb4:
                userAnswer = rb4.getText().toString();
                break;
            default:
                userAnswer = "None";
                break;
        }
        Answer current = new Answer();
        current.setQNO(currentQuestionNumber);
        current.setAnswer(userAnswer);
        resultData = resultData + current.toJsonString() + ",";
        answers.add(current);
        if (count < noOfQuestions) {
            setQuestions();
        } else {
            resultData = resultData.substring(0, resultData.length() - 1);
            resultData = resultData + "]}";
            Log.d("Prathab", resultData);
            Log.d("Prathab", resultData);
            Intent start = new Intent(Quiz.this, Result.class);
            start.putExtra(ApplicationHelper.USER_ANSWERS, resultData);
            getJSONString();
            startActivity(start);
        }
        return;
    }

    public String getJSONString() {
        return resultData;
    }

    public void clearChecked(View v) {
        rg.clearCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void initialize() {
        layout = (ViewGroup) findViewById(R.id.quiz_layout);
        question = (TextView) findViewById(R.id.question);
        message = (TextView) findViewById(R.id.message);
        question_category = (TextView) findViewById(R.id.category);
        rg = (RadioGroup) findViewById(R.id.rg);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);
        next = (Button) findViewById(R.id.next);
        reset = (Button) findViewById(R.id.reset);
        Intent intent = getIntent();
        noOfQuestions = count = currentQuestionNumber = 0;
        login_history = getSharedPreferences(ApplicationHelper.SHARED_PREFS_USER_DATA, Context.MODE_PRIVATE);
        category = intent.getStringExtra(ApplicationHelper.QUIZ_CATEGORY_CHOICE);
        server_success = 0;
        server_message = "Cannot contact server\nCheck your Internet Connection and Try again";
        resultData = "";
        resultData = resultData + "{" + "\"" + "user_id" + "\"" + ":" + "\"" + login_history.getString(ApplicationHelper.USER_DATA_USER_ID, "") + "\"" + "," +
                "\"" + "table" + "\"" + ":" + "\"" + category + "\"" + "," + "\"" + "answer" + "\"" + ":" + "[";
        Log.d("Prathab", resultData);
    }
}
