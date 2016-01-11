package pl.rcebula.volumecontroller;

import android.graphics.Color;
import android.os.StrictMode;
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


public class MainActivity extends ActionBarActivity
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

    private final String HOST = "192.168.1.10";
    private final int PORT = 5656;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initializeVariables();

        client = new Client(HOST, PORT);
        errorTextView.setTextColor(Color.RED);
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

    }

    private void getVolume()
    {
        try
        {
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
            client.setVolume(vol);
        }
        catch (IOException ex)
        {
            errorTextView.setText(ex.getMessage());
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
