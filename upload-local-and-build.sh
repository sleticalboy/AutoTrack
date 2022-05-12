#!/usr/bin/env sh

./gradlew :PluginTransform:uploadArchives
./gradlew clean
./gradlew :TransformDemo:assembleDebug

# launch app
adb shell am start -n \
"com.sleticalboy.transform/com.sleticalboy.transform.MainActivity" \
-a android.intent.action.MAIN -c android.intent.category.LAUNCHER
