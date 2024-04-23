
from datetime import datetime, timezone
from dateutil import tz

import lorem
import requests

def from_utc_to_local(src):
    from_zone = tz.tzutc()
    to_zone = tz.tzlocal()
    # Set the timezone from the source to UTC
    utc = src.replace(tzinfo=from_zone)
    return utc.astimezone(to_zone)

class ChatClient:
    def __init__(self, base_url, user, passwd):
        self.base_url = base_url
        self.user = user
        self.passwd = passwd

    def new_user(self, new_user):
        self.post("/users", new_user)

    def new_message(self, room, new_message):
        uri = "/room/%s/messages" % room
        self.post(uri, new_message)

    def create_room(self, new_room):
        self.post("/rooms", new_room, ignore_http_error=True)

    def join(self, room):
        self.post("/room/%s/join" % room)

    def post(self, uri, payload = None, ignore_http_error = False):
        url = "%s%s" % (self.base_url, uri)
        auth = (self.user, self.passwd)
        if payload:
            response = requests.post(url, auth=auth, json=payload)
        else:
            response = requests.post(url, auth=auth)
        if not ignore_http_error and response.status_code not in [200, 201]:
            msg = "Response error (HTTP %s): %s" % (response.status_code, response.text)
            raise Exception(msg)

class ChatUser:
    def __init__(self, chat_url, login, password) -> None:
        self.chat_url = chat_url
        self.login = login
        self.password = password
        self.client = ChatClient(chat_url, login, password)

    def create_user(self, name, login, passwd):
        puts("Creating user <b>%s</b> (name %s, %s)" % (login, name, passwd))
        self.client.new_user({
            "name": name,
            "login": login,
            "password": passwd,
        })
        return ChatUser(self.chat_url, login, passwd)

    def send_message(self, room):
        puts("User <b>%s</b> sending message to <b>#%s</b>" % (self.login, room))
        self.client.new_message(room, {
            "content": lorem.sentence(),
            "time": iso_datetime_string()
        })

    def join(self, room):
        puts("User <b>%s</b> joining <b>#%s</b>" % (self.login, room))
        self.client.join(room)

    def create_room(self, name, handle):
        puts("User <b>%s</b> creating room <b>#%s</b>" % (self.login, handle))
        self.client.create_room({
            "name": name,
            "handle": handle,
        })

def puts(msg):
    msg = msg.replace("<b>", "\033[1m")
    msg = msg.replace("</b>", "\033[0m")
    print(msg, flush=True)

def iso_datetime_string():
    return datetime.now(tz=timezone.utc).strftime('%Y-%m-%dT%H:%M:%S.%f%z')
