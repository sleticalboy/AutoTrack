#!/usr/bin/env sh

./gradlew :javadoc-processor:publishToMavenLocal --console=rich
#./gradlew :PluginTransform:uploadArchives --console=rich
#./gradlew clean --console=rich
find . -name build | xargs rm -rfv
./gradlew :TransformDemo:assembleDebug --console=rich

# launch app
adb shell am start -n \
"com.sleticalboy.transform/com.sleticalboy.transform.MainActivity" \
-a android.intent.action.MAIN -c android.intent.category.LAUNCHER
