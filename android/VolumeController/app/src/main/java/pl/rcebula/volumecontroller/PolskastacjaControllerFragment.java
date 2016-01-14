package pl.rcebula.volumecontroller;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;


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

    private final int songListUpdateInterval = 5000; // in miliseconds
    private final int playSongPosition = 1;
    private final int downSongsRange = 4;

    private Handler songListDownloaderHandler;
    private ArrayAdapter<String> songListAdapter = null;
    private List<String> songs;

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
        View view = inflater.inflate(R.layout.fragment_polskastacja_controller, container, false);

        initializeVariables(view);

        songListDownloaderHandler = new Handler();
        songListDownloaderRunner.run();

        // Inflate the layout for this fragment
        return view;
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
            super.getActivity().setTitle(TITLE);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        songListDownloaderRunner.cancel();
    }

    private class DownloadSongList extends AsyncTask<String, Integer, List<String>>
    {
        private Exception ex = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            errorTextView.setText("Loading song list...");
        }

        @Override
        protected List<String> doInBackground(String... params)
        {
            List<String> tmpSongs = null;

            try
            {
                String url = params[0];

                HandleXml handleXml = new HandleXml(url);

                handleXml.fetchXML();

                tmpSongs = handleXml.getSongTitles();

                tmpSongs = tmpSongs.subList(downSongsRange, tmpSongs.size());
            }
            catch (Exception ex)
            {
                this.ex = ex;
            }

            return tmpSongs;
        }

        @Override
        protected void onPostExecute(List<String> tmpSongs)
        {
            super.onPostExecute(tmpSongs);

            if (ex == null)
            {
                songs = tmpSongs;

                if (songListView.getAdapter() == null)
                {
                    songListAdapter = new MyArrayAdapter(
                        PolskastacjaControllerFragment.super.getActivity(),
                        android.R.layout.simple_list_item_1, songs);
                    songListView.setAdapter(songListAdapter);
                }
                else
                {
                    songListAdapter.notifyDataSetChanged();
                }
                errorTextView.setText("");
            }
            else
            {
                errorTextView.setText(ex.getMessage());
            }
        }
    }

    private class MyArrayAdapter extends ArrayAdapter<String>
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

        public MyArrayAdapter(Context context, int resource, String[] objects)
        {
            super(context, resource, objects);
        }

        public MyArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects)
        {
            super(context, resource, textViewResourceId, objects);
        }

        public MyArrayAdapter(Context context, int resource, List<String> objects)
        {
            super(context, resource, objects);
        }

        public MyArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects)
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
