package thin.blog.banker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class StartQuiz extends AppCompatActivity implements View.OnClickListener {
    private Button aptitude, verbal, technical;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_quiz, menu);
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

    private void initialize() {
        aptitude = (Button) findViewById(R.id.aptitude);
        verbal = (Button) findViewById(R.id.verbal);
        technical = (Button) findViewById(R.id.technical);
        aptitude.setOnClickListener(this);
        verbal.setOnClickListener(this);
        technical.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(StartQuiz.this, Quiz.class);
        switch (v.getId()) {
            case R.id.aptitude:
                intent.putExtra(ApplicationHelper.QUIZ_CATEGORY_CHOICE, "aptitude");
                break;
            case R.id.verbal:
                intent.putExtra(ApplicationHelper.QUIZ_CATEGORY_CHOICE, "verbal");
                break;
            case R.id.technical:
                intent.putExtra(ApplicationHelper.QUIZ_CATEGORY_CHOICE, "technical");
                break;
        }
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        createToast("Select any one category shown above", Toast.LENGTH_SHORT);
        createToast("Random questions will be fetched from selected category", Toast.LENGTH_SHORT);
    }
}
