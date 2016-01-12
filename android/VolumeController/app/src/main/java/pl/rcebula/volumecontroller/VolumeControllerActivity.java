package pl.rcebula.volumecontroller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;


public class VolumeControllerActivity extends ActionBarActivity
{
    private SeekBar svSeekBar;
    private TextView svTextView;
    private Button minus5Button;
    private Button minus1Button;
    private Button plus1Button;
    private Button plus5Button;
    private Button setButton;
    private Button getButton;
    private CheckBox muteCheckBox;
    private TextView errorTextView;

    private Client client;

    private BroadcastReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_controller);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initializeVariables();

        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        String host = settings.getString("ipaddress", MainActivity.HOST);

        client = new Client(host, MainActivity.PORT);
        errorTextView.setText("");

        // add events
        svSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                svTextView.setText("Sound volume: " + Integer.toString(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        plus5Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                svSeekBar.setProgress(svSeekBar.getProgress() + 5);
            }
        });

        plus1Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                svSeekBar.setProgress(svSeekBar.getProgress() + 1);
            }
        });

        minus1Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                svSeekBar.setProgress(svSeekBar.getProgress() - 1);
            }
        });

        minus5Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                svSeekBar.setProgress(svSeekBar.getProgress() - 5);
            }
        });

        getButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getVolume();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setVolume();
            }
        });

        muteCheckBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean mute = muteCheckBox.isChecked();

                if (mute)
                {
                    setVolume(0);
                }
                else
                {
                    setVolume();
                }
            }
        });

        getVolume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        wifiReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                ConnectivityManager conMan =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
                {
                    getVolume();
                }
                else
                {
                    errorTextView.setText("You need wifi connection");
                }
            }
        };

        registerReceiver(wifiReceiver, filter);

        createNotification();
    }

    private void createNotification()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Volume Controller")
                        .setContentText("")
                        .setProgress(100, svSeekBar.getProgress(), false)
                        .setAutoCancel(false);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, VolumeControllerActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(VolumeControllerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 0 allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void getVolume()
    {
        try
        {
            errorTextView.setText("");

            if (!isWifiConnected())
            {
                return;
            }

            int vol = client.getVolume();

            svSeekBar.setProgress(vol);
        }
        catch (IOException ex)
        {
            errorTextView.setText(ex.getMessage());
        }
    }

    private void setVolume()
    {
        setVolume(svSeekBar.getProgress());
    }

    private void setVolume(int vol)
    {
        try
        {
            errorTextView.setText("");

            if (!isWifiConnected())
            {
                return;
            }

            client.setVolume(vol);
        }
        catch (IOException ex)
        {
            errorTextView.setText(ex.getMessage());
        }
    }

    private boolean isWifiConnected()
    {
        ConnectivityManager connManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected())
        {
            errorTextView.setText("You need wifi connection");

            return false;
        }

        return true;
    }

    private void initializeVariables()
    {
        svSeekBar = (SeekBar) findViewById(R.id.soundVolumeBar);
        svTextView = (TextView) findViewById(R.id.soundVolumeTextView);
        plus5Button = (Button) findViewById(R.id.plus5Button);
        plus1Button = (Button) findViewById(R.id.plus1Button);
        minus1Button = (Button) findViewById(R.id.minus1Button);
        minus5Button = (Button) findViewById(R.id.minus5Button);
        setButton = (Button) findViewById(R.id.setButton);
        getButton = (Button) findViewById(R.id.getButton);
        muteCheckBox = (CheckBox) findViewById(R.id.muteCheckBox);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }
}
