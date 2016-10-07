
# PolarChart
A simple Polar Chart for Android (minSdkVersion=15).

## Demo
<img src="https://raw.githubusercontent.com/aurelhubert/polarchart/master/demo1.gif" width="208" height="368" />

## Features
* Display your data as a Polar Chart
* Animate the changes between data
* Customize the number of sections and circles
* Customize all the paints (circles, sections, data shape)

## How to?

### Gradle
```groovy
dependencies {
    compile 'com.aurelhubert:polarchart:0.1.0'
}
```
### XML
```xml
<com.aurelhubert.polarchart.PolarChart
        android:id="@+id/polar_chart"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:nb_circles="8"
        app:nb_sections="12"/>
```

### Activity/Fragment
```java
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
```

## Contributions
Feel free to create issues / pull requests.

## License
```
PolarChart library for Android
Copyright (c) 2016 Aurelien Hubert (http://github.com/aurelhubert).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
