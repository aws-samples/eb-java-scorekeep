import time
import json
import socket
import os
import binascii

START_TIME = time.time()
HEX=hex(int(START_TIME))[2:]
TRACE_ID="1-" + HEX + "-" + binascii.b2a_hex(os.urandom(12))
SEGMENT_ID=binascii.b2a_hex(os.urandom(8))
SEGMENT_DOC=json.dumps({"trace_id": TRACE_ID, "id": SEGMENT_ID, "start_time": START_TIME, "in_progress": True, "name": "Scorekeep-build"})
HEADER=json.dumps({"format": "json", "version": 1})
TRACE_DATA = HEADER + "\n" + SEGMENT_DOC

UDP_IP= "127.0.0.1"
UDP_PORT=2000
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(TRACE_DATA, (UDP_IP, UDP_PORT))

print SEGMENT_DOC
