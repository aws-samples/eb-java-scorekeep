#!/bin/bash
cd scorekeep-worker
pip3 install requests -t bundle
cp requirements.txt scorekeep-worker.py bundle
cd bundle
zip -r ../../scorekeep-worker.zip *
cd ../
aws lambda update-function-code --function-name scorekeep-worker --zip-file fileb://../scorekeep-worker.zip
rm -r bundle