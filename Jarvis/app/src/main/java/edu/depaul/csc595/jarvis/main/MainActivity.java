package edu.depaul.csc595.jarvis.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.depaul.csc595.jarvis.R;
import edu.depaul.csc595.jarvis.inventory.AppliancesActivity;
import edu.depaul.csc595.jarvis.prevention.PreventionActivity;
import edu.depaul.csc595.jarvis.profile.LogInActivity;
import edu.depaul.csc595.jarvis.profile.ProfileActivity;
import edu.depaul.csc595.jarvis.profile.user.UserInfo;
import edu.depaul.csc595.jarvis.reminders.ReminderActivity;
import edu.depaul.csc595.jarvis.rewards.RewardsActivity;
import edu.depaul.csc595.jarvis.settings.SettingsActivity;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View headerLayout;
    private TextView tv_email;
    private TextView tv_name;
    private TextView tv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        //drawer.
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //going to inflate the header view at runtime instead of in the layout so that we
        //can modify it --ed
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        //ImageView img = (ImageView)navigationView.findViewById(R.id.imageView);
        navigationView.setNavigationItemSelectedListener(this);
        tv_logout = (TextView)headerLayout.findViewById(R.id.nav_header_main_logout);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("Main", "Logged in :" + UserInfo.getInstance().getIsLoggedIn());
        //if user is logged in, set name, email and enable log out
        if(UserInfo.getInstance().getIsLoggedIn()){
            tv_email = (TextView)headerLayout.findViewById(R.id.nav_header_main_email);
            tv_name = (TextView)headerLayout.findViewById(R.id.nav_header_main_person_name);
            //tv_logout = (TextView)headerLayout.findViewById(R.id.nav_header_main_logout);
            tv_logout.setText("Not " + UserInfo.getInstance().getFirstName() + "?");
            tv_name.setText(UserInfo.getInstance().getFirstName() + " " + UserInfo.getInstance().getLastName());
            tv_email.setText(UserInfo.getInstance().getCredentials().getEmail());

            tv_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo.getInstance().logOutUser(MainActivity.this);
                }
            });
        }
        else{
            tv_logout.setText(" ");
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        //if user is logged in, set name, email and enable log out
        if(UserInfo.getInstance().getIsLoggedIn()){
            tv_email = (TextView)headerLayout.findViewById(R.id.nav_header_main_email);
            tv_name = (TextView)headerLayout.findViewById(R.id.nav_header_main_person_name);
            tv_logout = (TextView)headerLayout.findViewById(R.id.nav_header_main_logout);
            tv_logout.setText("Not " + UserInfo.getInstance().getFirstName() + "?");
            tv_name.setText(UserInfo.getInstance().getFirstName() + " " + UserInfo.getInstance().getLastName());
            tv_email.setText(UserInfo.getInstance().getCredentials().getEmail());

            tv_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo.getInstance().logOutUser(MainActivity.this);
                }
            });
        }
        else{
            tv_logout.setText(" ");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        UserInfo user = UserInfo.getInstance();

        if (id == R.id.nav_home) {
            Intent goToHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToHome);
        }
        else if (id == R.id.nav_prevention) {
            Intent goToPrevention = new Intent(getApplicationContext(), PreventionActivity.class);
            startActivity(goToPrevention);
        }
        else if (id == R.id.nav_rewards) {
            Intent goToRewards = new Intent(this, RewardsActivity.class);
            startActivity(goToRewards);

        }
        else if (id == R.id.nav_appliances) {
            Intent goToAppliances = new Intent(getApplicationContext(), AppliancesActivity.class);
            startActivity(goToAppliances);

        }
        else if (id == R.id.nav_profile) {
            Intent goToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            if(user.getIsLoggedIn()) {
                startActivity(goToProfile);
            }
            else startActivity(new Intent(getApplicationContext(), LogInActivity.class));
        }
        else if (id == R.id.nav_reminder) {
            Intent goToReminder = new Intent(getApplicationContext(), ReminderActivity.class);
            startActivity(goToReminder);

        }
        else if (id == R.id.nav_settings) {
            Intent goToSetting = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(goToSetting);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
