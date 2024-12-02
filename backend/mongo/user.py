from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from pymongo import MongoClient
from typing import Optional

app = FastAPI()

# MongoDB连接
from urllib.parse import quote_plus
import dns.resolver

# 配置DNS解析器
dns.resolver.default_resolver = dns.resolver.Resolver(configure=False)
dns.resolver.default_resolver.nameservers = ['8.8.8.8', '8.8.4.4']  # 使用Google DNS
dns.resolver.default_resolver.timeout = 30
dns.resolver.default_resolver.lifetime = 30
# 对用户名和密码进行URL编码
username = quote_plus("lizhinan2000")
password = quote_plus("2024MongoDB@")
# 构建MongoDB URI
uri = f"mongodb+srv://{username}:{password}@cluster0.imnmg.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
client = MongoClient(uri)
try:
    client.admin.command('ping')
    print("Pinged your deployment. You successfully connected to MongoDB!")
except Exception as e:
    print(e)
db = client["RAG"]
users_collection = db["user"]

# 数据模型
class UserCreate(BaseModel):
    username: str
    password: str
    email: Optional[str] = None

class UserLogin(BaseModel):
    username: str
    password: str

class UserResponse(BaseModel):
    username: str
    email: Optional[str] = None
    message: str

# 注册接口
@app.post("/register", response_model=UserResponse)
async def register(user: UserCreate):
    # 检查用户是否已存在
    if users_collection.find_one({"username": user.username}):
        raise HTTPException(
            status_code=400,
            detail="The user exists!"
        )
    
    # 创建新用户
    user_dict = {
        "username": user.username,
        "password": user.password,
        "email": user.email
    }
    
    try:
        users_collection.insert_one(user_dict)
        return UserResponse(
            username=user.username,
            email=user.email,
            message="register successfully!"
        )
    except Exception as e:
        raise HTTPException(
            status_code=400,
            detail=f"Database Error: {str(e)}"
        )

# 登录接口
@app.post("/login", response_model=UserResponse)
async def login(user: UserLogin):
    # 查找用户
    db_user = users_collection.find_one({
        "username": user.username,
        "password": user.password
    })
    
    if not db_user:
        raise HTTPException(
            status_code=400,
            detail="User or Password Error!"
        )
    
    return UserResponse(
        username=db_user["username"],
        email=db_user.get("email"),
        message="Login successfully!"
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)