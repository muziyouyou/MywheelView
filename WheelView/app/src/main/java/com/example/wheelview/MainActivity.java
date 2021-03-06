package com.example.wheelview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wangjie.androidinject.annotation.present.AIActivity;

import java.util.Arrays;

public class MainActivity extends AIActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] PLANETS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    private WheelView wva;

    private String mychoice=PLANETS[0];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        wva = (WheelView) findViewById(R.id.main_wv);
        //设置控件中显示条目个数,1就是为三个,3就是为7个
        wva.setOffset(1);
        //设置数据源
        wva.setItems(Arrays.asList(PLANETS));
        //设置监听
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mychoice=item;
            	Log.e(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });


        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), ""+mychoice,Toast.LENGTH_SHORT).show();
            }
        });
    }

  /*  public void onClickCallbackSample(View view) {
        switch (view.getId()) {
            case R.id.main_show_dialog_btn:
                View outerView = LayoutInflater.from(context).inflate(R.layout.wheel_view, null);
                WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(Arrays.asList(PLANETS));
                wv.setSeletion(3);
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        //Logger.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                    }
                });

                new AlertDialog.Builder(context)
                        .setTitle("WheelView in Dialog")
                        .setView(outerView)
                        .setPositiveButton("OK", null)
                        .show();

                break;
        }
    }*/
}
