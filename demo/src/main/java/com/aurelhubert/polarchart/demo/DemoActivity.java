package com.aurelhubert.polarchart.demo;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aurelhubert.polarchart.PolarChart;

import java.util.ArrayList;

/**
 *
 */

public class DemoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

		initUI();
	}

	/**
	 * Init UI
	 */
	private void initUI() {

		final PolarChart polarChart = (PolarChart) findViewById(R.id.polar_chart);

		// Number of sections
		polarChart.setNbSections(8);
		// Number of circles
		polarChart.setNbCircles(5);

		// Set data
		final ArrayList<Float> values = new ArrayList<>();
		values.add(4f);
		values.add(3f);
		values.add(5f);
		values.add(2.3f);
		values.add(4.3f);
		values.add(3.3f);
		values.add(2.4f);
		values.add(1.2f);

		// Set the values with animation (or not)
		polarChart.setSectionsValue(values, true);

		// Use Bezier curve or classic path
		polarChart.setUseBezierCurve(true);

		// Define custom Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.parseColor("#2196F3"));
		polarChart.setShapePaint(paint);

		// Activate onTouchListener and add valueChanged listener
		polarChart.setPolarChartListener(new PolarChart.PolarChartListener() {
			@Override
			public void onValueChanged(int section, float value) {
				Log.d("PolarChart", "onValueChanged: " + section + " / " + value);
			}
		});

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				values.clear();
				for (int i = 0; i < polarChart.getNbSections(); i++) {
					values.add((float) (Math.random() * polarChart.getNbCircles() - 1) + 1);
				}
				polarChart.setSectionsValue(values, true);
				handler.postDelayed(this, 3000);
			}
		}, 3000);
	}
}
