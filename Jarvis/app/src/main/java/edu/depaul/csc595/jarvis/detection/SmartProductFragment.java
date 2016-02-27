package edu.depaul.csc595.jarvis.detection;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.depaul.csc595.jarvis.R;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmartProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmartProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String LOG_TAG = "SmartProductFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String email;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    public final int RESULT_OK = 1;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SmartProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SmartProductFragment newInstance(String param1, String param2) {
        SmartProductFragment fragment = new SmartProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SmartProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        DetectionBaseActivity activity = (DetectionBaseActivity) getActivity();
        email = activity.getEmail();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(LOG_TAG, " " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Log.d(LOG_TAG, "resultCode == Activity.RESULT_OK");
            SmartProductListFragment fragment = (SmartProductListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
            fragment.updateSmartProducts();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_smart_product, container, false);
        ButterKnife.bind(this, view);

        Log.d(LOG_TAG, "Fab is " + fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "I came here");
                    Intent intent = new Intent(getActivity(), RegisterProducts.class);
                    intent.putExtra(DetectionBaseActivity.EMAIL_EXTRA, email);
                    Log.d(LOG_TAG, "Email: " + email);
                    startActivityForResult(intent, RESULT_OK);
                }
            });
        }

        return view;
    }




}
