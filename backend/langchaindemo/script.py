import re
from pathlib import Path
with open("./requirements.txt", 'r') as f:
    requirements = f.read()

cleaned = re.sub(r' @ file://.*', '', requirements)
with open("./requirements.txt", 'w') as f:
    f.write(cleaned)