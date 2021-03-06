package pl.rcebula.volumecontroller;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PolskastacjaControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PolskastacjaControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PolskastacjaControllerFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;

    private static final String TITLE = "Polskastacja Controller";

    private ListView songListView;
    private TextView errorTextView;

    private final int songListUpdateInterval = 10000; // in miliseconds
    private final int playSongPosition = 1;

    private Handler songListDownloaderHandler;
    private ArrayAdapter<HandleXml.Song> songListAdapter = null;
    private List<HandleXml.Song> songs = new ArrayList<>();

    private class MyRunnable implements Runnable
    {
        private DownloadSongList songListDownloader = new DownloadSongList();

        @Override
        public void run()
        {
            if (songListDownloader.getStatus() == AsyncTask.Status.FINISHED
                    || songListDownloader.getStatus() == AsyncTask.Status.PENDING)
            {
                songListDownloader = new DownloadSongList();

                songListDownloader.execute("http://www.polskastacja.pl/webplayer/" +
                        "playerControl.php?action=playlist&details=43");

                songListDownloaderHandler.postDelayed(songListDownloaderRunner,
                        songListUpdateInterval);
            }
        }

        public void cancel()
        {
            songListDownloaderHandler.removeCallbacks(songListDownloaderRunner);
            songListDownloader.cancel(true);
        }
    }

    MyRunnable songListDownloaderRunner = new MyRunnable();

    public PolskastacjaControllerFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PolskastacjaControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PolskastacjaControllerFragment newInstance()
    {
        PolskastacjaControllerFragment fragment = new PolskastacjaControllerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("polskastacja", "create view");

        View view = inflater.inflate(R.layout.fragment_polskastacja_controller, container, false);

        initializeVariables(view);

        songListDownloaderHandler = new Handler();

        songListAdapter = new MyArrayAdapter(
                PolskastacjaControllerFragment.super.getActivity(),
                android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(songListAdapter);

        if (savedInstanceState != null)
        {
            songs.clear();
            songs.addAll((List<HandleXml.Song>) savedInstanceState.getSerializable("songs"));
            songListAdapter.notifyDataSetChanged();
        }

        registerForContextMenu(songListView);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == songListView)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(songs.get(info.position).getTitle());
            menu.add("Copy to clipboard");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        super.onContextItemSelected(item);

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String songTitle = songs.get(info.position).getTitle();

        ClipboardManager clipboard =
                (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("song title", songTitle);
        clipboard.setPrimaryClip(clip);

        return true;
    }

    private void initializeVariables(View v)
    {
        songListView = (ListView) v.findViewById(R.id.songListView);
        errorTextView = (TextView) v.findViewById(R.id.errorTextView);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        else
        {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            super.getActivity().setTitle(Html.fromHtml("<small>" + TITLE + "</small>"));

            ActionBarActivity activity = (ActionBarActivity)super.getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setLogo(R.mipmap.ic_polskastacja);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Log.d("polskastacja", "resume");

        songListDownloaderRunner.run();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Log.d("polskastacja", "stop");

        songListDownloaderRunner.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        Log.d("polskastacja", "save instance bundle");

        outState.putSerializable("songs", (Serializable) songs);
    }

    private int getPlayedIndex(List<HandleXml.Song> songs)
    {
        for (int i = 0; i < songs.size(); ++i)
        {
            if (songs.get(i).isPlaying())
            {
                return i;
            }
        }

        return -1;
    }

    private class DownloadSongList extends AsyncTask<String, Integer, List<HandleXml.Song>>
    {
        private Exception ex = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (!((MainActivity)PolskastacjaControllerFragment.super.getActivity()).isNetworkAvailable())
            {
                errorTextView.setText("You need internet connection");

                this.cancel(true);
                return;
            }

            errorTextView.setText("Loading song list...");
        }

        @Override
        protected List<HandleXml.Song> doInBackground(String... params)
        {
            List<HandleXml.Song> tmpSongs = null;

            try
            {
                String url = params[0];

                HandleXml handleXml = new HandleXml(url);

                handleXml.fetchXML();

                tmpSongs = handleXml.getSongs();

                tmpSongs = cutBottom(tmpSongs);
            }
            catch (Exception ex)
            {
                this.ex = ex;
            }

            return tmpSongs;
        }

        private List<HandleXml.Song> cutBottom(List<HandleXml.Song> songs)
        {
            int playedIndex = getPlayedIndex(songs);

            int cutDownIndex = playedIndex - playSongPosition;

            return songs.subList(cutDownIndex, songs.size());
        }

        @Override
        protected void onPostExecute(List<HandleXml.Song> tmpSongs)
        {
            super.onPostExecute(tmpSongs);

            if (ex == null)
            {
                songs.clear();
                songs.addAll(tmpSongs);

                songListAdapter.notifyDataSetChanged();

                errorTextView.setText("");
            }
            else
            {
                errorTextView.setText(ex.getMessage());
            }
        }
    }

    private class MyArrayAdapter extends ArrayAdapter<HandleXml.Song>
    {
        private ColorStateList defaultTextColors = null;

        public MyArrayAdapter(Context context, int resource)
        {
            super(context, resource);
        }

        public MyArrayAdapter(Context context, int resource, int textViewResourceId)
        {
            super(context, resource, textViewResourceId);
        }

        public MyArrayAdapter(Context context, int resource, HandleXml.Song[] objects)
        {
            super(context, resource, objects);
        }

        public MyArrayAdapter(Context context, int resource, int textViewResourceId, HandleXml.Song[] objects)
        {
            super(context, resource, textViewResourceId, objects);
        }

        public MyArrayAdapter(Context context, int resource, List<HandleXml.Song> objects)
        {
            super(context, resource, objects);
        }

        public MyArrayAdapter(Context context, int resource, int textViewResourceId, List<HandleXml.Song> objects)
        {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            TextView view = (TextView) super.getView(position, convertView, parent);

            if (defaultTextColors == null)
            {
                defaultTextColors = view.getTextColors();
            }

            if (position == playSongPosition)
            {
                view.setTextColor(Color.RED);
            }
            else
            {
                view.setTextColor(defaultTextColors);
            }

            return view;
        }
    }
}
