#!/usr/bin/env sh

./gradlew :PluginTransform:uploadArchives --console=rich
./gradlew clean --console=rich
./gradlew :TransformDemo:assembleDebug --console=rich

# launch app
adb shell am start -n \
"com.sleticalboy.transform/com.sleticalboy.transform.MainActivity" \
-a android.intent.action.MAIN -c android.intent.category.LAUNCHER
