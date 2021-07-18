package com.example.scores;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import androidx.appcompat.app.AppCompatActivity;



public class TableActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade);
        TextView test=findViewById(R.id.test);
        String data=getIntent().getStringExtra("data");

        Document doc = Jsoup.parse(data);

        Elements title = doc.select("table").eq(0).select("tr");
        String result = title.text();
        result+="\n";
        result+=doc.select("table").eq(2).select("tr").eq(0).select("td").eq(0).text();

        Elements trs = doc.select("table").eq(2).select("tr");
        for(int i = 0;i<trs.size();i++){
            Elements tds = trs.get(i).select("td").eq(1);
            //tds.addAll(trs.get(i).select("td").eq(6));
            Elements tds2=trs.get(i).select("td").eq(6);
            for(int j = 0;j<tds.size();j++){
                String text = tds.get(j).text();//course
                text=text.substring(text.indexOf("]")+1,text.length());
                String grade=tds2.get(j).text();//grade
                result+="\n";
                result+=text;
                result+="  成绩 : ";
                result+=grade;
                result+="\n";
            }
        }
        test.setText(result);

    }
}
