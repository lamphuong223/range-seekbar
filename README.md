RangeSeekBar
===============

A slider widget for Android allowing to set a minimum and maximum value on a numerical range.

![](https://raw.githubusercontent.com/lamphuong223/range-seekbar/master/screenshot/s1.png)

Download
------
- Add it in your root `build.gradle` at the end of repositories:
```groovy
allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```
- Add the dependency in your module-level `build.gradle`
```groovy
dependencies {
    compile 'com.github.lamphuong223:range-seekbar:1.0.1'
}
```
Usage
-----
### Layout XML
```xml
<com.lpphan.rangeseekbar.RangeSeekBar
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:tick_count="20"
    app:thumb_color="@color/colorAccent"
    app:thumb_normal_radius="6dp"
    app:thumb_pressed_radius="8dp"/>
```
### Java code
```java
RangeSeekBar seekBar = (RangeSeekBar) findViewById(R.id.seekBar);
seekBar.setTickCount(20);
seekBar.setThumbColor(Color.RED);
seekBar.setThumbNormalRadius(12);
seekBar.setThumbPressedRadius(16);
seekBar.setLeftIndex(0);
seekBar.setRightIndex(10);
```
Initial the `RangeSeekBar` and setup `OnRangeSeekBarChangerListener`
```java
seekBar.setOnRangeBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangerListener() {
    @Override
    public void onIndexChange(RangeSeekBar rangeBar, int leftIndex, int rightIndex) {
        Log.d(TAG, "leftIndex: " + leftIndex);
        Log.d(TAG, "rightIndex: " + rightIndex);
    }
});
```
### XML attributes 
            tick_count              format = interger   
            thumb_normal_radius     format=  dimen
            thumb_pressed_radius    format=  dimen
            left_index              format=  interger
            right_index             format=  interger
            thumb_color             format=  color
License
-------

    Copyright 2016 lamphuong223

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
