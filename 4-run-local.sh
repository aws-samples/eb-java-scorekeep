#!/bin/bash
set -eo pipefail
export AWS_REGION=$(aws configure get region)
export TOPICS=$(aws sns list-topics --output text | grep scorekeep-sns-topic)
export NOTIFICATION_TOPIC=$(echo $TOPICS | cut -d' ' -f 2)
export GAME_TABLE=scorekeep-game
export MOVE_TABLE=scorekeep-move
export SESSION_TABLE=scorekeep-session
export STATE_TABLE=scorekeep-state
export USER_TABLE=scorekeep-user

./gradlew bootrun