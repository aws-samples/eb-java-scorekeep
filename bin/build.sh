SEGMENT=$(python bin/xray_start.py)
./gradlew build --quiet --stacktrace &> gradle.log; GRADLE_RETURN=$?
if (( GRADLE_RETURN != 0 )); then 
  echo "Gradle failed with exit status $GRADLE_RETURN" >&2
  python bin/xray_error.py "$SEGMENT" "$(cat gradle.log)"
  exit 1
fi
python bin/xray_success.py "$SEGMENT"
