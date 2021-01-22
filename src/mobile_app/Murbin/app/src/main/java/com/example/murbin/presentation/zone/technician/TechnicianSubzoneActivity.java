package com.example.murbin.presentation.zone.technician;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.murbin.App;
import com.example.murbin.BaseActivity;
import com.example.murbin.R;
import com.example.murbin.comun.Mqtt;
import com.example.murbin.firebase.Auth;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TechnicianSubzoneActivity extends BaseActivity implements View.OnClickListener {

    public static MqttClient client = null;
    private final Auth mAuth = new Auth(this);

    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;
    private ViewGroup mContainer;
    private TextView mTitle, mCount;
    private ConstraintLayout mStreetlights;
    private String mMessage, mIdSubzone;
    private ConstraintLayout botonOnOff;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(App.DEFAULT_TAG, "TechnicianSubzoneActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technician_subzone_main);
        initializeLayoutElements();
    }

    /**
     * Method to start the layout elements
     */
    private void initializeLayoutElements() {
        // Toolbar menu
        mToolbar = findViewById(R.id.technician_subzone_activity_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setTitle("");
        }

        mTitle = findViewById(R.id.technician_subzone_activity_tv_title);
        mContainer = findViewById(R.id.technician_subzone_activity_container);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                mMessage = extras.getString("message", "");
                if (mMessage != null && !mMessage.isEmpty()) {
                    App.getInstance().snackMessage(mContainer, R.color.black, mMessage, this);
                }
            }
            if (extras.containsKey("idSubzone")) {
                mIdSubzone = extras.getString("idSubzone", "");
                mTitle.setText(mIdSubzone);
                Log.d("extra mIdSubzone", mIdSubzone);
            }
        }

        // MQTT
        try {
//            Log.d(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
            client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                    new MemoryPersistence());
            client.connect();
        } catch (MqttException e) {
//            Log.d(Mqtt.TAG, "Error al conectar.", e);
        }

        botonOnOff = findViewById(R.id.technician_subzone_status_container);
        botonOnOff.setOnClickListener(this);
        mStreetlights = findViewById(R.id.technician_subzone_streetlights_container);
        mStreetlights.setOnClickListener(this);

        Task<QuerySnapshot> query = FirebaseFirestore.getInstance().collection("zones")
                .document("Gandia").collection("subzones")
                .document(mIdSubzone).collection("streetlights").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            count++;
                        }
                        mCount = findViewById(R.id.technician_subzone_streetlights);
                        mCount.setText(String.valueOf(count));
                    }
                });
        query.isSuccessful();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.technician_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        try {
//            Log.d(Mqtt.TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
//            Log.d(Mqtt.TAG, "Error al desconectar.", e);
        }
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.technician_subzone_status_container: {
                try {
//                    Log.d(App.DEFAULT_TAG, "Pulsado technician_subzone_status_container");
//                Log.d(Mqtt.TAG, "Cambiar estado de la farola: " + "TOGGLE");
                    MqttMessage message = new MqttMessage("TOGGLE".getBytes());
                    message.setQos(Mqtt.qos);
                    message.setRetained(false);
                    client.publish(Mqtt.topicRoot + "TOGGLE", message);
                } catch (MqttException e) {
//                Log.d(Mqtt.TAG, "Error al publicar.", e);
                }

                break;
            }
            case R.id.technician_subzone_streetlights_container: {
                Log.d("mIdSubzone", mIdSubzone);
                Intent intent = new Intent(this, TechnicianStreetlightsActivity.class);
                intent.putExtra("idSubzone", mIdSubzone);
                startActivity(intent);

                break;
            }
        }
    }
}
