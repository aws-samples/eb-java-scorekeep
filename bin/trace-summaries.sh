EPOCH=$(date +%s)
echo "epoch is $EPOCH"
aws xray get-trace-summaries --start-time $(($EPOCH-120)) --end-time $(($EPOCH-60))

