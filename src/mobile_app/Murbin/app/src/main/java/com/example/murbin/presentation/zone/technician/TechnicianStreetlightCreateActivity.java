package com.example.murbin.presentation.zone.technician;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.example.murbin.App;
import com.example.murbin.BaseActivity;
import com.example.murbin.R;
import com.example.murbin.data.StreetlightsDatabaseCrud;
import com.example.murbin.firebase.Auth;
import com.example.murbin.models.GeoPoint;
import com.example.murbin.models.Streetlight;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TechnicianStreetlightCreateActivity extends BaseActivity implements View.OnClickListener {

    private final Auth mAuth = new Auth(this);

    private ViewGroup mContainer;
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;
    private String mMessage;
    private Button m_btn_cancel, m_btn_save, m_btn_location;
    private StreetlightsDatabaseCrud mStreetlightsDatabaseCrud;
    private GeoPoint location;
    private Streetlight mStreetlight;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(App.DEFAULT_TAG, "TechnicianStreetlightCreateActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technician_streetlight_create_formulary);
        initializeLayoutElements();
    }

    /**
     * Method to start the layout elements
     */
    private void initializeLayoutElements() {

        mContainer = findViewById(R.id.technician_streetlight_create_container);

        m_btn_cancel = findViewById(R.id.technician_streetlight_create_btn_cancelar);
        m_btn_save = findViewById(R.id.technician_streetlight_create_btn_crear);
        m_btn_location = findViewById(R.id.technician_streetlight_create_btn_location);

        m_btn_cancel.setOnClickListener(this);
        m_btn_save.setOnClickListener(this);
        m_btn_location.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                mMessage = extras.getString("message", "");
                if (mMessage != null && !mMessage.isEmpty()) {
                    App.getInstance().snackMessage(mContainer, R.color.black, mMessage, this);
                }
            }
            if (extras.containsKey("id")) {
                mId = extras.getString("id", "");
//                Log.d("mId", mId);
            }
        }

        mStreetlightsDatabaseCrud = new StreetlightsDatabaseCrud(mId);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.technician_streetlight_create_btn_location: {
//                Log.d(App.DEFAULT_TAG, "Pulsado: " + id);
                showMapDialogFragment("location", "TechnicianStreetlightCreateActivity");
                break;
            }

            case R.id.technician_streetlight_create_btn_cancelar: {
                finish();
                break;
            }
            case R.id.technician_streetlight_create_btn_crear: {
                if (checkForm()) {
                    mStreetlight = new Streetlight(this.location, true);
                    mStreetlightsDatabaseCrud.create(mStreetlight, documentId -> {
                        if (!documentId.isEmpty()) {
                            Intent intent = new Intent(TechnicianStreetlightCreateActivity.this, TechnicianStreetlightsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("message", "Farola creada correctamente.");
                            startActivity(intent);
                        } else {
                            App.getInstance().snackMessage(
                                    mContainer,
                                    R.color.black,
                                    "Error desconocido al crear la farola.",
                                    TechnicianStreetlightCreateActivity.this
                            );
                        }
                    });
                }

                break;
            }
        }
    }

    /**
     * Check if form is correctly
     *
     * @return boolean
     */
    private boolean checkForm() {
        boolean result = true;

        return result;
    }

    /**
     * Sets the location of the Streetlight with the received Geoppoint as a parameter.
     *
     * @param location Geopoint
     */
    public void setLocationStreetlight(GeoPoint location) {
        this.location = location;
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            m_btn_location.setHint(location.getLatitude() + "" + location.getLongitude());
        } else {
            m_btn_location.setHint(R.string.global_string_location);
        }
    }
}
