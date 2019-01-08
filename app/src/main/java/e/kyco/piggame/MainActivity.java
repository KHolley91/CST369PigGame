package e.kyco.piggame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String MAX_SCORE = "pref_numberOfChoices";


    private boolean preferencesChange = true; //check if preferance has been changed

/*



    private static final String PREF_NAME = "prefs";
    private static final String KEY_TROUGH = "trough";
    private static final String KEY_COMP_SCORE = "compScore";
    private static final String KEY_USER_SCORE = "userScore";
    private static final String TOTAL_WINS = "totalWins";
    private static final String TOTAL_LOSSES = "totalLosses";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

*/




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //setting default values for shared pref
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //listener to shared pref changes
      PreferenceManager.getDefaultSharedPreferences(this)
              .registerOnSharedPreferenceChangeListener(preferencesChangeListener);






    }

        protected void onStart(){
        super.onStart();
        //once preferences have been set start the game
        if(preferencesChange) {
            MainActivityFragment pigGameFragment = (MainActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.pigFragment);
            pigGameFragment.setMaximumScorePref(PreferenceManager.getDefaultSharedPreferences(this));


           // pigGameFragment.roll();
        }

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. score prefs
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }





    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Intent preferencesIntent = new Intent(this, SettingsActivity.class);
            startActivity(preferencesIntent);
            return super.onOptionsItemSelected(item);


        }

    private final OnSharedPreferenceChangeListener preferencesChangeListener =
            new OnSharedPreferenceChangeListener() {
                // called when the user changes the app's preferences
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    preferencesChange = true; // user changed app setting

                    MainActivityFragment pigFragment = (MainActivityFragment)
                            getSupportFragmentManager().findFragmentById(
                                    R.id.pigFragment);

                    if (key.equals(MAX_SCORE)) { // # of choices to display changed

                        pigFragment.updateMaxScore(sharedPreferences);
                        //playPigGame
                    } else {
                        // must select one region--set North America as default
                        SharedPreferences.Editor editor =
                                sharedPreferences.edit();
                        // regions.add(getString(R.string.default_region));
                        // editor.putStringSet(REGIONS, regions);
                        editor.putInt(MAX_SCORE, R.string.defaultScore);
                        editor.apply();


                        //toast class displays small message:https://www.javatpoint.com/android-toast-example
                        Toast.makeText(MainActivity.this,
                                R.string.notifyDefaultScore,
                                Toast.LENGTH_SHORT).show();



                    }


                    //toast to show message that game will be restarting now
                    Toast.makeText(MainActivity.this,
                            R.string.restartGame,
                            Toast.LENGTH_SHORT).show();

                }




            };


}

