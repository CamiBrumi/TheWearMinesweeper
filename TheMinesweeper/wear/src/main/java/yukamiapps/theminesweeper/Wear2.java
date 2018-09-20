package yukamiapps.theminesweeper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Wear2 extends Activity {

    String message = "Button already pushed!";
    private String EXTRA_MESSAGE = "extra_message_id";
    private int count2Win;
    private int posV = 0;
    private int posH = 0;
    private TextView time;
    private GestureDetector mDetector;
    private RelativeLayout background;
    private LinearLayout menu;
    private Button resumeB;
    private Toast toast;
    private TextView finalMessage;
    private TextView timeTv;

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final long startTime = SystemClock.elapsedRealtime();
        menu = (LinearLayout) findViewById(R.id.menu);
        resumeB = (Button) findViewById(R.id.resumeButton);

        // Configurar un detector de gestos
        mDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                menu.setVisibility(View.VISIBLE);
                resumeB.setVisibility(View.VISIBLE);
                message = "Exit?";
                finalMessage.setText(message);
            }
        });

        background = (RelativeLayout) findViewById(R.id.RL);
        time = (TextView) findViewById(R.id.time);

        finalMessage = (TextView) findViewById(R.id.finalMessage);
        timeTv = (TextView) findViewById(R.id.timeTv);


        final int COLUM = 4;
        final int cols = 10;
        count2Win = 0;

        // fer un array de bottons gran, la que passarà informació al array petit
        final String[][] myMacroButtonsArray = new String[cols][cols];
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < cols; j++)
                myMacroButtonsArray[i][j] = "☺";

        // fer un array petit que al seu torn serà presentat a la pantalla en un gridview de 4x4
        final String[] myMicroButtonsArray = new String[COLUM * COLUM];
        fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);

        // rebre de la pantalla menú el nombre de bombes que l'usuari ha escollit
        final int bombsNum = getIntent().getIntExtra(EXTRA_MESSAGE, 0);

        // determinar la posició aleatòria de cada bomba/mina
        int bombsPos;
        int[][] bombMapPos = new int[bombsNum][2];
        for (int i = 0; i < bombsNum; i++) {
            bombsPos = randInt(0, (cols * cols) - 1);
            bombMapPos[i][0] = bombsPos / cols;
            bombMapPos[i][1] = bombsPos % cols;
        }

        // crear un mapa en el qual les totes les posicions contenen un 0
        final int myMap[][] = new int[cols][cols];
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < cols; j++)
                myMap[i][j] = 0;
