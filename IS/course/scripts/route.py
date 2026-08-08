import requests

BASE_URL = "http://localhost:8080/api"

def login(username, password):
    print(f"Logging in as {username}...")
    response = requests.post(
        f"{BASE_URL}/auth/login",
        json={
            "username": username,
            "password": password
        }
    )
    response.raise_for_status()
    token = response.json()["token"]
    print(f"Token received for {username}")
    return token


def auth_headers(token):
    return {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }


def main():
    # 1. Captain login
    captain_token = login("captain1", "captain1")

    # 2. Captain creates route request
    print("Creating route request...")
    response = requests.post(
        f"{BASE_URL}/route/request",
        headers=auth_headers(captain_token),
        json={
            "shipId": 1,
            "sourceZoneId": 23,
            "targetZoneId": 1,
            "goal": "TRANSFER",
            "documentsIds": [1]
        }
    )
    response.raise_for_status()
    route_request_id = response.json()["id"]
    print(f"Route request created with id={route_request_id}")

    # 3. Submit route request
    print("Submitting route request...")
    response = requests.post(
        f"{BASE_URL}/route/request/{route_request_id}/submit",
        headers=auth_headers(captain_token),
    )
    response.raise_for_status()
    print("Route request submitted")

    # 4. Keeper login
    keeper_token = login("keeper", "keeper")

    # 5. Keeper creates route
    print("Keeper creating route...")
    response = requests.post(
        f"{BASE_URL}/route",
        headers=auth_headers(keeper_token),
        json={
            "routeRequestId": route_request_id
        }
    )
    response.raise_for_status()
    route_id = response.json()["id"]
    print(f"Route created with id={route_id}")

    # 6. Add segments
    print("Adding route segments...")
    response = requests.post(
        f"{BASE_URL}/route/{route_id}/segments",
        headers=auth_headers(keeper_token),
        json={
            "zoneIds": [23, 19, 18, 16, 15, 9, 1]
        }
    )
    response.raise_for_status()
    print("Segments added")

    # 7. Approve route
    print("Approving route...")
    response = requests.post(
        f"{BASE_URL}/route/{route_id}/approve",
        headers=auth_headers(keeper_token),
    )
    response.raise_for_status()
    print("Route approved")

    # 8. Captain starts route
    print("Starting route...")
    response = requests.post(
        f"{BASE_URL}/route/{route_id}/start",
        headers=auth_headers(captain_token),
    )
    response.raise_for_status()
    print("Route started")

    # 9. Captain completes route
    print("Completing route...")
    response = requests.post(
        f"{BASE_URL}/route/{route_id}/complete",
        headers=auth_headers(captain_token),
    )
    response.raise_for_status()
    print("Route completed successfully")


if __name__ == "__main__":
    main()
