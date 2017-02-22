package com.dutchit.dutchit;

import android.content.DialogInterface;
import android.icu.util.ValueIterator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class PayActivity extends AppCompatActivity {
    Button card,cash,paytm;
    CheckoutTask ctask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        card=(Button)findViewById(R.id.card);
        cash=(Button)findViewById(R.id.cash);
        paytm=(Button)findViewById(R.id.paytm);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayComplete();
            }
        });
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayComplete();
            }
        });
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashDialog();
            }
        });
    }

    public void onPayComplete(){
        String u = Globals.getOutUrl()+"?tnum="+Globals.getTableN()+"&pid="+Globals.getPId();
        ctask=new CheckoutTask();
        ctask.execute(u);
        try {
            ctask.get();
            Toast.makeText(this,"Thank You",Toast.LENGTH_SHORT).show();
            this.finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void cashDialog(){
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        LayoutInflater i = this.getLayoutInflater();
        final View dialogV=i.inflate(R.layout.dialog_cash,null);
        db.setView(dialogV);
        final EditText cash = (EditText)dialogV.findViewById(R.id.code);
        db.setTitle("Cash Payment");
        db.setMessage("Enter code from waiter");
        db.setNeutralButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("code",cash.getText().toString());
                if(cash.getText().toString().equals("1234"))
                {
                    onPayComplete();
                    dialog.cancel();
                }
                else{
                    Toast.makeText(getBaseContext(),"Invalid Code",Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog d = db.create();
        d.show();

    }
}
