package com.example.vehicleapp.vehicleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity
{
    Vehicle theVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        theVehicle = (Vehicle) extras.get("vehicle");

        TextView carText = findViewById(R.id.carText);
        TextView licenseNumberText = findViewById(R.id.licenseNumberText);
        TextView priceText = findViewById(R.id.priceText);
        TextView colourText = findViewById(R.id.colourText);
        TextView transmissionText = findViewById(R.id.transmissionText);
        TextView mileageText = findViewById(R.id.mileageText);
        TextView fuelTypeText = findViewById(R.id.fuelTypeText);
        TextView engineSizeText = findViewById(R.id.engineSizeText);
        TextView doorsText = findViewById(R.id.doorsText);
        TextView bodyText = findViewById(R.id.bodyText);
        TextView conditionText = findViewById(R.id.conditionText);
        TextView notesText = findViewById(R.id.notesText);
        ImageButton vehicleEdit = findViewById(R.id.vehicleEdit);

        carText.setText(theVehicle.getMake() + " " + theVehicle.getModel() + " " + "(" + theVehicle.getYear() + ")");
        licenseNumberText.setText(theVehicle.getLicense_number());
        priceText.setText("£" + theVehicle.getPrice());
        colourText.setText(theVehicle.getColour());
        transmissionText.setText(theVehicle.getTransmission());
        mileageText.setText("" + theVehicle.getMileage());
        fuelTypeText.setText(theVehicle.getFuel_type());
        engineSizeText.setText("" + theVehicle.getEngine_size());
        doorsText.setText("" + theVehicle.getNumber_doors());
        bodyText.setText(theVehicle.getBody_style());
        conditionText.setText(theVehicle.getCondition());
        notesText.setText(theVehicle.getNotes());

        vehicleEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("theVehicle", theVehicle);
                startActivity(intent); //launch the activity
            }
        });
    }
}
