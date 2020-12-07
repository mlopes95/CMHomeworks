package com.example.phonedialer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SecondActivity extends AppCompatActivity {

    EditText label;
    EditText number;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

    label = findViewById(R.id.editTextLabel);
    number = findViewById(R.id.editTextNumb);
    save = findViewById(R.id.save);

    }

    public void save(View view){
       Intent replyIntent = new Intent();
       String[] values = {"", ""};
       if(label.getText().toString() != "" && number.getText().toString() != ""){
           values = new String[]{label.getText().toString(), number.getText().toString()};
       } else {
           values = new String[]{"", ""};
       }

       replyIntent.putExtra("editTextValues", values);
       setResult(RESULT_OK, replyIntent);
       finish();
    }


}