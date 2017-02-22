package com.dutchit.dutchit;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SooD on 11-02-2017.
 */

class ConnectTask extends AsyncTask<String,Void,Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        return connectToUrl(params[0]);
    }
    private boolean connectToUrl(String u) {
        try {
            Log.d("Url","started");
            Log.d("Tnum",""+Globals.getTableN());
            //String params = "tnum="+Globals.getTableN()+"&Name="+Globals.getUName();
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setDoOutput(true);
            conn.setDoInput(true);
//            DataOutputStream o = new DataOutputStream(conn.getOutputStream());
            //o.writeBytes(params);
            //o.flush();
            //o.close();
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
            JSONObject jobj = new JSONObject(responseJ);
            Globals.setPId(jobj.getInt("PID"));
            Globals.setMenuUrl(jobj.getString("menu_url"));
            Globals.setBillUrl(jobj.getString("bill_url"));
            Globals.setOutUrl(jobj.getString("checkout"));
            Globals.setFBill(jobj.getString("final_bill"));
            Log.d("PID",""+Globals.getPId());
            Log.d("MenuURL",""+Globals.getMenuUrl());
            Log.d("BillURL",""+Globals.getBillUrl());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
