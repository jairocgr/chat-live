#!/usr/bin/env python3

from utils import ChatClient
from utils import puts, from_utc_to_local
from dateutil import parser

import requests
import sseclient
import json
import argparse

def print_event(event):
    if event['type'] == 'MESSAGE':
        timestamp = parser.parse(event['data']['time'])
        timestamp = from_utc_to_local(timestamp)
        timestamp = timestamp.strftime('%Y-%m-%d %H:%M:%S')
        args = (
            timestamp,
            event['id'],
            event['author'],
            event['room'],
            event['data']['content'],
        )
        puts("%s %s <b>%s</b> in <b>#%s</b>: %s" % args)
    if event['type'] == 'JOIN':
        timestamp = parser.parse(event['createdAt'])
        timestamp = from_utc_to_local(timestamp)
        timestamp = timestamp.strftime('%Y-%m-%d %H:%M:%S')
        args = (
            timestamp,
            event['id'],
            event['author'],
            event['room'],
        )
        puts("%s %s <b>%s</b> joined <b>#%s</b>" % args)

def main():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument("-l", "--url", help="Base url", default="http://app:8080")
    parser.add_argument("-u", "--user", help="User name", default="admin")
    parser.add_argument("-p", "--password", help="User password", default="admin")
    args = parser.parse_args()

    base_url = args.url
    username = args.user
    passwd = args.password

    admin = ChatClient(base_url, username, passwd)

    rooms = [
        ("General Room", "general"),
        ("Gamming Room", "gamming"),
        ("Miscellaneous Subjects", "misc"),
        ("Budget Planning for 2025", "budget-2025"),
    ]

    auth = (username, passwd)
    uri = "/rooms/stream"
    url = base_url + uri
    params = []

    for room in rooms:
        name = room[0]
        handle = room[1]
        admin.create_room({ "name": name, "handle": handle })
        params.append(("room", handle))

    response = requests.get(url, stream=True, auth=auth, params=params)

    if response.status_code != 200:
        errmsg = "Error (HTTP %s): %s" % (response.status_code, response.text)
        raise Exception(errmsg)

    sse = sseclient.SSEClient(response)

    for event in sse.events():
        data = json.loads(event.data)
        print_event(data)

if __name__ == "__main__":
    main()
