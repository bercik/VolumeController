package pl.rcebula.volumecontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    public static final String TITLE = "URL Controller";

    private Button openButton;
    private Button closeButton;
    private TextView errorTextView;
    private Button shutdownButton;

    private RadioGroup urlsRadioGroup;
    private List<RadioButton> urlRadioButtons;

    private List<Urls.Url> urls;

    private OnFragmentInteractionListener mListener;

    private MainActivity mainActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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

        mainActivity = (MainActivity) getActivity();

        urls = new Urls().getUrls();
        urlRadioButtons = new ArrayList<>();

        initializeVariables(view);
        createRadioButtons();

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

        shutdownButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                                shutdown();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to shutdown computer?")
                        .setNegativeButton("No", dialogClickListener)
                        .setPositiveButton("Yes", dialogClickListener).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void createRadioButtons()
    {
        boolean first = true;

        for (Urls.Url url : urls)
        {
            RadioButton rb = new RadioButton(getActivity());

            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[] { android.R.attr.state_checked },
                    getResources().getDrawable(R.mipmap.checked_radio_button));
            stateListDrawable.addState(new int[] { },
                    getResources().getDrawable(R.mipmap.unchecked_radio_button));
            rb.setButtonDrawable(stateListDrawable);

            rb.setText(url.getDescription());

            urlsRadioGroup.addView(rb);

            if (first)
            {
                first = false;
                rb.setChecked(true);
            }

            urlRadioButtons.add(rb);
        }
    }

    private void initializeVariables(View v)
    {
        errorTextView = (TextView) v.findViewById(R.id.errorTextView);
        openButton = (Button) v.findViewById(R.id.openButton);
        closeButton = (Button) v.findViewById(R.id.closeButton);
        shutdownButton = (Button) v.findViewById(R.id.shutdownButton);
        urlsRadioGroup = (RadioGroup) v.findViewById(R.id.urlsRadioGroup);
    }

    private void openURL()
    {
        String url = null;

        for (int i = 0; i < urlRadioButtons.size(); ++i)
        {
            if (urlRadioButtons.get(i).isChecked())
            {
                url = urls.get(i).getUrl();
            }
        }

        new ClientConnectionAsyncTask().execute(mainActivity.getClient().OPEN_URL, url);
    }

    private void closeURL()
    {
        new ClientConnectionAsyncTask().execute(mainActivity.getClient().CLOSE_URL);
    }

    private void shutdown()
    {
        new ClientConnectionAsyncTask().execute(mainActivity.getClient().SHUTDOWN);
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
            super.getActivity().setTitle(Html.fromHtml("<small>" + TITLE + "</small>"));

            ActionBarActivity activity = (ActionBarActivity)super.getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setLogo(R.mipmap.ic_url);
        }
    }

    private class ClientConnectionAsyncTask extends AsyncTask<String, Integer, Integer>
    {
        private Exception ex = null;

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
            try
            {
                String action = params[0];

                if (action.equals(mainActivity.getClient().OPEN_URL))
                {
                    String url = params[1];

                    mainActivity.getClient().openURL(url);
                }
                else if (action.equals(mainActivity.getClient().CLOSE_URL))
                {
                    mainActivity.getClient().closeURL();
                }
                else if (action.equals(mainActivity.getClient().SHUTDOWN))
                {
                    mainActivity.getClient().shutdown();
                }
            }
            catch (Exception ex)
            {
                this.ex = ex;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer)
        {
            super.onPostExecute(integer);

            if (ex != null)
            {
                errorTextView.setText(ex.getMessage());

                return;
            }

            errorTextView.setText("");
        }
    }
}
