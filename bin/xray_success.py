import time
import json
import socket
import sys

SEGMENT_DOC = json.loads(sys.argv[1])
del SEGMENT_DOC["in_progress"]
END_TIME = time.time()
SEGMENT_DOC["end_time"] = END_TIME
HEADER=json.dumps({"format": "json", "version": 1})
TRACE_DATA = HEADER + "\n" + json.dumps(SEGMENT_DOC)

UDP_IP= "127.0.0.1"
UDP_PORT=2000
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(TRACE_DATA, (UDP_IP, UDP_PORT))

print json.dumps(SEGMENT_DOC)
