SEGMENT=$(python bin/xray_start.py)
GRADLE_OUTPUT=$(gradle build); GRADLE_RETURN=$? 
if (( GRADLE_RETURN != 0 )); then 
  echo "Grade failed with exit status $GRADLE_RETURN" >&2 
  echo "and output: $GRADLE_OUTPUT" >&2
  python bin/xray_error.py "$SEGMENT" "$GRADLE_OUTPUT"
  exit 1
fi
python bin/xray_success.py "$SEGMENT"