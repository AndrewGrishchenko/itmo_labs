import requests
import json
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"


def auth(username):
    return HTTPBasicAuth(username, username)


def choose(prompt, options):
    print(f"\n{prompt}")
    for i, opt in enumerate(options, 1):
        print(f"{i}. {opt}")
    while True:
        try:
            choice = int(input("> "))
            if 1 <= choice <= len(options):
                return options[choice - 1]
        except:
            pass
        print("Invalid choice")


def create_user(a):
    username = input("username: ")
    data = {
        "username": username,
        "password": username
    }
    r = requests.post(f"{BASE_URL}/users", json=data, auth=a)
    print(r.status_code, r.text)


def create_music(a):
    name = input("music name: ")
    data = {"name": name}
    r = requests.post(f"{BASE_URL}/music", json=data, auth=a)
    print(r.status_code, r.text)


def create_video(a):
    title = input("title: ")
    music_input = input("music list (comma separated, optional): ")

    data = {"title": title}

    if music_input.strip():
        data["music"] = [m.strip() for m in music_input.split(",")]

    r = requests.post(f"{BASE_URL}/videos", json=data, auth=a)
    print(r.status_code, r.text)


def create_complaint(a):
    video_id = int(input("videoId: "))
    details = input("claimDetails: ")

    data = {
        "videoId": video_id,
        "claimDetails": details
    }

    r = requests.post(f"{BASE_URL}/complaints", json=data, auth=a)
    print(r.status_code, r.text)


def view_entities(a, entity):
    r = requests.get(f"{BASE_URL}/{entity}", auth=a)
    print(r.status_code)
    try:
        print(json.dumps(r.json(), indent=2))
    except:
        print(r.text)


def moderate(a):
    complaint_id = input("complaint id: ")

    action = choose("action", ["acceptViolation", "rejectViolation"])

    comment = input("moderator comment (optional): ")

    data = {}
    if comment.strip():
        data["moderatorComment"] = comment

    r = requests.post(
        f"{BASE_URL}/complaints/{complaint_id}/{action}",
        json=data,
        auth=a
    )

    print(r.status_code, r.text)


def main():
    username = input("login: ")
    a = auth(username)

    action = choose("choose action", ["add", "view", "moderate"])

    if action == "moderate":
        moderate(a)
        return

    entity = choose("choose entity", ["users", "videos", "music", "complaints"])

    if action == "add":
        if entity == "users":
            create_user(a)
        elif entity == "videos":
            create_video(a)
        elif entity == "music":
            create_music(a)
        elif entity == "complaints":
            create_complaint(a)

    elif action == "view":
        view_entities(a, entity)


if __name__ == "__main__":
    main()
