import os
import boto3
import json
import requests
import time
from aws_xray_sdk.core import xray_recorder
from aws_xray_sdk.core import patch_all

patch_all()
queue_url = os.environ['WORKER_QUEUE']

def lambda_handler(event, context):
    # Create SQS client
    sqs = boto3.client('sqs')
    s3client = boto3.client('s3')

    # Receive message from SQS queue
    response = sqs.receive_message(
        QueueUrl=queue_url,
        AttributeNames=[
            'SentTimestamp'
        ],
        MaxNumberOfMessages=1,
        MessageAttributeNames=[
            'All'
        ],
        VisibilityTimeout=0,
        WaitTimeSeconds=0
    )
    print(json.dumps(response))
    message = response['Messages'][0]
    receipt_handle = message['ReceiptHandle']

    print("Item pulled: " + message["Body"])
    body = json.loads(message["Body"])
    attributes = message["Attributes"]
    timestamp = attributes["SentTimestamp"]
    date = time.strftime('%Y/%m/%d', time.gmtime(float(timestamp)/1000.))
    endpoint = body["endpoint"]
    gameid = body["gameid"]
    sessionid = body["sessionid"]
    bucket_name = body["bucketname"]

    apiurl = "http://" + endpoint + "/api/"
    r = requests.get( apiurl + "game/" + sessionid + "/" + gameid )
    game = r.json()
    print("Game (original): " + json.dumps(game))

    # deref session
    r = requests.get( apiurl + "session/" + sessionid)
    session = r.json()
    game["session"] = session

    # deref users
    users = []
    for userid in game["users"]:
      r = requests.get( apiurl + "user/" + userid)
      user = r.json()
      users.append(user)
    game["users"] = users

    # deref states
    states = []
    for stateid in game["states"]:
      r = requests.get( apiurl + "state/" + sessionid + "/" + gameid + "/" + stateid)
      user = r.json()
      states.append(user)
    game["states"] = states

    # deref moves
    moves = []
    for moveid in game["moves"]:
      r = requests.get( apiurl + "move/" + sessionid + "/" + gameid + "/" + moveid)
      user = r.json()
      moves.append(user)
    game["moves"] = moves
    #print("Game (updated): " + json.dumps(game))
    print("Game (updated): " + json.dumps(game, sort_keys=True, indent=2))

    # write to s3
    key = date + "/" + sessionid + "/" + gameid + ".json"
    s3client.put_object(Bucket=bucket_name, Key=key, Body=json.dumps(game, sort_keys=True, indent=2))

    # Delete received message from queue
    sqs.delete_message(
        QueueUrl=queue_url,
        ReceiptHandle=receipt_handle
    )
