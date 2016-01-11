package pl.rcebula.volumecontroller;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class SettingsActivity extends ActionBarActivity
{

    private Button saveButton;
    private Button cancelButton;
    private EditText ipAddressEditText;
    private TextView errorTextView;

    private void initializeVariables()
    {
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        ipAddressEditText = (EditText) findViewById(R.id.ipAddressEditText);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeVariables();

        errorTextView.setText("");

        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        ipAddressEditText.setText(settings.getString("ipaddress", MainActivity.HOST));

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                errorTextView.setText("");

                String ipAddress = ipAddressEditText.getText().toString();
                if (!checkIPv4(ipAddress))
                {
                    errorTextView.setText("Invalid ip address");

                    return;
                }

                SharedPreferences settings = getSharedPreferences("Preferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ipaddress", ipAddress);
                editor.commit();

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private boolean checkIPv4(final String ip)
    {
        boolean isIPv4;
        try
        {
            final InetAddress inet = InetAddress.getByName(ip);
            isIPv4 = inet.getHostAddress().equals(ip)
                    && inet instanceof Inet4Address;
        }
        catch (final UnknownHostException e)
        {
            isIPv4 = false;
        }

        return isIPv4;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
