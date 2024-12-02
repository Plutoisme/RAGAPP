
from pymongo.mongo_client import MongoClient
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

# Create a new client and connect to the server
client = MongoClient(uri)

# Send a ping to confirm a successful connection
try:
    client.admin.command('ping')
    print("Pinged your deployment. You successfully connected to MongoDB!")
except Exception as e:
    print(e)