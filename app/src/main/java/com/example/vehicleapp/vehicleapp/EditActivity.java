package com.example.vehicleapp.vehicleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class EditActivity extends AppCompatActivity
{
    Vehicle theVehicle;
    EditText idEdit;
    EditText makeEdit;
    EditText modelEdit;
    EditText yearEdit;
    EditText licenseNumberEdit;
    EditText priceEdit;
    EditText colourEdit;
    EditText transmissionEdit;
    EditText mileageEdit;
    EditText fuelTypeEdit;
    EditText engineSizeEdit;
    EditText bodyStyleEdit;
    EditText doorsEdit;
    EditText conditionEdit;
    EditText notesEdit;
    Button vehicleEditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle extras = getIntent().getExtras();
        theVehicle = (Vehicle) extras.get("theVehicle");

        idEdit = findViewById(R.id.idEdit);
        makeEdit = findViewById(R.id.makeEdit);
        modelEdit = findViewById(R.id.modelEdit);
        yearEdit = findViewById(R.id.yearEdit);
        licenseNumberEdit = findViewById(R.id.licenseNumberEdit);
        priceEdit = findViewById(R.id.priceEdit);
        colourEdit = findViewById(R.id.colourEdit);
        transmissionEdit = findViewById(R.id.transmissionEdit);
        mileageEdit = findViewById(R.id.mileageEdit);
        fuelTypeEdit = findViewById(R.id.fuelTypeEdit);
        engineSizeEdit = findViewById(R.id.engineSizeEdit);
        bodyStyleEdit = findViewById(R.id.bodyStyleEdit);
        doorsEdit = findViewById(R.id.doorsEdit);
        conditionEdit = findViewById(R.id.conditionEdit);
        notesEdit = findViewById(R.id.notesEdit);
        Button vehicleEditBtn = findViewById(R.id.vehicleEditBtn);

        idEdit.setText("" + theVehicle.getVehicle_id());
        makeEdit.setText(theVehicle.getMake());
        modelEdit.setText(theVehicle.getModel());
        yearEdit.setText("" + theVehicle.getYear());
        licenseNumberEdit.setText(theVehicle.getLicense_number());
        priceEdit.setText("" + theVehicle.getPrice());
        colourEdit.setText(theVehicle.getColour());
        transmissionEdit.setText(theVehicle.getTransmission());
        mileageEdit.setText("" + theVehicle.getMileage());
        fuelTypeEdit.setText(theVehicle.getFuel_type());
        engineSizeEdit.setText("" + theVehicle.getEngine_size());
        bodyStyleEdit.setText(theVehicle.getBody_style());
        doorsEdit.setText("" + theVehicle.getNumber_doors());
        conditionEdit.setText(theVehicle.getCondition());
        notesEdit.setText(theVehicle.getNotes());

        final HashMap<String, String> params = new HashMap<>();

        vehicleEditBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Gson gson = new Gson();
                int vehicle_id = Integer.valueOf(idEdit.getText().toString());
                String make = makeEdit.getText().toString();
                String model = modelEdit.getText().toString();
                int year = Integer.valueOf(yearEdit.getText().toString());
                String license_number = licenseNumberEdit.getText().toString();
                int price = Integer.valueOf(priceEdit.getText().toString());
                String colour = colourEdit.getText().toString();
                String transmission = transmissionEdit.getText().toString();
                int mileage = Integer.valueOf(mileageEdit.getText().toString());
                String fuel_type = fuelTypeEdit.getText().toString();
                int engine_size = Integer.valueOf(engineSizeEdit.getText().toString());
                String body_style = bodyStyleEdit.getText().toString();
                int number_doors = Integer.valueOf(doorsEdit.getText().toString());
                String condition = conditionEdit.getText().toString();
                String notes = notesEdit.getText().toString();

                Vehicle v = new Vehicle(vehicle_id, make, model, year, price, license_number, colour,
                        number_doors, transmission, mileage, fuel_type, engine_size, body_style, condition, notes);

                String vehicleJson = gson.toJson(v);
                System.out.println(vehicleJson);
                params.put("json", vehicleJson);
                params.put("vehicle_id", idEdit.getText().toString());
                String url = "http://10.0.2.2:8006/vehiclesdb/api";
                performPutCall(url, params);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); //refresh page
            }
        });
    }

    public String performPutCall(String requestURL, HashMap<String, String> putDataParams)
    {
        URL url;
        String response = "";
        try
        {
            url = new URL(requestURL);
            //create connection object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //send PUT data to connection using output stream & buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPutDataString(putDataParams)); //send PUT data key/value data to the server
            writer.flush(); //clear writer
            writer.close(); //close writer
            os.close(); //close output stream

            //get server response (success/error)
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code = " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                Toast.makeText(this, "Vehicle Updated :)", Toast.LENGTH_LONG).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = br.readLine()) != null)
                {
                    response += line;
                }
            }
            else
                Toast.makeText(this, "Error! Failed to Update Vehicle :(", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Response = " + response);
        return response;
    }

    //convert hashmap to URL query key/value pairs
    private String getPutDataString(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
