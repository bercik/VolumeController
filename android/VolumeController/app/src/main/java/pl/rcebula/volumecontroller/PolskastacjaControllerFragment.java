package pl.rcebula.volumecontroller;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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

        updateSongList();

        // Inflate the layout for this fragment
        return view;
    }

    private void initializeVariables(View v)
    {
        songListView = (ListView) v.findViewById(R.id.songListView);
        errorTextView = (TextView) v.findViewById(R.id.errorTextView);
    }

    private void updateSongList()
    {
        HandleXml handleXml = new HandleXml(
                "http://www.polskastacja.pl/webplayer/playerControl.php?action=playlist&details=43");
        try
        {
            errorTextView.setText("");
            handleXml.fetchXML();

            List<String> songs = handleXml.getSongTitles();

            songListView.setAdapter(new ArrayAdapter<String>(super.getActivity(),
                    android.R.layout.simple_list_item_1, songs));
        }
        catch (IOException | XmlPullParserException ex)
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
}
