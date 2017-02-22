package com.dutchit.dutchit;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SooD on 11-02-2017.
 */

public class CheckoutTask extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... params) {

        return  checkout(params[0]);
    }
    private String checkout(String u){

        Log.d("Url","started");
        Log.d("Tnum",""+Globals.getTableN());
        //String params = "tnum="+Globals.getTableN()+"&Name="+Globals.getUName();
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            int responseCode= conn.getResponseCode();
            Log.d("Conn response",""+responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String responseJ = "";
            String tmp;
            while((tmp=br.readLine())!= null)
            {
                responseJ+=tmp;
            }
            br.close();
            Log.d("Response",responseJ);
            Log.d("URL",u);

            return responseJ;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
