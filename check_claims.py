import requests

url = "http://localhost:8081/api/v1/auth/login"
payload = {
    "email": "mithun-io@outlook.com",
    "password": "Qw3!@sPe:E1"
}
response = requests.post(url, json=payload)
data = response.json()
token = data.get("accessToken")

if token:
    headers = {"Authorization": f"Bearer {token}"}
    claims_response = requests.get("http://localhost:8081/api/v1/claims/search", headers=headers)
    print("Claims Response:")
    print(claims_response.json())
else:
    print("Login failed:")
    print(data)
