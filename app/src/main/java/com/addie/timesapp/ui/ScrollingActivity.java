package com.addie.timesapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.addie.timesapp.R;
import com.addie.timesapp.adapter.ListViewAdapter;
import com.addie.timesapp.data.AppUsage;
import com.addie.timesapp.data.DbHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ScrollingActivity extends AppCompatActivity {

    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelNames;
    ArrayList<BarChartData> timeHourArrayList = new ArrayList<>();
    DbHandler dbhandle;
    List<AppUsage> applist, topfive;
    String usageTime;

    ListView listView;
  //  String mTitle[] = {"facebook","Whatsapp","Twitter", "Instargram", "Youtube"};
  //  String mUsage[] = {"Usage Time: 1 Hour 12 Minutes","Usage Time: 55 Minutes","Usage Time: 30 Minutes", "Usage Time: 20 Minutes", "Usage Time: 15 Minutes"};
    String mVisits[] = {"Visits: 19","Visits: 8","Visits: 20","Visits: 17","Visits: 14"};
    //int images[] = {R.drawable.facebook,R.drawable.whatsapp,R.drawable.twitter,R.drawable.instargram,R.drawable.youtube};
    String mTitle[] = new String[5];
    String mUsage[] = new String[5];
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        dbhandle = new DbHandler(this);
        applist = dbhandle.getWeek();
        topfive = dbhandle.getTop();


        Date date = new Date();

        if(applist.get(applist.size()-1).getDate().equals(date)){
            usageTime = Integer.toString(applist.get(applist.size()-1).getUsage());
        }else{
            usageTime ="";
        }
        TextView text = (TextView)findViewById(R.id.usage_time);
        text.setText(usageTime);
        displayBarGraph(applist);
        HashMap<String, Integer> totalUsageData = new HashMap<String, Integer>();

        String usage;
        for (int i=0; i< topfive.size(); i++){
            if(topfive.get(i).getUsage() >= 3600000){
                usage = "Usage: "+ topfive.get(i).getUsage() / 3600000 +"H" + topfive.get(i).getUsage()%3600000 + "min" ;
            }
            else if (60000 <topfive.get(i).getUsage() && topfive.get(i).getUsage() < 3600000){
                usage = "Usage: "+ topfive.get(i).getUsage() / 60000 +"min";
            }
            else{
                usage = "Usage: ";
            }

            mTitle[i]=(topfive.get(i).getApp_name());
            mUsage[i]= usage;
        }


        listView = findViewById(R.id.appListView);
        ListViewAdapter adapter = new ListViewAdapter(ScrollingActivity.this, mTitle, mUsage,mVisits, topfive);
        listView.setAdapter(adapter);

    }

    public void displayBarGraph(List<AppUsage> list){
        barChart = findViewById(R.id.bargraph);
        barEntryArrayList = new ArrayList<>();
        labelNames = new ArrayList<>();
        BarChartData barChartData1= new BarChartData(3,"1h");
        timeHourArrayList = barChartData1.fillData(list);


        barEntryArrayList.clear();
        labelNames.clear();

        for (int i=0; i< timeHourArrayList.size(); i++){
            int time = timeHourArrayList.get(i).getTime();
            String hour = timeHourArrayList.get(i).getDay();
            barEntryArrayList.add(new BarEntry(i,time));
            labelNames.add(hour);
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Usage time");
        barDataSet.setColors(Color.rgb(27,227,40));
        barDataSet.setDrawValues(false);

        Description description = new Description();
        description.setText("Day");

        barChart.setDescription(description);
        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(false);
        barChart.getAxisLeft().setAxisMaximum(24);

        BarData barData = new BarData(barDataSet);
      //  barData.setBarWidth(0.5f);
        barChart.setData((barData));


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
        YAxis yAxis = barChart.getAxisRight();
        yAxis.setEnabled(false);
        barChart.animateY(500);
        barChart.invalidate();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_apps_action_settings:
                startActivity(new Intent(ScrollingActivity.this, SettingsActivity.class));
                break;
        }

        return true;
    }

}