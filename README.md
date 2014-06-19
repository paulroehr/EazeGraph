EazeGraph
=========

EazeGraph is an Android library for creating beautiful and fancy charts. Its main goal was to create a lighweight library which is easy to use and highly customizeable with an "up-to-date"-look.

Currently 4 different chart types are available, which can be viewed below.

__________
<b>IMPORTANT:</b>

This library is not intented for "mathematical" purposes like "achartengine" or "androidplot". It is intented to have a beautiful visual presentation of "user related"-data where only one value is provided and the rest is calculated dynamically.

So for example it's not possible to push 2D-values in the LineChart and let them plot just like in our beloved math lessons.

If you want such functionality either you use one of the libraries I named before or you wait some time until I finished a "mathematical plotting"-chart ;)

Currently LineCharts and BarCharts only support positive values. I will provide this functionality later.

<b>Your Android application should at least use Android API Level 14 in order to use this library!!!</b>

Chart types
===========

- Bar Chart

<img src="https://raw.github.com/blackfizz/EazeGraph/master/imagery/bar_chart.png" width="400px" height="300px" />


- Stacked Bar Chart

<img src="https://raw.github.com/blackfizz/EazeGraph/master/imagery/stacked_bar_chart.png" width="450px" height="300px" />

- Pie Chart

<img src="https://raw.github.com/blackfizz/EazeGraph/master/imagery/pie_chart.png" width="400px" height="420px" >


- Line Chart

<img src="https://raw.github.com/blackfizz/EazeGraph/master/imagery/line_chart.png" width="600px" height="320px" />
<img src="https://raw.github.com/blackfizz/EazeGraph/master/imagery/cubic_line_chart.png" width="600px" height="320px" />


Features
========

- 4 different chart types
- dynamic legend label generation
- possibility to use your own legend labels 
- animations for every chart
- touch interaction for PieChart and LineChart
- various xml attributes for customizing the charts
- and much more


Examples
========

Examples on how to correctly use these charts are either below or you can view the source of the sample app I provided.

If you want to see the library in action, just download the sample APK from the PlayStore:


Including in your project
=========================

Insert in your root project's 'build.gradle' under repositories:

    repositories {
        maven {
            url 'https://oss.sonatype.org/content/groups/public'
        }
    }

and in your android app project folder in the 'build.gradle' under dependencies:

    dependencies {
        compile 'com.github.blackfizz:eazegraph:1.0.0-SNAPSHOT'
    }

That's it. now you are ready to use the library!

Usage
=====

Wiki
====

Changelog
=========

**1.0.0**
* initial commit of this library

Contributing
=============

I would love to see people contributing to this project. So just go ahead. If you think you did something amazing and your feature should be implemented in this library, make a pull request! Do not hesitate.

Credit
======

This library was developed for the official YAZIO Android app:

https://play.google.com/store/apps/details?id=com.yazio.android

https://play.google.com/store/apps/details?id=com.yazio.android.pro

License
=======

    Copyright (C) 2014 Paul Cech

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[5]: https://raw.github.com/blackfizz/EazeGraph/master/imagery/bar_chart.png
[6]: https://raw.github.com/blackfizz/EazeGraph/master/imagery/stacked_bar_chart.png
[7]: https://raw.github.com/blackfizz/EazeGraph/master/imagery/pie_chart.png
[8]: https://raw.github.com/blackfizz/EazeGraph/master/imagery/line_chart.png
[9]: https://raw.github.com/blackfizz/EazeGraph/master/imagery/cubic_line_chart.png
