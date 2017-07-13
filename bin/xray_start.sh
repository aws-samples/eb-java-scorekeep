START_TIME=$(date +%s)
HEX_TIME=$(printf '%x\n' $START_TIME)
GUID=$(dd if=/dev/random bs=12 count=1 2>/dev/null | od -An -tx1 | tr -d ' \t\n')
TRACE_ID="1-$HEX_TIME-$GUID"
SEGMENT_ID=$(dd if=/dev/random bs=8 count=1 2>/dev/null | od -An -tx1 | tr -d ' \t\n')
SEGMENT_DOC="{\"trace_id\": \"$TRACE_ID\", \"id\": \"$SEGMENT_ID\", \"start_time\": $START_TIME, \"in_progress\": true, \"name\": \"Scorekeep-build\"}"
HEADER='{"format": "json", "version": 1}'
TRACE_DATA="$HEADER\n$SEGMENT_DOC"
echo "$HEADER" > document.txt
echo "$SEGMENT_DOC" >> document.txt
UDP_IP="127.0.0.1"
UDP_PORT=2000
cat document.txt > /dev/udp/$UDP_IP/$UDP_PORT
