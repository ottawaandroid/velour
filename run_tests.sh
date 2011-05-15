#1/bin/sh
cd tests
ant install && \
adb shell am instrument -w ca.ottawaandroid.velour.tests/android.test.InstrumentationTestRunner
