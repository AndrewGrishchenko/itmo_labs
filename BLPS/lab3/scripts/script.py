import requests
import json

BASE_URL = "http://localhost:8080"
#BASE_URL = "http://192.168.3.8:8080"

jwt_token = None


def headers():
    if jwt_token:
        return {
            "Authorization": f"Bearer {jwt_token}"
        }
    return {}


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


def register():
    username = input("username: ")
    password = input("password: ")

    data = {
        "username": username,
        "password": password
    }

    r = requests.post(
        f"{BASE_URL}/users/register",
        json=data
    )

    print(r.status_code)

    try:
        print(json.dumps(r.json(), indent=2))
    except:
        print(r.text)


def login():
    global jwt_token

    username = input("username: ")
    password = input("password: ")

    data = {
        "username": username,
        "password": password
    }

    r = requests.post(
        f"{BASE_URL}/users/login",
        json=data
    )

    print(r.status_code)

    if r.ok:
        body = r.json()

        jwt_token = body.get("token")

        print("logged in")
    else:
        print(r.text)


def create_user():
    username = input("username: ")

    data = {
        "username": username,
        "password": username
    }

    r = requests.post(
        f"{BASE_URL}/users",
        json=data,
        headers=headers()
    )

    print(r.status_code, r.text)


def create_music():
    name = input("music name: ")

    data = {
        "name": name
    }

    r = requests.post(
        f"{BASE_URL}/music",
        json=data,
        headers=headers()
    )

    print(r.status_code, r.text)


def create_video():
    title = input("title: ")
    music_input = input(
        "music list (comma separated, optional): "
    )

    data = {
        "title": title
    }

    if music_input.strip():
        data["music"] = [
            m.strip()
            for m in music_input.split(",")
        ]

    r = requests.post(
        f"{BASE_URL}/videos",
        json=data,
        headers=headers()
    )

    print(r.status_code, r.text)


def create_complaint():
    video_id = int(input("videoId: "))
    details = input("claimDetails: ")

    data = {
        "videoId": video_id,
        "claimDetails": details
    }

    r = requests.post(
        f"{BASE_URL}/complaints",
        json=data,
        headers=headers()
    )

    print(r.status_code, r.text)


def view_entities(entity):
    r = requests.get(
        f"{BASE_URL}/{entity}",
        headers=headers()
    )

    print(r.status_code)

    try:
        print(json.dumps(r.json(), indent=2))
    except:
        print(r.text)


def moderate():
    complaint_id = input("complaint id: ")

    action = choose(
        "action",
        ["acceptViolation", "rejectViolation"]
    )

    comment = input(
        "moderator comment (optional): "
    )

    data = {}

    if comment.strip():
        data["moderatorComment"] = comment

    r = requests.post(
        f"{BASE_URL}/complaints/{complaint_id}/{action}",
        json=data,
        headers=headers()
    )

    print(r.status_code, r.text)


def complaint():
    complaint_id = input("complaint id: ")

    action = choose(
        "action",
        ["submit"]
    )

    r = requests.post(
        f"{BASE_URL}/complaints/{complaint_id}/{action}",
        headers=headers()
    )

    print(r.status_code, r.text)


def ensure_logged_in():
    if not jwt_token:
        print("You are not logged in")
        return False

    return True


def main():
    while True:
        action = choose(
            "choose action",
            [
                "register",
                "login",
                "add",
                "view",
                "moderate",
                "complaint",
                "exit"
            ]
        )

        if action == "exit":
            return

        if action == "register":
            register()
            continue

        if action == "login":
            login()
            continue

        if not ensure_logged_in():
            continue

        if action == "moderate":
            moderate()
            continue

        if action == "complaint":
            complaint()
            continue

        entity = choose(
            "choose entity",
            ["users", "videos", "music", "complaints"]
        )

        if action == "add":
            if entity == "users":
                create_user()

            elif entity == "videos":
                create_video()

            elif entity == "music":
                create_music()

            elif entity == "complaints":
                create_complaint()

        elif action == "view":
            view_entities(entity)


if __name__ == "__main__":
    main()
