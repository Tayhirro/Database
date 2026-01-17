import urllib.request
import re

url = 'https://so.gushiwen.org/shiwen/aju.aspx?id=1c177f8c9bdc'
r = urllib.request.urlopen(url).read().decode('utf-8')
m = re.search(r'<div class="contson"[^>]*>(.*?)</div>', r, re.S)
if m:
    content = m.group(1)
    content = re.sub(r'<[^>]+>', '', content)
    print(content)
else:
    print('not found')