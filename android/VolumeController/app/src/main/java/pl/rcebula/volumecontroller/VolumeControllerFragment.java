package pl.rcebula.volumecontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


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

    private Client client;

    public static final String TITLE = "Volume Controller";

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_volume_controller, container, false);

        initializeVariables(view);

        SharedPreferences settings = super.getActivity().getSharedPreferences("Preferences", 0);
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
                setVolume(true);
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
            }
        });

        getVolume();

        // Inflate the layout for this fragment
        return view;
    }

    private void getVolume()
    {
        new ClientConnectionAsyncTask().execute("GET_VOL");
    }

    private void setVolume(boolean getVolAfter)
    {
        setVolume(svSeekBar.getProgress(), getVolAfter);
    }

    private void setVolume(int vol, boolean getVolAfter)
    {
        new ClientConnectionAsyncTask().execute("SET_VOL", Integer.toString(vol),
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

    private class ClientConnectionAsyncTask extends AsyncTask<String, Integer, Integer>
    {
        private Exception ex = null;
        boolean setVol = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (!((MainActivity) getActivity()).isWifiAvailable())
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

                if (action.equals("GET_VOL"))
                {
                    setVol = true;
                    vol = client.getVolume();
                }
                else if (action.equals("SET_VOL"))
                {
                    vol = Integer.parseInt(params[1]);
                    boolean getVolAfter = Boolean.parseBoolean(params[2]);
                    client.setVolume(vol);

                    if (getVolAfter)
                    {
                        setVol = true;
                        vol = client.getVolume();
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
                    svSeekBar.setProgress(vol);
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
