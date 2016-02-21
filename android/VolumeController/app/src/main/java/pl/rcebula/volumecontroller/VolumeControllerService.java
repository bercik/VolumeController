package pl.rcebula.volumecontroller;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class VolumeControllerService extends IntentService
{
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_INC_VOL = "ACTION_INC_VOL";
    public static final String ACTION_DEC_VOL = "ACTION_DEC_VOL";

    // TODO: Rename parameters
    public static final String PARAM_INC = "PARAM_INC";
    public static final String PARAM_DEC = "PARAM_DEC";

    public static final String PARAM_OUT = "PARAM_OUT";

    public VolumeControllerService()
    {
        super("VolumeControllerService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionIncVol(Context context, Integer incVol)
    {
        Intent intent = new Intent(context, VolumeControllerService.class);
        intent.setAction(ACTION_INC_VOL);
        intent.putExtra(PARAM_INC, incVol);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_INC_VOL.equals(action))
            {
                final Integer incVol = (Integer) intent.getSerializableExtra(PARAM_INC);
                handleActionIncVol(incVol);
            }
            else if (ACTION_DEC_VOL.equals(action))
            {
                final Integer decVol = (Integer) intent.getSerializableExtra(PARAM_DEC);
                handleActionDecVol(decVol);
            }
        }
    }

    private void handleActionDecVol(Integer decVol)
    {
        handleActionIncVol(-decVol);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionIncVol(Integer incVol)
    {
        SharedPreferences state = getSharedPreferences("State", 0);
        boolean mute = state.getBoolean("mute", false);

        if (!mute)
        {
            try
            {
                SharedPreferences settings = getSharedPreferences("Preferences", 0);
                String host = settings.getString("ipaddress", MainActivity.HOST);

                Client client = new Client(host, MainActivity.PORT);

                int vol = state.getInt("sound volume", 0);

                if (vol != -1)
                {
                    int newVol = vol + incVol;
                    if (newVol > 100)
                    {
                        newVol = 100;
                    }
                    else if (newVol < 0)
                    {
                        newVol = 0;
                    }

                    client.setVolume(newVol);
                    newVol = client.getVolume();

                    MainActivity.updateNotification(getApplicationContext(), newVol);

                    // processing done here….
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(VolumeControllerFragment.VolumeSetReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(PARAM_OUT, newVol);
                    sendBroadcast(broadcastIntent);
                }
            }
            catch (IOException ex)
            {
                Log.e("volume controller serv", ex.getMessage());
            }
        }
    }
}
