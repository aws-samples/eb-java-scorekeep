#!/bin/bash
curl -sLO https://github.com/aws/aws-xray-java-agent/releases/latest/download/xray-agent.zip
unzip -qof xray-agent.zip
rm -f xray-agent.zip
git add disco/  # so it'll appear in git ls-files
