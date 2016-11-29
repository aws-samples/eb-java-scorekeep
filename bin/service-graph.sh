EPOCH=$(date +%s)
echo "epoch is $EPOCH"
aws xray get-service-graph --start-time $(($EPOCH-600)) --end-time $EPOCH
