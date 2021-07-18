package com.example.scores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectActivity extends AppCompatActivity {
    private static AsynNet dct = null;
    private String cookie=null;
    private Spinner spinner1 =null;
    private Spinner spinner2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        cookie=getIntent().getStringExtra("cookie");
    }
    public void click2(View view) {
        spinner1=findViewById(R.id.spin_one);
        spinner2=findViewById(R.id.spin_two);
        String year=(String) spinner1.getSelectedItem();
        String term=(String) spinner2.getSelectedItem();
        year=year.substring(0,4);
        if(term.equals("第一学期"))
            term="0";
        else
            term="1";
        dct = new AsynNet();
        AsynNet.Search(year,term,cookie,new AsynNet.Callback() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(SelectActivity.this,TableActivity.class);
                intent.putExtra("data",response);
                startActivity(intent);
            }
        });

    }

}
