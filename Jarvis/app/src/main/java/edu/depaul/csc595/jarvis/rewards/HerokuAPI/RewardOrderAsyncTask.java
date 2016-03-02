package edu.depaul.csc595.jarvis.rewards.HerokuAPI;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import edu.depaul.csc595.jarvis.rewards.Models.RewardOrderModel;

/**
 * Created by markwilhelm on 3/1/16.
 */
public class RewardOrderAsyncTask  extends AsyncTask<Object,Void,Void> {
    final String TAG = "RewardOrderAsyncTask";

    final String orderUrl = "https://jarvis-services.herokuapp.com/services/rewards/order";

    Activity activity;
    RewardOrderModel rewardOrderModel;

    protected void onPreExecute(){super.onPreExecute();}

    protected Void doInBackground(Object... params){

        if(params[0].getClass().getSuperclass() == Activity.class){
            activity = (Activity)params[0];
        }
        else if (params[0] instanceof CreateRewardEventModel){
            rewardOrderModel = (RewardOrderModel)params[0];
        }

        if(params.length > 1) {
            if (params[1] instanceof CreateRewardEventModel) {
                rewardOrderModel = (RewardOrderModel) params[1];
            }
        }
        try {
            JSONObject jsonOutput = new JSONObject();
//    {
//        "customer": "csc595g1_01",
//            "account_identifier": "csc595g1_01",
//            "campaign": "HomeSafety",
//            "recipient": {
//        "name": "Test Order",
//                "email": "csc595g1@gmail.com"
//    },
//        "sku": "TNGO-E-V-STD",
//            "amount": 1000,
//            "reward_from": "CSC595 Group1",
//            "reward_subject": "Here is your reward!",
//            "reward_message": "Way to go! Thanks!",
//            "send_reward": true,
//            "external_id": "123456-XYZ"
//    }

            jsonOutput = rewardOrderModel.toJSON();

            Log.d(TAG, "doInBackground " + jsonOutput);

            URL url = new URL(orderUrl);
            HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();
            connect.setDoOutput(true);
            connect.setDoInput(true);
            connect.setRequestProperty("Content-Type", "application/json");
            connect.setRequestProperty("Accept", "application/json");
            connect.setRequestMethod("POST");
            connect.setChunkedStreamingMode(0);

            OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());
            out.write(jsonOutput.toString());
            out.close();

            StringBuilder sb = new StringBuilder();
            int httpResult = connect.getResponseCode();
            if(httpResult == HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line = null;
                while((line = br.readLine()) != null){
                    sb.append(line);
                }
                JSONObject input =  new JSONObject(sb.toString());
                //for testing in app. comment out for production
                if(activity != null){
                    Toast.makeText(activity.getBaseContext(), "Order sent!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        catch (JSONException | IOException e){}

        return null;
    }

    protected void onPostExecute(Void result){

    }

}
