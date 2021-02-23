package com.w20.telephonyapidemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    private TelephonyManager mTelephonyManager;

    Button sendMessageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mDialButton = findViewById(R.id.btn_dial);
        final EditText mPhoneNoEt = findViewById(R.id.et_phone_no);

        sendMessageBtn = (Button) findViewById(R.id.btn_send_message);
        final EditText messagetEt = (EditText) findViewById(R.id.et_message);

        // initializing the Telephony manager instance
        mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        mDialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = mPhoneNoEt.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    // an intent to dial a number
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));

                    /*
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(dial));
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("CALL", "onClick: call permission denied" );
                        return;
                    }
                    // an intent to call a number
                    startActivity(callIntent);

                     */
                }else {
                    Toast.makeText(MainActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendMessageBtn.setEnabled(false);
        if(checkPermission(Manifest.permission.SEND_SMS)) {
            sendMessageBtn.setEnabled(true);
        }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messagetEt.getText().toString();
                String phoneNo = mPhoneNoEt.getText().toString();
                if(!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNo)) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNo));
                    smsIntent.putExtra("sms_body", message);
                    startActivity(smsIntent);
//                    if(checkPermission(Manifest.permission.SEND_SMS)) {
//                        SmsManager smsManager = SmsManager.getDefault();
//                        /*
//                        * If you want to use sentIntent,
//                        * watch for the result code Activity.RESULT_OK on success,
//                        * or one of RESULT_ERROR_GENERIC_FAILURE, RESULT_ERROR_RADIO_OFF,
//                        * and RESULT_ERROR_NULL_PDU to indicate an error.
//                        * */
//                        smsManager.sendTextMessage(phoneNo, null, message, null, null);
//                    }else {
//                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            switch(state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Toast.makeText(MainActivity.this, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(MainActivity.this, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(MainActivity.this, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*
    * In the onResume() method, we can begin to listen using the TelephonyManager listen() method,
    * passing it the PhoneStateListener instance and the static LISTEN_CALL_STATE.
    * We stop listening in the onStop() method by passing the LISTEN_NONE as the second argument to listen().
    * */
    @Override
    protected void onResume() {
        super.onResume();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendMessageBtn.setEnabled(true);
                }
                return;
            }
        }
    }
}
