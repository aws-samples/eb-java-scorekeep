#!/bin/bash
# Replace the API domain name with your environment's domain to run the script against the application running in Elastic Beanstalk
API=localhost:5000/api

# for (( c=1; c<=5; c++ ))
for (( ; ; ))
do
  echo "Creating users, "
  USER1ID=$(curl --silent -X POST $API/user | jq -r .id)
  USER2ID=$(curl --silent -X POST $API/user | jq -r .id)
  echo "session, "
  SESSIONID=$(curl --silent -X POST $API/session | jq -r .id)
  echo "game, "
  GAMEID=$(curl --silent -X POST $API/game/$SESSIONID | jq -r .id)
  echo "configuring game, "
  curl --silent -X POST $API/game/$SESSIONID/$GAMEID/users -H "Content-Type: application/json" --data "[\"$USER1ID\",\"$USER2ID\"]"
  curl --silent -X PUT $API/game/$SESSIONID/$GAMEID/rules/TicTacToe
  curl --silent -X PUT $API/game/$SESSIONID/$GAMEID/name/tic-tac-toe-test
  EPOCH=$(date +%s)
  curl --silent -X PUT $API/game/$SESSIONID/$GAMEID/starttime/$EPOCH
  echo "playing game, "
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER1ID -H "Content-Type: text/plain" --data "X1" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER2ID -H "Content-Type: text/plain" --data "O2" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER1ID -H "Content-Type: text/plain" --data "X3" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER2ID -H "Content-Type: text/plain" --data "O4" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER1ID -H "Content-Type: text/plain" --data "X5" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER2ID -H "Content-Type: text/plain" --data "O6" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER1ID -H "Content-Type: text/plain" --data "X7" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER2ID -H "Content-Type: text/plain" --data "O8" >/dev/null
  curl --silent -X POST $API/move/$SESSIONID/$GAMEID/$USER1ID -H "Content-Type: text/plain" --data "X9" >/dev/null
  echo "ending game, "
  EPOCH=$(date +%s)
  curl --silent -X PUT $API/game/$SESSIONID/$GAMEID/endtime/$EPOCH
  echo "game complete."
  curl $API/game/$SESSIONID/$GAMEID
  # Cleanup
  echo "cleaning up"
  curl -X DELETE $API/game/$SESSIONID/$GAMEID
  curl -X DELETE $API/session/$SESSIONID
  curl -X DELETE $API/user/$USER1ID
  curl -X DELETE $API/user/$USER2ID
  echo ""
done
echo ""
