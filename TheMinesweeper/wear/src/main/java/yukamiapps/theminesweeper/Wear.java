package yukamiapps.theminesweeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Wear extends Activity {

    private LinearLayout mainMenu;
    private RelativeLayout instructions;
    private TextView minesNumTV;
    private int minesNum;
    private String EXTRA_MESSAGE = "extra_message_id";
    private Toast toast;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

        mDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                menu.setVisibility(View.VISIBLE);
                resumeB.setVisibility(View.VISIBLE);
                message = "Exit?";
                finalMessage.setText(message);
            }
        });

        mainMenu = (LinearLayout) findViewById(R.id.main_menu_layout);
        instructions = (RelativeLayout) findViewById(R.id.instructions_layout);

        minesNumTV = (TextView) findViewById(R.id.minesNum);
        minesNum = 3;
        display();
    }

    // incrementar en 1 el nombre de bombes.
    // aquest mètode és cridat quan el botó '+' és clicat
    public void increment(View view) {
        if (minesNum < 50) {
            minesNum = minesNum + 1;
            display();
        } else showToast("Cannot increment more!");
    }

    // disminuir en 1 el nombre de bombes.
    // aquest mètode és cridat quan el botó '-' és clicat
    public void decrement(View view) {
        if (minesNum > 3) {
            minesNum = minesNum - 1;
            display();
        } else showToast("Cannot decrement more!");
    }

    // començar la partida
    // aquest mètode és cridat quan el botó 'PLAY' és clicat
    public void play(View view) {
        Intent intent = new Intent(this, Wear2.class);
        intent.putExtra(EXTRA_MESSAGE, minesNum);
        startActivity(intent);
    }

    // mostrar el nombre de bombes en el textView que hi ha entre els botons + i -.
    private void display() {
        minesNumTV.setText(String.valueOf(minesNum));
    }


    // mostrar la pantalla d'auxili del joc
    // aquest mètode és cridat quan el botó 'Help' és clicat
    public void help(View view) {
        mainMenu.setVisibility(View.GONE);
        instructions.setVisibility(View.VISIBLE);
    }

    // tornar a la pantalla de menú principal
    // aquest mètode és cridat quan el botó 'Back to Menu' és clicat
    public void backToMenu(View view) {
        instructions.setVisibility(View.GONE);
        mainMenu.setVisibility(View.VISIBLE);
    }

    // mostrar a la pantall un Toast quan el nombre de bombes és mínim o màxim
    public void showToast(String st) { // Toast toast is declared in the class
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        toast.show();
    }

    // sortir del joc
    // aquest mètode és cridat quan el botó 'Exit Game' és clicat
    public void finishActivity(View view) {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

}