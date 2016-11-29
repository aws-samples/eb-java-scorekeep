EPOCH=$(date +%s)
echo "epoch is $EPOCH"
TRACEIDS=$(aws xray get-trace-summaries --start-time $(($EPOCH-120)) --end-time $(($EPOCH-60)) --query 'TraceSummaries[*].Id' --output text | cut -c 1-180)
aws xray batch-get-traces --trace-ids $TRACEIDS --query 'Traces[*]'
