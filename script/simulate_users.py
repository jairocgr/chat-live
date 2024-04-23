#!/usr/bin/env python3

import time
import random
import secrets
import threading
import re
import argparse

from utils import ChatUser
from faker import Faker

N_FAKE_USERS = 6
LOGIN_MAX_LEN = 18

def user_loop(user, topics):
    while True:
        index = random.randrange(0, len(topics))
        topic = topics[index]
        user.send_message(topic)
        sleep = random.randrange(0, 2)
        time.sleep(sleep)

def gen_login_number_sufix():
    rand = random.randint(0, 3)
    return str(rand) if rand > 0 else ""


def name_to_login(name, max_len):
    login_number = gen_login_number_sufix()
    space_replacement = ["", "-", "_"]
    index = random.randrange(0, len(space_replacement))
    replacement = space_replacement[index]
    login = re.sub('[^a-z ]', '', name.lower())
    login = login.replace(' ', replacement)
    login = login[:max_len] if len(login) > max_len else login
    login = login.rstrip(''.join(space_replacement))
    login = login + login_number
    return login

def main():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument("-l", "--url", help="Base url", default="http://app:8080")
    parser.add_argument("-p", "--password", help="Admin password", default="admin")
    args = parser.parse_args()

    base_url = args.url
    passwd = args.password

    admin = ChatUser(base_url, "admin", passwd)

    faker = Faker()
    users = []
    for i in range(N_FAKE_USERS):
        profile = faker.simple_profile()
        name = profile['name']
        login = name_to_login(name, 16)
        passwd = secrets.token_urlsafe(8)
        user = admin.create_user(name, login, passwd)
        users.append(user)

    admin.create_room("Gamming Room", "gamming")
    admin.create_room("Miscellaneous Subjects", "misc")
    admin.create_room("Budget Planning for 2025", "budget-2025")

    topics = ["general", "gamming", "misc", "budget-2025"]

    for user in users:
        for topic in topics[1:]:
            user.join(topic)

    for user in users:
        thread = threading.Thread(target=user_loop, args=(user, topics))
        thread.start()

if __name__ == "__main__":
    main()
