import time
import json
import socket
import sys
import binascii
import os

SEGMENT_DOC = json.loads(sys.argv[1])
EXCEPTION_ID = binascii.b2a_hex(os.urandom(8))
WORKING_DIRECTORY = "/var/app/current"
PATHS = "/var/app/current/src/main/java/scorekeep/"
LOG = sys.argv[2]
MESSAGE = LOG.split('* What went wrong:')[0]
ERROR = { "working_directory": WORKING_DIRECTORY, "paths": [ PATHS ], "exceptions": [ { "id": EXCEPTION_ID, "message": MESSAGE } ] }
del SEGMENT_DOC["in_progress"]
END_TIME = time.time()
SEGMENT_DOC["end_time"] = END_TIME
SEGMENT_DOC["error"] = True
SEGMENT_DOC["cause"] = ERROR

HEADER=json.dumps({"format": "json", "version": 1})
TRACE_DATA = HEADER + "\n" + json.dumps(SEGMENT_DOC)

UDP_IP= "127.0.0.1"
UDP_PORT=2000
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(TRACE_DATA, (UDP_IP, UDP_PORT))

print json.dumps(SEGMENT_DOC)
