package com.dutchit.dutchit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;


public class BillActivity extends AppCompatActivity {

    BillTask billTask;
    JSONObject bill;
    TextView total,pid,tid;
    WebView listView;
    Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        total = (TextView)findViewById(R.id.total);
        pid = (TextView)findViewById(R.id.pid);
        tid = (TextView)findViewById(R.id.tid);
        pay = (Button)findViewById(R.id.pay);
        listView = (WebView) findViewById(R.id.item_list);
        billTask=new BillTask();
        String tu = Globals.getBillUrl()+"?tnum="+Globals.getTableN();
        Log.d("Bill Url",tu);
        billTask.execute(tu);
        try{

            bill=billTask.get();
            Log.d("Bill BILL",bill.toString());
            Globals.setgAmount(bill.getJSONObject("pids").getJSONObject(""+Globals.getPId()).getInt("total"));
            total.setText(""+Globals.getgAmount());

            Globals.setBill(bill);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Bill","JSON ERROR");
        }
        finally {
            Log.d("Set pid",""+Globals.getPId());
            pid.setText(""+Globals.getPId());
            Log.d("Set tid",""+Globals.getTableN());
            tid.setText(""+Globals.getTableN());
        }
        final Intent payI = new Intent(this,PayActivity.class);
        Log.d("Intent","Created");
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Pay","clicked");
                startActivity(payI);
            }
        });
        Log.d("listener","created");
        listView.loadUrl(Globals.getFBill());
        listView.getSettings().setJavaScriptEnabled(true);
        listView.addJavascriptInterface(new DutchInterface(),"DI");

    }
    class DutchInterface{
        int Pid;
        int Tid;
        String name;

        public DutchInterface(){
            Pid=Globals.getPId();
            Tid=Globals.getTableN();
            name=Globals.getUName();
        }
        @android.webkit.JavascriptInterface
        public int getPId() {
            return Pid;
        }
        @android.webkit.JavascriptInterface
        public int getTId() {
            return Tid;
        }
        @android.webkit.JavascriptInterface
        public String getName() {
            return name;
        }
    }
}
