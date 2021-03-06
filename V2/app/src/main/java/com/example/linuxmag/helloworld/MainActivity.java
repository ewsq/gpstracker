package com.example.linuxmag.helloworld;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


/**
 *
 * <h4>Classe principale - Activity</h4>
 *
 * @author fredericamps@gmail.com
 */
public class MainActivity extends AppCompatActivity {

    EditText editTextPhone;
    EditText editTextPass;
    CheckBox checkBoxRecord;

    Context myContext;
    Controller myController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this.getApplicationContext();
        myController = new Controller(this.getApplicationContext(), MainActivity.this);

        // Champ numero telephone
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        checkBoxRecord = (CheckBox) findViewById(R.id.checkBoxRecord);

        // unlock
        Button buttonUnlock = (Button) findViewById(R.id.buttonUnlock);
        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((myController.unlock(editTextPass.getText().toString()))) {
                    editTextPhone.setText(myController.getTel());
                    checkBoxRecord.setChecked(myController.getRecord());
                } else {
                    Toast.makeText(myContext, "Unlock failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        // start
        Button buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myController.isStartOk(editTextPhone.getText().toString(), editTextPass.getText().toString())) {

                    if (checkBoxRecord.isChecked())
                        ManageSettings.isRecord = true;
                    else
                        ManageSettings.isRecord = false;

                    Toast.makeText(myContext, "Start service", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(myContext, "Can't start service", Toast.LENGTH_LONG).show();
                }
            }
        });

        // stop
        Button buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myController.stopIsOK(editTextPass.getText().toString())) {

                    if (myController.broadCastMessage("stopService")) {
                        Toast.makeText(myContext, "Stop service", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(myContext, "Can't stop service", Toast.LENGTH_LONG).show();
                }
            }
        });

        // checkBox
        checkBoxRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (myController.unlock(editTextPass.getText().toString())) {
                      if (isChecked)
                          ManageSettings.isRecord = true;
                      else
                          ManageSettings.isRecord = false;
                  }
              }
          }

        );
    }

    @Override
    public void onPause() {

        myController.onPauseOK(editTextPhone.getText().toString(),
                editTextPass.getText().toString());

        super.onPause();
    }

    /**
     * Test si le service est deja lance
     *
     * @param serviceClass
     * @return
     */

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}