package pl.rcebula.volumecontroller;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VolumeControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VolumeControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolumeControllerFragment extends Fragment
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

    private MainActivity mainActivity;

    public static final String TITLE = "Volume Controller";

    private VolumeSetReceiver volumeSetReceiver;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static VolumeControllerFragment newInstance()
    {
        VolumeControllerFragment fragment = new VolumeControllerFragment();
        return fragment;
    }

    private void initializeVariables(View v)
    {
        svSeekBar = (SeekBar) v.findViewById(R.id.soundVolumeBar);
        svTextView = (TextView) v.findViewById(R.id.soundVolumeTextView);
        plus5Button = (Button) v.findViewById(R.id.plus5Button);
        plus1Button = (Button) v.findViewById(R.id.plus1Button);
        minus1Button = (Button) v.findViewById(R.id.minus1Button);
        minus5Button = (Button) v.findViewById(R.id.minus5Button);
        setButton = (Button) v.findViewById(R.id.setButton);
        getButton = (Button) v.findViewById(R.id.getButton);
        muteCheckBox = (CheckBox) v.findViewById(R.id.muteCheckBox);
        errorTextView = (TextView) v.findViewById(R.id.errorTextView);
    }

    public VolumeControllerFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("volume controller", "create");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_volume_controller, container, false);

        Log.d("volume controller", "create view");

        mainActivity = (MainActivity) getActivity();

        initializeVariables(view);

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
                setProgress(svSeekBar.getProgress() + 5, true);
            }
        });

        plus1Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setProgress(svSeekBar.getProgress() + 1, true);
            }
        });

        minus1Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setProgress(svSeekBar.getProgress() - 1, true);
            }
        });

        minus5Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setProgress(svSeekBar.getProgress() - 5, true);
            }
        });

        getButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!muteCheckBox.isChecked())
                {
                    getVolume();
                }
            }
        });

        setButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!muteCheckBox.isChecked())
                {
                    setVolume(true);
                }
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
                    setVolume(0, false);
                }
                else
                {
                    setVolume(true);
                }

                SharedPreferences settings = getActivity().getSharedPreferences("State", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("mute", mute);
                editor.commit();

                MainActivity.updateNotification(getActivity().getApplicationContext(),
                        svSeekBar.getProgress());
            }
        });

        SharedPreferences state = getActivity().getSharedPreferences("State", 0);
        boolean mute = state.getBoolean("mute", false);

        if (!mute)
        {
            getVolume();
        }

        IntentFilter filter = new IntentFilter(VolumeSetReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        volumeSetReceiver = new VolumeSetReceiver();
        getActivity().registerReceiver(volumeSetReceiver, filter);

        // Inflate the layout for this fragment
        return view;
    }

    private void setProgress(int newProgress, boolean updateNotification)
    {
        svSeekBar.setProgress(newProgress);

        if (updateNotification)
        {
            mainActivity.updateNotification(getActivity().getApplicationContext(), newProgress);
        }
    }

    private void getVolume()
    {
        new ClientConnectionAsyncTask().execute(mainActivity.getClient().GET_VOLUME);
    }

    private void setVolume(boolean getVolAfter)
    {
        setVolume(svSeekBar.getProgress(), getVolAfter);
    }

    private void setVolume(int vol, boolean getVolAfter)
    {
        new ClientConnectionAsyncTask().execute(mainActivity.getClient().SET_VOLUME, Integer.toString(vol),
                Boolean.toString(getVolAfter));

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
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public class VolumeSetReceiver extends BroadcastReceiver
    {
        public static final String ACTION_RESP =
                "VOLUME_SET";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            int vol = intent.getIntExtra(VolumeControllerService.PARAM_OUT, -1);

            setProgress(vol, false);
        }
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
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Log.d("volume controller", "stop");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        Log.d("volume controller", "destroy view");

        getActivity().unregisterReceiver(volumeSetReceiver);
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
            actionBar.setLogo(R.mipmap.ic_speaker);
        }
    }

    private class ClientConnectionAsyncTask extends AsyncTask<String, Integer, Integer>
    {
        private Exception ex = null;
        boolean setVol = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (!mainActivity.isWifiAvailable())
            {
                errorTextView.setText("You need wifi connection");

                this.cancel(true);

                return;
            }

            errorTextView.setText("");
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer vol = null;

            try
            {
                String action = params[0];

                if (action.equals(mainActivity.getClient().GET_VOLUME))
                {
                    setVol = true;
                    vol = mainActivity.getClient().getVolume();
                }
                else if (action.equals(mainActivity.getClient().SET_VOLUME))
                {
                    vol = Integer.parseInt(params[1]);
                    boolean getVolAfter = Boolean.parseBoolean(params[2]);
                    mainActivity.getClient().setVolume(vol);

                    if (getVolAfter)
                    {
                        setVol = true;
                        vol = mainActivity.getClient().getVolume();
                    }
                }
            }
            catch (Exception ex)
            {
                this.ex = ex;
            }

            return vol;
        }

        @Override
        protected void onPostExecute(Integer vol)
        {
            super.onPostExecute(vol);

            if (ex != null)
            {
                errorTextView.setText(ex.getMessage());
                return;
            }

            if (setVol)
            {
                if (vol != -1)
                {
                    setProgress(vol, true);
                }
                else
                {
                    errorTextView.setText("Can't get sound volume from server");
                }
            }

            errorTextView.setText("");
        }
    }
}
