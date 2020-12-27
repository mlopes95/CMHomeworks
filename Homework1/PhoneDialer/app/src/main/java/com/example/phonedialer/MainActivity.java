package com.example.phonedialer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button btnSpeedOne;
    Button btnSpeedTwo;
    Button btnSpeedThree;
    FloatingActionButton btnErase;

    public static final int GET_ONE = 1;
    public static final int GET_TWO = 2;
    public static final int GET_THREE = 3;

    private String SPOneNumb;
    private String SPTwoNumb;
    private String SPThreeNumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btnErase = findViewById(R.id.buttonErase);
        btnSpeedOne = findViewById(R.id.speed1);
        btnSpeedTwo = findViewById(R.id.speed2);
        btnSpeedThree = findViewById(R.id.speed3);

        onClickBtnSpeedListener();

    }


    public void btnClick(View view){
        Button btn = (Button) view;
        String val = btn.getText().toString();
        onButtonClick(editText, val);
    }

    public void onDial(View view){
        if(editText.getText().length() <= 3){
            Toast.makeText(this, "Please enter valid number", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            String hash = editText.getText().toString();
            if(hash.contains("#")){
                hash.replace("#", "%23");
            }
            intent.setData(Uri.parse("tel:" + hash));

            startActivity(intent);
        }

    }
    public void onErase(View view){
        if(editText.getText().toString().length() > 0){
            editText.setText(editText.getText().toString().substring(0, editText.getText().length()-1));
        }
    }

    public void onButtonClick(EditText eText, String text){
        String currentString = editText.getText().toString();
        if(currentString.equals("Enter the number")){
            editText.setText("");
            currentString = editText.getText().toString();
        }
        eText.setText(currentString + text);
    }

    public void onClickBtnSpeedListener(){

        Intent intent = new Intent(this, SecondActivity.class);

        btnSpeedOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(SPOneNumb);
            }
        });

        btnSpeedOne.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivityForResult(intent, GET_ONE);
                return true;
            }
        });

        btnSpeedTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(SPTwoNumb);
            }
        });

        btnSpeedTwo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivityForResult(intent, GET_TWO);
                return true;
            }
        });


        btnSpeedThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(SPThreeNumb);
            }
        });

        btnSpeedThree.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivityForResult(intent, GET_THREE);
                return true;
            }
        });

        btnErase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editText.setText("");
                return true;
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_ONE) {
            if(resultCode == RESULT_OK) {
                String[] strEditText = data.getStringArrayExtra("editTextValues");
                if(strEditText[0].equals("")){
                    btnSpeedOne.setText(getString(R.string.speed1));
                } else {
                    btnSpeedOne.setText(strEditText[0]);
                }
                SPOneNumb = strEditText[1];
            }
        }
        if (requestCode == GET_TWO) {
            if(resultCode == RESULT_OK) {
                String[] strEditText = data.getStringArrayExtra("editTextValues");
                if(strEditText[0].equals("")){
                    btnSpeedTwo.setText(getString(R.string.speed2));
                } else {
                    btnSpeedTwo.setText(strEditText[0]);
                }
                SPTwoNumb = strEditText[1];
            }
        }
        if (requestCode == GET_THREE) {
            if(resultCode == RESULT_OK) {
                String[] strEditText = data.getStringArrayExtra("editTextValues");
                if(strEditText[0].equals("")){
                    btnSpeedThree.setText(getString(R.string.speed3));
                } else {
                    btnSpeedThree.setText(strEditText[0]);
                }
                SPThreeNumb = strEditText[1];
            }
        }
    }
}