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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UrlControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UrlControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UrlControllerFragment extends Fragment
{

    private Client client;

    private BroadcastReceiver wifiReceiver = null;

    public static final String TITLE = "URL Controller";

    private Button openButton;
    private Button closeButton;
    private TextView errorTextView;
    private TextView urlTextView;

    private String url;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static UrlControllerFragment newInstance()
    {
        UrlControllerFragment fragment = new UrlControllerFragment();
        return fragment;
    }

    public UrlControllerFragment()
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
        View view = inflater.inflate(R.layout.fragment_url_controller, container, false);

        SharedPreferences settings = super.getActivity().getSharedPreferences("Preferences", 0);
        String host = settings.getString("ipaddress", MainActivity.HOST);
        url = settings.getString("url", MainActivity.URL);

        client = new Client(host, MainActivity.PORT);

        initializeVariables(view);

        urlTextView.setText("URL: " + url);
        errorTextView.setText("");

        openButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openURL();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeURL();
            }
        });

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
                }
                else
                {
                    errorTextView.setText("You need wifi connection");
                }
            }
        };

        super.getActivity().registerReceiver(wifiReceiver, filter);

        // Inflate the layout for this fragment
        return view;
    }

    private void initializeVariables(View v)
    {
        errorTextView = (TextView) v.findViewById(R.id.errorTextView);
        urlTextView = (TextView) v.findViewById(R.id.urlTextView);
        openButton = (Button) v.findViewById(R.id.openButton);
        closeButton = (Button) v.findViewById(R.id.closeButton);
    }

    private void openURL()
    {
        try
        {
            errorTextView.setText("");
            client.openURL(url);
        }
        catch (IOException ex)
        {
            errorTextView.setText(ex.getMessage());
        }
    }

    private void closeURL()
    {
        try
        {
            errorTextView.setText("");
            client.closeURL();
        }
        catch (IOException ex)
        {
            errorTextView.setText(ex.getMessage());
        }
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

        if (wifiReceiver != null)
        {
            super.getActivity().unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
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
}
