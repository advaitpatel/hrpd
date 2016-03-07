package edu.depaul.csc595.jarvis.rewards;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import edu.depaul.csc595.jarvis.R;
import edu.depaul.csc595.jarvis.profile.user.UserInfo;
import edu.depaul.csc595.jarvis.rewards.HerokuAPI.RewardOrderAsyncTask;
import edu.depaul.csc595.jarvis.rewards.Models.RewardCatalogModel;
import edu.depaul.csc595.jarvis.rewards.Models.RewardOrderModel;
import edu.depaul.csc595.jarvis.rewards.adapters.RewardOrderAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class RewardOrderFragment extends Fragment {

    private RewardOrderAsyncTask rewardOrderAsyncTask;
    private TotalPointsAsyncTask totalPointsAsyncTask;
    private RewardsCatalogAsyncTask rewardsCatalogAsyncTask;

    private ArrayList<RewardCatalogModel> alRewardCatalogModel;

    private RelativeLayout catalog_layout;
    private TextView tv_amount;
    private TextView tv_sku;

    private String recipient_email = "";


    public RewardOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_rewards_order, container, false);

//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_reward_order_recycler_view);

        buildUI(rootView);

        catalog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //need to add async task to place an order.
                rewardOrderAsyncTask = new RewardOrderAsyncTask();
                RewardOrderModel rewardOrderModel = buildOrder();
                rewardOrderAsyncTask.execute(rewardOrderModel, null, null);

                String orderPlaced = "Order Placed...";
                Snackbar.make(v, orderPlaced, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        alRewardCatalogModel = new ArrayList<RewardCatalogModel>();
        TextView card_view_rewards_content = (TextView) rootView.findViewById(R.id.card_view_rewards_content);

        //Get the total reward points for this user
        totalPointsAsyncTask = new TotalPointsAsyncTask();
        totalPointsAsyncTask.execute(RewardOrderFragment.this, card_view_rewards_content);

        //Get the full catalog for orders
        rewardsCatalogAsyncTask = new RewardsCatalogAsyncTask();
        rewardsCatalogAsyncTask.execute(RewardOrderFragment.this, alRewardCatalogModel);

        RewardOrderAdapter adapter = new RewardOrderAdapter(alRewardCatalogModel);
//        recyclerView.setAdapter(adapter);


        return rootView;

    }

    private void buildUI(View rootView) {

        catalog_layout = (RelativeLayout) rootView.findViewById(R.id.catalog_layout);
        tv_sku = (TextView) rootView.findViewById(R.id.tv_sku);
        tv_amount = (TextView) rootView.findViewById(R.id.tv_amount);

    }

    private RewardOrderModel buildOrder() {
        boolean isAuthed = false;

        if (UserInfo.getInstance().isGoogleLoggedIn()) {
            isAuthed = true;
            recipient_email = UserInfo.getInstance().getGoogleAccount().getEmail();
        } else if (UserInfo.getInstance().getIsLoggedIn()) {
            isAuthed = true;
            recipient_email = UserInfo.getInstance().getCredentials().getEmail();
        }

        RewardOrderModel rewardOrderModel = new RewardOrderModel();

        //check if logged in, if so, send reward event
        if (isAuthed) {
            rewardOrderModel.setRecipient_name(UserInfo.getInstance().getFirstName() + " " + UserInfo.getInstance().getLastName());
            rewardOrderModel.setRecipient_email(recipient_email);

            String amount = "1000";
            rewardOrderModel.setAmount(Integer.valueOf(amount));

            rewardOrderModel.setSku("TNGO-E-V-STD");

        } else {
            //You must first log in...
        }


        return rewardOrderModel;
    }


    class TotalPointsAsyncTask extends AsyncTask<Object, Void, Integer> {
        private final String TAG = "TotalPointsAsyncTask";
        private final String baseURL = "https://jarvis-services.herokuapp.com/services/rewards/totalpoints?email=";
        private RewardOrderFragment rewardOrderFragment;
        private TextView points_tv;

        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected Integer doInBackground(Object... params) {

            String email;
            JSONObject jobj;

            if(UserInfo.getInstance().isGoogleLoggedIn()){
                email = UserInfo.getInstance().getGoogleAccount().getEmail();
            }
            else if(UserInfo.getInstance().getIsLoggedIn()){
                email = UserInfo.getInstance().getCredentials().getEmail();
            }
            else{
                return null;
            }

            //do any params setting here....
            if(params.length > 0){
                if(params[0] instanceof RewardOrderFragment) {
                    rewardOrderFragment = (RewardOrderFragment) params[0];
                }
                if(params.length > 1){
                    if(params[1] instanceof TextView){
                        points_tv = (TextView) params[1];
                    }
                }
            }

            //get points...
            try{
                URL url = new URL(baseURL + email);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("GET");
                connection.setChunkedStreamingMode(0);

                StringBuilder sb = new StringBuilder();
                int response = connection.getResponseCode();
                if(response == HttpsURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    jobj = new JSONObject(sb.toString());
                    return jobj.getInt("sum");
                }
            }
            catch(MalformedURLException e){
                Log.d(TAG, "doInBackground error, returning 0");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return 0;
            }
            catch(IOException e){
                Log.d(TAG, "doInBackground error, returning 0");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return 0;
            }
            catch(JSONException e){
                Log.d(TAG, "doInBackground error, returning 0");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return 0;
            }

            return null;
        }

        protected void onPostExecute(Integer result){
//            @+id/card_view_rewards_content
            if(rewardOrderFragment != null){
                if(points_tv != null){
                    points_tv.setText(Integer.toString(result));
                    //profileFragment.
                }
            }
        }

    }


    class RewardsCatalogAsyncTask extends AsyncTask<Object, Void, ArrayList<RewardCatalogModel>> {
        private final String TAG = "RewardsCatalogAsyncTask";
        private final String baseURL = "https://jarvis-services.herokuapp.com/services/rewards/catalog";
        private RewardOrderFragment rewardOrderFragment;
        private TextView points_tv;
        ArrayList<RewardCatalogModel> alRewardCatalogModel;

        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected ArrayList<RewardCatalogModel> doInBackground(Object... params) {

            String email;
            JSONObject jsonRewardCatalog = new JSONObject();

            if(UserInfo.getInstance().isGoogleLoggedIn()){
                email = UserInfo.getInstance().getGoogleAccount().getEmail();
            }
            else if(UserInfo.getInstance().getIsLoggedIn()){
                email = UserInfo.getInstance().getCredentials().getEmail();
            }
            else{
                return null;
            }

            //do any params setting here....
            if(params.length > 0){
                if(params[0] instanceof RewardOrderFragment) {
                    rewardOrderFragment = (RewardOrderFragment) params[0];
                }
                if(params.length > 1){
                    alRewardCatalogModel = (ArrayList<RewardCatalogModel>) params[1];
                }
            }

            //get points...
            try{
                URL url = new URL(baseURL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("GET");
                connection.setChunkedStreamingMode(0);

                StringBuilder sb = new StringBuilder();
                int response = connection.getResponseCode();
                if(response == HttpsURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    jsonRewardCatalog = new JSONObject(sb.toString());

                    //return buildRewardCatalogList(jsonRewardCatalog);
                }
            }
            catch(MalformedURLException e){
                Log.d(TAG, "doInBackground error, returning null");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return null;
            }
            catch(IOException e){
                Log.d(TAG, "doInBackground error, returning null");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return null;
            }
            catch(JSONException e){
                Log.d(TAG, "doInBackground error, returning null");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return null;
            } catch (Exception e) {
                Log.d(TAG, "doInBackground error, returning null");
                Log.d(TAG, "doInBackground " + e.getMessage());
                return null;
            }

            return buildRewardCatalogList(jsonRewardCatalog);
        }

        protected void onPostExecute(ArrayList<RewardCatalogModel> result){
//            @+id/card_view_rewards_content
            if(rewardOrderFragment != null){
                alRewardCatalogModel = result;
            }
        }

        private ArrayList<RewardCatalogModel> buildRewardCatalogList(JSONObject jsonRewardCatalog) {
            JSONArray jsonArrayCatalogItems;
            ArrayList<RewardCatalogModel> alRewardCatalogModel = new ArrayList<RewardCatalogModel>();

            try {
                jsonArrayCatalogItems = jsonRewardCatalog.getJSONArray("catalogItems");
                for (int i = 0; i < jsonArrayCatalogItems.length(); i++) {
                    RewardCatalogModel rewardCatalogModel = new RewardCatalogModel();
                    JSONObject jsonRewardCatalogItem = jsonArrayCatalogItems.getJSONObject(i);
                    rewardCatalogModel.setCatalogId(jsonRewardCatalogItem.getString("catalogId"));
                    rewardCatalogModel.setBrand(jsonRewardCatalogItem.getString("brand"));
                    rewardCatalogModel.setImage_url(jsonRewardCatalogItem.getString("image_url"));
                    rewardCatalogModel.setType(jsonRewardCatalogItem.getString("type"));
                    rewardCatalogModel.setDescription(jsonRewardCatalogItem.getString("description"));
                    rewardCatalogModel.setSku(jsonRewardCatalogItem.getString("sku"));
                    rewardCatalogModel.setIs_variable(jsonRewardCatalogItem.getBoolean("is_variable"));

                    rewardCatalogModel.setDenomination(jsonRewardCatalogItem.getInt("denomination"));
                    rewardCatalogModel.setMin_price(jsonRewardCatalogItem.getInt("min_price"));
                    rewardCatalogModel.setMax_price(jsonRewardCatalogItem.getInt("max_price"));

                    rewardCatalogModel.setCurrency_code(jsonRewardCatalogItem.getString("currency_code"));
                    rewardCatalogModel.setAvailable(jsonRewardCatalogItem.getBoolean("available"));
                    rewardCatalogModel.setCountry_code(jsonRewardCatalogItem.getString("country_code"));
                    rewardCatalogModel.setTstamp(jsonRewardCatalogItem.getString("dttm"));

                    alRewardCatalogModel.add(rewardCatalogModel);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return alRewardCatalogModel;
        }

    }

//    {
//        "customer": "csc595g1_01",
//        "account_identifier": "csc595g1_01",
//        "campaign": "HomeSafety",
//        "recipient": {
//            "name": "Test Order",
//            "email": "csc595g1@gmail.com"
//        },
//        "sku": "TNGO-E-V-STD",
//            "amount": 1000,
//            "reward_from": "CSC595 Group1",
//            "reward_subject": "Here is your reward!",
//            "reward_message": "Way to go! Thanks!",
//            "send_reward": true,
//            "external_id": "123456-XYZ"
//    }


}
