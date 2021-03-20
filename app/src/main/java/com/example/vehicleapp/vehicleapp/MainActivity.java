package com.example.vehicleapp.vehicleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    String [] vehicles;
    ArrayList<Vehicle> allVehicles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ListView vehicleList = findViewById(R.id.vehicleList);
        final HashMap<String, String> params = new HashMap<>();

        HttpURLConnection urlConnection; InputStream in = null; //making a http call
        try
        {
            URL url = new URL("http://10.0.2.2:8082/vehiclesdb/api"); //open the connection to the specified URL
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream()); //get the response from the server in an input stream
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String response = convertStreamToString(in); //convert the input stream to a string
        System.out.println("Server Response = " + response); //print the response to logcat

        try
        {
            //declare a new json array and pass it the string response from the server
            //this will convert the string into a JSON array which we can then iterate
            //over using a loop
            JSONArray jsonArray = new JSONArray(response);

            //instantiate the vehicles array and set the size to the amount of vehicle objects
            vehicles = new String[jsonArray.length()];

            //use a for loop to iterate over the JSON array
            for (int i = 0; i < jsonArray.length(); i++)
            {
                //the following line of code will get the name of the vehicle from the JSON object
                int vehicle_id = Integer.valueOf(jsonArray.getJSONObject(i).get("vehicle_id").toString());
                String make = jsonArray.getJSONObject(i).get("make").toString();
                String model = jsonArray.getJSONObject(i).get("model").toString();
                int year = Integer.valueOf(jsonArray.getJSONObject(i).get("year").toString());
                int price = Integer.valueOf(jsonArray.getJSONObject(i).get("price").toString());
                String license_number = jsonArray.getJSONObject(i).get("license_number").toString();
                String colour = jsonArray.getJSONObject(i).get("colour").toString();
                int number_doors = Integer.valueOf(jsonArray.getJSONObject(i).get("number_doors").toString());
                String transmission = jsonArray.getJSONObject(i).get("transmission").toString();
                int mileage = Integer.valueOf(jsonArray.getJSONObject(i).get("mileage").toString());
                String fuel_type = jsonArray.getJSONObject(i).get("fuel_type").toString();
                int engine_size = Integer.valueOf(jsonArray.getJSONObject(i).get("engine_size").toString());
                String body_style = jsonArray.getJSONObject(i).get("body_style").toString();
                String condition = jsonArray.getJSONObject(i).get("condition").toString();
                String notes = jsonArray.getJSONObject(i).get("notes").toString();

                // print to log cat
                System.out.println("ID = " + vehicle_id + " Make = " + make + " Model = " + model);

                Vehicle v = new Vehicle(vehicle_id, make, model, year, price, license_number, colour,
                        number_doors, transmission, mileage, fuel_type, engine_size, body_style, condition, notes);
                allVehicles.add(v);

                // add the name of the current vehicle to the vehicles array
                vehicles[i] = make + " " + model + " " + "(" + year + ")";
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, vehicles);
        vehicleList.setAdapter(arrayAdapter);
        vehicleList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(MainActivity.this, "You Selected " + allVehicles.get(i).getMake()
                        + " " + allVehicles.get(i).getModel()+ " " + "(" + allVehicles.get(i).getYear() + ")",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("vehicle", allVehicles.get(i));
                startActivity(intent); //launch the activity
            }
        });

        ImageButton vehicleAdd = findViewById(R.id.vehicleAdd);
        vehicleAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(intent); //launch the activity
            }
        });

        vehicleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l)
            {
                Toast.makeText(MainActivity.this, "Long Press On " + allVehicles.get(i).getMake()
                                + " " + allVehicles.get(i).getModel(), Toast.LENGTH_SHORT).show();
                final String vehicle_idS = String.valueOf(allVehicles.get(i).getVehicle_id());

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int option)
                    {
                        switch (option)
                        {
                            case DialogInterface.BUTTON_POSITIVE: //if yes is pressed
                                params.put("vehicle_id", vehicle_idS);
                                String url = "http://10.0.2.2:8082/vehiclesdb/api?vehicle_id=" + vehicle_idS;
                                performDeleteCall(url, params);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent); //refresh page
                                break;

                            case DialogInterface.BUTTON_NEGATIVE: //if no is pressed
                                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent2); //refresh page
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Delete Vehicle?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });
    }

    public String convertStreamToString(InputStream is)
    {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String performDeleteCall(String requestURL, HashMap<String, String> deleteDataParams)
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
            conn.setRequestMethod("DELETE");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //send DELETE data to connection using output stream & buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getDeleteDataString(deleteDataParams)); //send DELETE data key/value data to the server
            writer.flush(); //clear writer
            writer.close(); //close writer
            os.close(); //close output stream

            //get server response (success/error)
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code = " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                Toast.makeText(this, "Vehicle Deleted :)", Toast.LENGTH_LONG).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = br.readLine()) != null)
                {
                    response += line;
                }
            }
            else
                Toast.makeText(this, "Error! Failed to Delete Vehicle :(", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Response = " + response);
        return response;
    }

    //convert hashmap to URL query key/value pairs
    private String getDeleteDataString(HashMap<String, String> params) throws UnsupportedEncodingException
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