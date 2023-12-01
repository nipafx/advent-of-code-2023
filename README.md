# Advent of Code 2023

(Hopefully) Elegant Java solutions for [Advent of Code 2023](https://adventofcode.com/2023), probably with a tendency to overuse new language features and APIs. 😊
I'm using [JDK 22](https://jdk.java.net/22/) and plan to put each solution into a single file, so they can be executed with:

```
java -ea --enable-preview --source 22 src/DayXPartY.java
```

You can also run all of them in one go after setting `JAVA_22_LAUNCHER` in [`All.java`](src/All.java):

```
java src/All.java
```

The solution files contain the demo inputs as strings and assert the correct results for them.
As stated in the Advent of Code FAQ, [copies of the full input are not allowed](https://adventofcode.com/2023/about#faq_copying), and so these files do not contain them, nor the correct solutions.