//-------------------------------------------------------------------------------------------------------------------------------

        int v, h;
        // situem les bombes al mapa gran
        for (int i = 0; i < bombsNum; i++) {
            v = bombMapPos[i][0];
            h = bombMapPos[i][1];
            while (myMap[v][h] == 10) {
                bombsPos = randInt(0, (cols * cols) - 1);
                bombMapPos[i][0] = v = bombsPos / cols;
                bombMapPos[i][1] = h = bombsPos % cols;
            }
            myMap[v][h] = 10;
        }

        // fiquem els numeros al voltant de les bombes
        int count = 0;
        while (count < bombsNum) {
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < cols; j++) {
                    if (isInRange(bombMapPos[count][0], bombMapPos[count][1], i, j) && myMap[i][j] < 10)
                        myMap[i][j]++;
                }
            }
            count++;
        }

        //crear un boolean array el el qual totes les posicions contenen un False.
        // ens servirà per a indicar amb un True els botons que han sigut apretats
        final boolean[][] wasPushed = new boolean[cols][cols];
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < cols; j++)
                wasPushed[i][j] = false;

        // creem un adaptador de strings
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, myMicroButtonsArray);

        // l'adaptador agafa la informació del array petit, myMicroButtonsArray, i la passa
        // al gridview, el qual es mostrarà a la pantalla del rellotge
        final GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(adapter);

        // setegem un onItemLongClickListener que "escoltarà" quan mantindrem apretat sobre
        // un dels elements del gridview i transformarà el contingut de la cel·la clicada de
        // ☺ a ☻, o a l'inrevés.
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int verMini = position / COLUM;
                int horMini = position % COLUM;

                int verMacro = verMini + posV;
                int horMacro = horMini + posH;

                if (myMacroButtonsArray[verMacro][horMacro].equals("☻"))
                    myMacroButtonsArray[verMacro][horMacro] = "☺";
                else if (myMacroButtonsArray[verMacro][horMacro].equals("☺"))
                    myMacroButtonsArray[verMacro][horMacro] = "☻";


                fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        // setegem un onItemClickListener que "escoltarà" quan cliquem un element de la
        // llista i farà una acció o una altra depenent del que conté la posició de l'array
        // que correspon a la posició que hem clicat del gridview
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                int verMini = position / COLUM;
                int horMini = position % COLUM;

                int verMacro = verMini + posV;
                int horMacro = horMini + posH;

                // si la cel·la no ha sigut clicada anteriorment...
                if (!wasPushed[verMacro][horMacro]) {

                    // si la posició del array que correspon a la posició clicada del gridview
                    // és el nombre 10, o sigui una bomba, acabar el joc i l'usuari perdrà
                    if (myMap[verMacro][horMacro] == 10) { // † cooler
                        for (int i = 0; i < cols; i++) {
                            for (int j = 0; j < cols; j++) {
                                if (myMap[i][j] == 10) {
                                    myMacroButtonsArray[i][j] = "⚠";
                                    wasPushed[i][j] = true;
                                } else {
                                    myMacroButtonsArray[i][j] = String.valueOf(myMap[i][j]);
                                    wasPushed[i][j] = true;
                                }
                            }
                        }
                        fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);
                        adapter.notifyDataSetChanged();

                        menu.setVisibility(View.VISIBLE);
                        message = "You Lost!";
                        finalMessage.setText(message);

                        // si la posició del array que correspon a la posició clicada del gridview
                        // és el 0, desvelar totes les posicions del seu voltant que siguin nombres i no bombes.
                    } else if (myMap[verMacro][horMacro] == 0) {
                        reveal(verMacro, horMacro, cols, myMap, wasPushed, myMacroButtonsArray);
                        fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);
                        adapter.notifyDataSetChanged();

                        // si position és un nombre, desvelar-lo
                    } else {
                        myMacroButtonsArray[verMacro][horMacro] = myMap[verMacro][horMacro] + "";
                        fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);
                        adapter.notifyDataSetChanged();
                        wasPushed[verMacro][horMacro] = true;
                    }

                    // comprovar si s'han desvelat totes les posicions sense bomba. En el cas
                    // afirmatiu, l'usuari ha guanyat
                    for (int i = 0; i < cols; i++)
                        for (int j = 0; j < cols; j++) if (wasPushed[i][j]) count2Win++;

                    if (count2Win == (cols * cols - bombsNum)) {

                        long endTime = SystemClock.elapsedRealtime();
                        long elapsedMilliSeconds = endTime - startTime;
                        double elapsedSeconds = elapsedMilliSeconds / 1000;

                        int seconds = (int) elapsedSeconds % 60;
                        int mins = (int) elapsedSeconds / 60;
                        int hours = mins / 60;
                        mins = mins % 60;

                        time.setText(hours + " hours, " + mins + " minutes and " + seconds + " seconds.");


                        for (int i = 0; i < cols; i++)
                            for (int j = 0; j < cols; j++) {
                                if (myMap[i][j] == 10) {
                                    myMacroButtonsArray[i][j] = "⚠";
                                    wasPushed[i][j] = true;
                                } else {
                                    myMacroButtonsArray[i][j] = myMap[i][j] + "";
                                    wasPushed[i][j] = true;
                                }
                            }
                        fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);
                        adapter.notifyDataSetChanged();

                        menu.setVisibility(View.VISIBLE);
                        message = "You Won!";
                        finalMessage.setText(message);
                        timeTv.setVisibility(View.VISIBLE);


                    } else count2Win = 0;

                    // si la posició ja ha estat clicada amb anterioritat, mostrar un missatge d'advertencia
                } else if (!message.equals("You Won!") && !message.equals("You Lost!"))
                    showToast("Button already pushed!");

            }
        });

        // creem un detector de "swipes", o sigui quan l'usuari passi el dit cap amunt/avall/dreta/esquerra
        // per a desplaçar-se per la taula de joc/ array gran de 10x10
        new SwipeDetector(gridview).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {
                int cNum = 0;

                if (swipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                    if (posH == 0)
                        showToast("Left edge reached");
                        //Toast.makeText(Wear2.this, "You reached the left edge.", Toast.LENGTH_SHORT).show();
                    else {
                        posH--;
                        cNum = 1;
                    }
                } else if (swipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                    if (posH == 6)
                        showToast("Right edge reached");
                        //Toast.makeText(Wear2.this, "You reached the right edge.", Toast.LENGTH_SHORT).show();
                    else {
                        posH++;
                        cNum = 1;
                    }
                } else if (swipeType == SwipeDetector.SwipeTypeEnum.TOP_TO_BOTTOM) {
                    if (posV == 0)
                        showToast("Top edge reached");
                        //Toast.makeText(Wear2.this, "You reached the top edge.", Toast.LENGTH_SHORT).show();
                    else {
                        posV--;
                        cNum = 2;
                    }
                } else if (swipeType == SwipeDetector.SwipeTypeEnum.BOTTOM_TO_TOP) {
                    if (posV == 6)
                        showToast("Bottom edge reached");
                        //Toast.makeText(Wear2.this, "You reached the bottom edge.", Toast.LENGTH_SHORT).show();
                    else {
                        posV++;
                        cNum = 2;
                    }
                }

                fromMacroToMicroButtons(myMacroButtonsArray, myMicroButtonsArray, posV, posH, COLUM);
                adapter.notifyDataSetChanged();

                if ((posH == 0) && (posV == 0)) {
                    background.setBackgroundColor(Color.parseColor("#000000"));
                } else {
                    if (cNum == 1) {
                        if (posH == 0)
                            background.setBackgroundColor(Color.parseColor("#1B5E20"));
                        else if (posH == 1)
                            background.setBackgroundColor(Color.parseColor("#2E7D32"));
                        else if (posH == 2)
                            background.setBackgroundColor(Color.parseColor("#388E3C"));
                        else if (posH == 3)
                            background.setBackgroundColor(Color.parseColor("#43A047"));
                        else if (posH == 4)
                            background.setBackgroundColor(Color.parseColor("#4CAF50"));
                        else if (posH == 5)
                            background.setBackgroundColor(Color.parseColor("#66BB6A"));
                        else if (posH == 6)
                            background.setBackgroundColor(Color.parseColor("#81C784"));
                    } else if (cNum == 2) {
                        if (posV == 0)
                            background.setBackgroundColor(Color.parseColor("#004D40"));
                        else if (posV == 1)
                            background.setBackgroundColor(Color.parseColor("#00695C"));
                        else if (posV == 2)
                            background.setBackgroundColor(Color.parseColor("#00796B"));
                        else if (posV == 3)
                            background.setBackgroundColor(Color.parseColor("#00897B"));
                        else if (posV == 4)
                            background.setBackgroundColor(Color.parseColor("#009688"));
                        else if (posV == 5)
                            background.setBackgroundColor(Color.parseColor("#26A69A"));
                        else if (posV == 6)
                            background.setBackgroundColor(Color.parseColor("#4DB6AC"));
                    }
                }
            }
        });
    }



    /**
     * @param v La component vertical de la posició de la bomba
     * @param h La component horitzontal de la posició de la bomba
     * @param i La component vertical de la posició que comprovem si està al voltant de la bomba
     * @param j La component horitzontal de la posició que comprovem si està al voltant de la bomba
     * @return True, si la posició que comprovem està al voltant de la bomba, o False, en el cas contrari
     */
    public boolean isInRange(int v, int h, int i, int j) {
        boolean inRange = false;
        if (i == (v - 1)) {
            if ((j == (h - 1)) || (j == h) || (j == (h + 1))) inRange = true;
        } else if (i == v) {
            if ((j == (h - 1)) || (j == (h + 1))) inRange = true;
        } else if (i == v + 1) {
            if ((j == (h - 1)) || (j == h) || (j == (h + 1))) inRange = true;
        }
        return inRange;
    }

    // passar informació de l'array gran al petit
    public void fromMacroToMicroButtons(String[][] Macro, String[] micro, int v, int h, int columns) { //COLUM, osea el numero de colmunas del array pequeño
        int count = 0;
        for (int i = v; i < (v + columns); i++)
            for (int j = h; j < (h + columns); j++) {
                micro[count] = Macro[i][j];
                count++;
            }
    }

    // revelar les posicions del voltant d'un 0
    public void reveal(int v, int h, int cols, int[][] map, boolean[][] isPushed, String[][] buttonsArray) {
        if ((v >= 0) && (v < cols) && (h >= 0) && (h < cols)) {
            if (!(map[v][h] == 10) && !isPushed[v][h]) {
                isPushed[v][h] = true;
                buttonsArray[v][h] = map[v][h] + "";

                if (map[v][h] == 0) {
                    reveal((v - 1), h, cols, map, isPushed, buttonsArray);
                    reveal((v + 1), h, cols, map, isPushed, buttonsArray);
                    reveal(v, (h - 1), cols, map, isPushed, buttonsArray);
                    reveal(v, (h + 1), cols, map, isPushed, buttonsArray);
                }
            }
        }
    }

    // Capturar les clicades llargues
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    // acabar l'activitat
    public void finishActivity(View view) {
        finish();
    }

    public void resume(View view) {
        menu.setVisibility(View.GONE);
        resumeB.setVisibility(View.GONE);
    }

    // mostrar un Toast (missatge passatger que apareix a la pantalla)
    public void showToast(String st) { // Toast toast is declared in the class
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        toast.show();
    }
}
