package pl.rcebula.volumecontroller;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
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
    private EditText url1EditText;
    private EditText url2EditText;
    private Button setToDefaultButton;

    private void initializeVariables()
    {
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        ipAddressEditText = (EditText) findViewById(R.id.ipAddressEditText);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        url1EditText = (EditText) findViewById(R.id.url1EditText);
        url2EditText = (EditText) findViewById(R.id.url2EditText);
        setToDefaultButton = (Button) findViewById(R.id.setToDefaultButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeVariables();

        errorTextView.setText("");

        update();

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

                String url1 = url1EditText.getText().toString();
                if (!URLUtil.isValidUrl(url1))
                {
                    errorTextView.setText("Invalid URL 1");

                    return;
                }

                String url2 = url2EditText.getText().toString();
                if (!URLUtil.isValidUrl(url2))
                {
                    errorTextView.setText("Invalid URL 2");

                    return;
                }

                SharedPreferences settings = getSharedPreferences("Preferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ipaddress", ipAddress);
                editor.putString("url1", url1);
                editor.putString("url2", url2);
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

        setToDefaultButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ipAddressEditText.setText(MainActivity.HOST);
                url1EditText.setText(MainActivity.URL1);
                url2EditText.setText(MainActivity.URL2);
            }
        });
    }

    private void update()
    {
        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        ipAddressEditText.setText(settings.getString("ipaddress", MainActivity.HOST));
        url1EditText.setText(settings.getString("url1", MainActivity.URL1));
        url2EditText.setText(settings.getString("url2", MainActivity.URL2));
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
