import requests

# 注册
register_url = "http://localhost:8000/register"
register_data = {
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com"
}
response = requests.post(register_url, json=register_data)
print(response.json())

# 登录
login_url = "http://localhost:8000/login"
login_data = {
    "username": "testuser",
    "password": "123456"
}
response = requests.post(login_url, json=login_data)
print(response.json())
