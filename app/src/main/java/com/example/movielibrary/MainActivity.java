package com.example.movielibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.movielibrary.movie.Movie;
import com.example.movielibrary.movie.MovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    String genre;
    TextView myTitle, myYear, myCountry, myGenre, myCost, myKeywords;

    ArrayList<String> myList = new ArrayList<>();
    ArrayAdapter myAdapter;
    int count = 0;
    DrawerLayout drawer;

    ArrayList<Movie> movies = new ArrayList<>();
    Gson gson = new Gson();

    private MovieViewModel mMovieViewModel;
    MyRecyclerViewAdapter adapter;

    DatabaseReference myRef;

    View myFrame;
    int x_down;
    int y_down;

    View myConstraintLayout;
    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        myTitle = findViewById(R.id.editTextTitle);
        myYear = findViewById(R.id.editTextYear);
        myCountry = findViewById(R.id.editTextCountry);
        myGenre = findViewById(R.id.editTextGenre);
        myCost = findViewById(R.id.editTextCost);
        myKeywords = findViewById(R.id.editTextKeywords);

        myFrame = findViewById(R.id.frame_layout_id);

        myConstraintLayout = findViewById(R.id.constraint_layout_id);
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureDetector());

        myConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                scaleGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        myFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getActionMasked();

                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        x_down = (int) motionEvent.getX();
                        y_down = (int) motionEvent.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        int x_up = (int) motionEvent.getX();
                        int y_up = (int) motionEvent.getY();

                        // Horizontal swiping
                        if(x_up-x_down > 150 && Math.abs(y_down-y_up) < 40){
                            addMovie();
                        }

                        // Vertical swiping
                        if(y_up-y_down > 150 && Math.abs(x_down-x_up) < 40){
                            clearFields();
                        }

                        // Top right
                        if(y_up-y_down < 10 && (myFrame.getWidth()-x_down < 50) && (y_up < 50)){
                            Toast.makeText(getApplicationContext(), "Top Right", Toast.LENGTH_SHORT).show();

                            String costStr = myCost.getText().toString();
                            costStr = (costStr.equals("")) ? "0" : costStr;
                            int cost = Integer.parseInt(costStr);

                            int newCost = cost + 50;
                            myCost.setText(newCost + "");
                        }

                        // Top Left
                        if(y_up-y_down < 10 && (x_up < 50) && (y_up < 50)){
                            Toast.makeText(getApplicationContext(), "Top Left", Toast.LENGTH_SHORT).show();

                            String costStr = myCost.getText().toString();
                            costStr = (costStr.equals("")) ? "0" : costStr;
                            int cost = Integer.parseInt(costStr);

                            if(cost >= 50){
                                int newCost = cost - 50;
                                myCost.setText(newCost + "");
                            }
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        mMovieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        adapter = new MyRecyclerViewAdapter(mMovieViewModel);
        mMovieViewModel.getAllItems().observe(this, newData -> {
            adapter.setMovies(newData);
            adapter.notifyDataSetChanged();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, myList);

        ListView listView = findViewById(R.id.lv);
        listView.setAdapter(myAdapter);

        drawer = findViewById(R.id.dl);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(new MyNavigationListener());

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Item Added",Snackbar.LENGTH_LONG).setAction("undo",UndoListener).show();
                addMovie();
            }
        });

        Button addMovieButton = findViewById(R.id.btnAddMovie);
        addMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMovie();
            }
        });

        restoreSharedPreferences();

        /* Request permissions to access SMS */
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS},
                0);

        /* Create and instantiate the local broadcast receiver
           This class listens to messages come from class SMSReceiver
         */
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();

        /*
         * Register the broadcast handler with the intent filter that is declared in
         * class SMSReceiver @line 11
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER));

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Movie");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Toast.makeText(getApplicationContext(),"Movie added!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Toast.makeText(getApplicationContext(),"Movie deleted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Toast.makeText(getApplicationContext(), "Single Tap", Toast.LENGTH_SHORT).show();
            myCost = findViewById(R.id.editTextCost);
            String costStr = myCost.getText().toString();
            costStr = (costStr.equals("")) ? "0" : costStr;
            int cost = Integer.parseInt(costStr);

            int newCost = cost + 150;
            myCost.setText(newCost + "");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Toast.makeText(getApplicationContext(), "Double Tap", Toast.LENGTH_SHORT).show();
            loadMovies();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT).show();
            clearFields();
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Toast.makeText(getApplicationContext(), "Scrolling", Toast.LENGTH_SHORT).show();

            String yearStr = myYear.getText().toString();
            yearStr = (yearStr.equals("")) ? "0" : yearStr;
            int year = Integer.parseInt(yearStr);

            if(distanceX > 0){
                Toast.makeText(getApplicationContext(), "Right to left Scrolling", Toast.LENGTH_SHORT).show();
                int newYear = year - (int) distanceX;
                myYear.setText(newYear + "");
            } else if (distanceX < 0) {
                Toast.makeText(getApplicationContext(), "Left to right Scrolling", Toast.LENGTH_SHORT).show();
                int newYear = year - (int) distanceX;
                myYear.setText(newYear + "");
            } else {
                myKeywords = findViewById(R.id.editTextKeywords);
                String keywords = myKeywords.getText().toString().toUpperCase();
                myKeywords.setText(keywords);
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(Math.abs(velocityX) >= 1000 || Math.abs(velocityY) >= 1000){
                Toast.makeText(getApplicationContext(), "onFling", Toast.LENGTH_SHORT).show();
                moveTaskToBack(true);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    class MyScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            myKeywords = findViewById(R.id.editTextKeywords);
            String keywords = myKeywords.getText().toString().toLowerCase();
            myKeywords.setText(keywords);
            return super.onScale(detector);
        }
    }

    public void loadMovies(){
        myTitle = findViewById(R.id.editTextTitle);
        myYear = findViewById(R.id.editTextYear);
        myCountry = findViewById(R.id.editTextCountry);
        myGenre = findViewById(R.id.editTextGenre);
        myCost = findViewById(R.id.editTextCost);
        myKeywords = findViewById(R.id.editTextKeywords);

        myTitle.setText("Spider Man No Way Home");
        myYear.setText("2021");
        myCountry.setText("USA");
        myGenre.setText("Action");
        myCost.setText("15");
        myKeywords.setText("MCU");
    }

    View.OnClickListener UndoListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            removeMovie();
        }
    };

    class MyNavigationListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // get the id of the selected item
            int id = item.getItemId();
            if (id == R.id.addMovieMenu) {
                addMovie();
            } else if (id == R.id.removeLastMovieMenu) {
                removeMovie();
            } else if (id == R.id.removeAllMoviesMenu){
                removeAllMovies();
            } else if (id == R.id.closeMenu){
                closeActivity();
            } else if (id == R.id.listAllMoviesMenu){
                showMoviesUsingGson();
            }
            // close the drawer
            drawer.closeDrawers();
            // tell the OS
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // get the id of the selected item
        int id = item.getItemId();
        if (id == R.id.clearFieldMenu) {
            resetField(findViewById(R.id.constraint_layout_id));
        } else if (id == R.id.totalMoviesMenu){
            getTotalMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addMovie(){
        saveSharedPreferences();
        String title = myTitle.getText().toString();
        String yearStr = myYear.getText().toString();
        count++;
        myList.add(count + ") " + title + " | " + yearStr);
        String toastMessage = String.format("Movie - %s - has been added", title);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        myAdapter.notifyDataSetChanged();

        yearStr = (yearStr.equals("")) ? "2000" : yearStr;
        int year = Integer.parseInt(yearStr);
        String country = myCountry.getText().toString();
        String genre = myGenre.getText().toString();

        String costStr = myCost.getText().toString();
        costStr = (costStr.equals("")) ? "0" : costStr;
        int cost = Integer.parseInt(costStr);
        String keywords = myKeywords.getText().toString();

        Movie movie = new Movie(title, year, country, genre, cost, keywords);

        // Add a movie object to the arraylist
        movies.add(movie);

        // Add a movie to the local database (Room Database)
        mMovieViewModel.insert(movie);

        // Add a movie to firebase database
        myRef.push().setValue(movie);
    }

    public void removeMovie(){
        if(myList.size() > 0){
            String toastMessage = String.format("%s has been removed", myList.get(myList.size()-1));
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            myList.remove(myList.size()-1);
            count--;
            movies.remove(movies.size()-1);
            myAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(),"There are no movies to remove", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeAllMovies(){
        myList.clear();
        count = 0;

        // Delete all Movies in the arraylist
        movies.clear();
        myAdapter.notifyDataSetChanged();
        Toast.makeText(this, "All movies have been cleared", Toast.LENGTH_SHORT).show();

        // Delete all movies in the local database (Room Database)
        mMovieViewModel.deleteAll();

        // Delete all movies in the firebase database
        myRef.removeValue();
    }

    public void showMoviesUsingGson()
    {
        String moviesStr = gson.toJson(movies);
        SharedPreferences sP = getSharedPreferences("f1",0);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("KEY_GSON", moviesStr);
        editor.apply();

        Intent intent = new Intent(this, MovieListActivity.class);
        startActivity(intent);
    }

    public void closeActivity(){
        finish();
    }

    public void getTotalMovies(){
        int totalMovies = myList.size();
        String toastMsg = String.format("Total Movie(s): %d", totalMovies);
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        /*
         * This method 'onReceive' will get executed every time class SMSReceive sends a broadcast
         * */
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Retrieve the message from the intent
             * */
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            /*
             * String Tokenizer is used to parse the incoming message
             * The protocol is to have the account holder name and account number separate by a semicolon
             * */

            StringTokenizer sT = new StringTokenizer(msg, ";");
            String title = sT.nextToken();
            String year = sT.nextToken();
            String country = sT.nextToken();
            String genre = sT.nextToken();
            String cost = sT.nextToken();
            String keywords = sT.nextToken();
            String hiddenCost = sT.nextToken();

            /*
             * Now, its time to update the UI
             * */
            Number newCost = Integer.parseInt(cost) + Integer.parseInt(hiddenCost);

            myTitle.setText(title);
            myYear.setText(year);
            myCountry.setText(country);
            myGenre.setText(genre);
            myCost.setText(newCost.toString());
            myKeywords.setText(keywords);
        }
    }

    public void showToast(View view) {
        EditText editTextMovieTitle = findViewById(R.id.editTextTitle);

        String toastMessage = String.format("Movie - %s - has been added", editTextMovieTitle.getText().toString());

        Toast myMessage = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        myMessage.show();
    }

    public void doubleCost(View view) {
        EditText editTextCost = findViewById(R.id.editTextCost);

        if (editTextCost.getText().toString().trim().length() > 0) {
            Number cost = Integer.parseInt(editTextCost.getText().toString()) * 2;
            editTextCost.setText(cost.toString());
        }
    }

    public void clearFields(){
        resetField(findViewById(R.id.constraint_layout_id));
    }

    public void resetField(ViewGroup group) {
        // loop all instances of EditText
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).getText().clear();
            }
        }
    }

    public void resetClick(View view) {
        resetField(findViewById(R.id.constraint_layout_id));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText txtGenre = findViewById(R.id.editTextGenre);
        genre = txtGenre.getText().toString().toLowerCase();
        // Save the genre into a bundle
        outState.putString("genre",genre);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        EditText txtTitle = findViewById(R.id.editTextTitle);
        txtTitle.setText(txtTitle.getText().toString().toUpperCase());

        EditText txtGenre = findViewById(R.id.editTextGenre);
        // Retrieve the genre from the bundle
        genre = savedInstanceState.getString("genre");
        txtGenre.setText(genre);
    }

    public void saveSharedPreferences() {
        ViewGroup group = findViewById(R.id.constraint_layout_id);

        SharedPreferences sP = getSharedPreferences("f1",0);
        SharedPreferences.Editor editor = sP.edit();

        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                editor.putString(""+i,((EditText) view).getText().toString());
            }
        }

        EditText txtCost = findViewById(R.id.editTextCost);
        editor.putString("cost",txtCost.getText().toString());

        //editor.commit();
        editor.apply();
    }

    public void restoreSharedPreferences(){
        ViewGroup group = findViewById(R.id.constraint_layout_id);

        SharedPreferences sP = getSharedPreferences("f1",0);

        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText(sP.getString(""+i,""));
            }
        }

        EditText txtCost = findViewById(R.id.editTextCost);
        txtCost.setText(sP.getString("cost",""));
    }

    public void clearSharedPreferences(View view){
        SharedPreferences sP = getSharedPreferences("f1",0);
        SharedPreferences.Editor editor = sP.edit();
        //editor.clear().commit();
        editor.clear().apply();
    }

    public void loadCostSharedPreferences(View view){
        SharedPreferences sP = getSharedPreferences("f1",0);
        SharedPreferences.Editor editor = sP.edit();

        String costSp = sP.getString("cost","");
        if (costSp.trim().length() > 0) {
            Number doubleCostSp = Integer.parseInt(costSp) * 2;

            EditText txtCost = findViewById(R.id.editTextCost);
            txtCost.setText(doubleCostSp.toString());

            editor.putString("cost", doubleCostSp.toString());
            editor.apply();
        }
    }
}