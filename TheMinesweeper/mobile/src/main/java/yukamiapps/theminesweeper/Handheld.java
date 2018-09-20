package yukamiapps.theminesweeper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class Handheld extends Activity {

    private TextView title;
    private LinearLayout firstRow, secondRow, instructions, aboutDeveloper;
    private ScrollView scrollView;
    private Button backToMenuB;
    private View lineA, lineB;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld);

        title = (TextView) findViewById(R.id.the_minesweeper);
        firstRow = (LinearLayout) findViewById(R.id.first_row);
        secondRow = (LinearLayout) findViewById(R.id.second_row);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        backToMenuB = (Button) findViewById(R.id.backToMenuB);
        lineA = findViewById(R.id.lineA);
        lineB = findViewById(R.id.lineB);
        instructions = (LinearLayout) findViewById(R.id.instructions);
        aboutDeveloper = (LinearLayout) findViewById(R.id.aboutDeveloper);
        uri = Uri.parse("market://details?id=" + getPackageName());

    }

    public void rateApp(View view) {
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    public void howToPlay(View view) {
        title.setVisibility(View.GONE);
        firstRow.setVisibility(View.GONE);
        secondRow.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        lineA.setVisibility(View.VISIBLE);
        lineB.setVisibility(View.VISIBLE);
        backToMenuB.setVisibility(View.VISIBLE);
        instructions.setVisibility(View.VISIBLE);
    }

    public void aboutDeveloper(View view) {
        title.setVisibility(View.GONE);
        firstRow.setVisibility(View.GONE);
        secondRow.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        lineA.setVisibility(View.VISIBLE);
        lineB.setVisibility(View.VISIBLE);
        backToMenuB.setVisibility(View.VISIBLE);
        aboutDeveloper.setVisibility(View.VISIBLE);
    }

    public void backToMenu(View view) {
        scrollView.setVisibility(View.GONE);
        lineA.setVisibility(View.GONE);
        lineB.setVisibility(View.GONE);
        backToMenuB.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        firstRow.setVisibility(View.VISIBLE);
        secondRow.setVisibility(View.VISIBLE);

        if (instructions.getVisibility() == View.VISIBLE) instructions.setVisibility(View.GONE);
        else {
            aboutDeveloper.setVisibility(View.GONE);
        }
    }
}
