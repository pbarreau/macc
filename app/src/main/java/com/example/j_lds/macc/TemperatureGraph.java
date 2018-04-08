package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class TemperatureGraph extends AppCompatActivity {
    LineGraphSeries<DataPoint> series;
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("mm:ss");

    Button button_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_graph);

        getSupportActionBar().hide(); //Hides MyFirsApp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        button_BACK = (Button)findViewById(R.id.button_backHome);
        button_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backHome();
            }
        });

        GraphView graph = (GraphView)findViewById(R.id.Graph);
        series = new LineGraphSeries<DataPoint>(getDataPoint());
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            //@Override
         /*   public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    return sdf.format(new Date((long)value));
                }
                return super.formatLabel(value, isValueX);
            }*/
        });
        // graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        //possible de zomer sur le graphe
        graph.getViewport().setScalable(true);
        //set horizontally max and min
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(30);
    }
    //
    public DataPoint[] getDataPoint() {
        DataPoint[] dp = new DataPoint[]{
                new DataPoint(1,27),       //new DataPoint(new Date().getTime(),1),
                new DataPoint(2,22),       //new DataPoint(x,y),
                new DataPoint(4,25),
                new DataPoint(8,17)
        };
        return (dp);
    }

    public void backHome(){
        Intent intent;
        intent = new Intent(this, ClimHome.class);
        startActivity(intent);
    }

}
