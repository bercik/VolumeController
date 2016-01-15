package pl.rcebula.volumecontroller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends ActionBarActivity
        implements VolumeControllerFragment.OnFragmentInteractionListener,
        UrlControllerFragment.OnFragmentInteractionListener,
        PolskastacjaControllerFragment.OnFragmentInteractionListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static final String HOST = "192.168.1.10";
    public static final int PORT = 5656;
    public static final String URL1 =
            "http://www.polskastacja.pl/webplayer/index.php?channel=43&version=20150602";
    public static final String URL2 =
            "https://play.spotify.com/user/bercikos/playlist/06yODuxCoQ6OQSPS81bmtX?play=true";

    private Client client;

    private static NotificationCompat.Builder mBuilder;
    private static NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("main", "create");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        String host = settings.getString("ipaddress", MainActivity.HOST);

        client = new Client(host, MainActivity.PORT);

        createNotification();
        new ClientGetVolumeAsyncTask().execute();
    }

    public Client getClient()
    {
        return client;
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
    protected void onResume()
    {
        super.onResume();

        Log.d("main", "resume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        Log.d("main", "pause");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Log.d("main", "new intent");
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        Log.d("main", "stop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Log.d("main", "destroy");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 0 allows you to update the notification later on.
        mNotificationManager.cancel(0);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    return VolumeControllerFragment.newInstance();
                case 1:
                    return UrlControllerFragment.newInstance();
                case 2:
                    return PolskastacjaControllerFragment.newInstance();
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    private class ClientGetVolumeAsyncTask extends AsyncTask<String, Integer, Integer>
    {
        @Override
        protected Integer doInBackground(String... params)
        {
            int vol;

            try
            {
                vol = client.getVolume();
            }
            catch (IOException ex)
            {
                vol = 0;
            }

            return vol;
        }

        @Override
        protected void onPostExecute(Integer vol)
        {
            super.onPostExecute(vol);

            // createNotification(vol);
            updateNotification(vol);
        }
    }

    private void createNotification()
    {
        Intent incVolIntent = new Intent(getApplicationContext(), VolumeControllerService.class);
        incVolIntent.putExtra(VolumeControllerService.PARAM_CLIENT, client);
        incVolIntent.putExtra(VolumeControllerService.PARAM_INC, 5);
        incVolIntent.setAction(VolumeControllerService.ACTION_INC_VOL);
        PendingIntent incVolPIntent = PendingIntent.getService(getApplicationContext(),
                (int) System.currentTimeMillis(), incVolIntent, 0);

        Intent decVolIntent = new Intent(getApplicationContext(), VolumeControllerService.class);
        incVolIntent.putExtra(VolumeControllerService.PARAM_CLIENT, client);
        incVolIntent.putExtra(VolumeControllerService.PARAM_INC, -5);
        incVolIntent.setAction(VolumeControllerService.ACTION_INC_VOL);
        PendingIntent decVolPIntent = PendingIntent.getService(getApplicationContext(),
                (int) System.currentTimeMillis(), incVolIntent, 0);

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Volume Controller")
                        .setContentText("")
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .addAction(R.drawable.ic_stat_plus, "+5", incVolPIntent)
                        .addAction(R.drawable.ic_stat_minus, "-5", decVolPIntent);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                (int) System.currentTimeMillis(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 0 allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static void updateNotification(int newVol)
    {
        mBuilder.setProgress(100, newVol, false);
        mBuilder.setContentText(Integer.toString(newVol) + "%");

        // 0 allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isWifiAvailable()
    {
        ConnectivityManager connManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected())
        {
            return false;
        }

        return true;
    }

}
