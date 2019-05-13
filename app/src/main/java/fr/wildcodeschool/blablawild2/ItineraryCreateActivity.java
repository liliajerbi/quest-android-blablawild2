package fr.wildcodeschool.blablawild2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ItineraryCreateActivity extends AppCompatActivity {

    Date mDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_create);

        this.setTitle(getString(R.string.add_your_itinerary));

        Button bAddItinerary = findViewById(R.id.b_add_itinerary);
        bAddItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etDeparture = findViewById(R.id.et_departure);
                EditText etDestination = findViewById(R.id.et_destination);
                EditText etDriver = findViewById(R.id.et_driver);
                EditText etPrice = findViewById(R.id.et_price);

                String departure = etDeparture.getText().toString();
                String destination = etDestination.getText().toString();
                String driver = etDriver.getText().toString();
                String price = etPrice.getText().toString();

                if (departure.isEmpty() || destination.isEmpty() || driver.isEmpty() || price.isEmpty()) {
                    Toast.makeText(ItineraryCreateActivity.this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    ItineraryModel itineraryModel = new ItineraryModel(departure, destination, driver, mDate, Integer.parseInt(price));
                    // TODO : save itineraryModel into Firebase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference itiniraryRef = database.getReference("itineraries");
                    String key = itiniraryRef.push().getKey();
                    itiniraryRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(ItineraryCreateActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(ItineraryCreateActivity.this, "Failed to read value", Toast.LENGTH_LONG).show();

                        }
                    });
                    itiniraryRef.child(key).setValue(itineraryModel);
                }
            }
        });

        final EditText etDate = findViewById(R.id.et_date);
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etDate.setText(sdf.format(calendar.getTime()));
                mDate = calendar.getTime();
            }
        };
        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ItineraryCreateActivity.this, datePicker, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
