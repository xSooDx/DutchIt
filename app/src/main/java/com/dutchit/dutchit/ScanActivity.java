package com.dutchit.dutchit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;
    private BarcodeDetector detector;
    private Uri imageUri;
    private String result;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private ConnectTask cTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Button take_pick = (Button) findViewById(R.id.take_pic);
        scanResults = (TextView) findViewById(R.id.scan_results);
        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        take_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(ScanActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            scanResults.setText("Could not set up the detector!");
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        scanResults.setText(scanResults.getText() + code.displayValue + "\n");
                        int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.TEXT:
                                try {
                                    handleCode(code.rawValue);
                                }catch (Exception e){
                                    Log.e("HandleRes",e.getMessage());
                                    e.printStackTrace();
                                }


                                break;
                        }
                    }
                }
            }catch(Exception e)
            {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private boolean handleCode(String rawValue) throws JSONException, InterruptedException {
        JSONObject jobj = new JSONObject("{"+rawValue+"}");
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
        Log.d("Wifi:JSON",jobj.toString());
        //wc.hiddenSSID=true;
        wc.SSID="\""+jobj.getString("SSID")+"\"";
        wc.preSharedKey="\""+jobj.getString("Key")+"\"";
        Globals.setTableN(jobj.getInt("TNum"));
        Log.d("JTnum",""+jobj.getInt("TNum"));
        Log.d("Wifi Config",wc.SSID+" : " + wc.preSharedKey);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals(wc.SSID)) {
                Log.d("Wifi","Found");
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
        int i =0;
        while(!isConnected(this,wc.SSID)&& i<50)
        {
            Log.d("Is Conn","sleep"+i);
            Thread.sleep(100);
            i++;
        }
        if(!wifiManager.getConnectionInfo().getSSID().equals(wc.SSID))
        {
            Log.d("Is Conn","wroing ID(wtf) "+ wifiManager.getConnectionInfo().getSSID());
            return false;
        }
        Globals.setUrl(jobj.getString("URL"));
        cTask=new ConnectTask();
        cTask.execute(Globals.getUrl()+"?tnum="+Globals.getTableN()+"&name="+Globals.getUName());
        try {
            if(cTask.get())
            {
                Intent intent = new Intent(this,MenuActivity.class);
                startActivity(intent);
            }
            else
            {
                Log.d("Post Con","failed");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static boolean isConnected(Context context,String ssid) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED)
        {
            Log.d("Is conn "+ ssid,"conn to some nw "+m.getConnectionInfo().getSSID()+"Booo");
            if(m.getConnectionInfo().getSSID().equals(ssid))
            {
                Log.d("Is conn","conn to "+ssid+"yay!");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(ScanActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 800;
        int targetH = 800;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }
}


