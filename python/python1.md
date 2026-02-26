---
title: "闭包-高阶函数"
date: "2026-01-30"
categories:
  - python
description: "嵌套函数 ---捕获外部变量，并且保存该变量"
---
# 闭包-高阶函数

## 闭包

- 嵌套函数 ---捕获外部变量，并且保存该变量

```
#通过__closure__实现
def create_counter():
	count = 0
	def counter():
		nolocal count
		count += 1
		return count
	return counter	
my_counter.__closure__[0].cell_contents
```









### 传入函数

- 函数接收有函数
- 函数返回有函数

```python
def add(x, y, f):
    return f(x) + f(y)
```

### map

`map()`函数接收两个参数，一个是函数，一个是`Iterable`，`map`将传入的函数依次作用到序列的每个元素，并把结果作为新的`Iterator`返回。

举例说明，比如我们有一个函数f(x)=x2，要把这个函数作用在一个list `[1, 2, 3, 4, 5, 6, 7, 8, 9]`上，就可以用`map()`实现如下：

```
map(f,[x1,x2,x3,x4])
```



### reduce  迭代套

`reduce`把一个函数作用在一个序列`[x1, x2, x3, ...]`上，这个函数必须接收两个参数，`reduce`把结果继续和序列的下一个元素做累积计算，其效果就是：

```python
reduce(f, [x1, x2, x3, x4]) = f(f(f(x1, x2), x3), x4)
```

### filter

`filter()`也接收一个函数和一个序列。

```python
def _odd_iter():
    n =1
    while True:
        n=n+2
        yield n
def _not_divisible(n):		#返回函数
    return lambda x: x % n > 0       
def primes():
    yield 2
    it = _odd_iter()
    #过滤2的倍数
    while True:
        n = next(it)
        yield n
        it = filter(_not_divisible(n), it)	#bool为真，则输出        
```

- filter是一个嵌套过程     
  - 迭代器嵌套
  - 遍历所有的it，将it的值传递给function，然后进行谓词函数判断







### sorted

- sorted的高阶定义

```python
sorted([36, 5, -12, 9, -21], key=abs)
```

此外，`sorted()`函数也是一个高阶函数，它还可以接收一个`key`函数来实现自定义的排序，例如按绝对值大小排序：

```plain
>>> sorted([36, 5, -12, 9, -21], key=abs)
[5, 9, -12, -21, 36]
```

key指定的函数将作用于list的每一个元素上，并根据key函数返回的结果进行排序。对比原始的list和经过`key=abs`处理过的list





### partial

- 参数保留

```py
from functools import partial
func_name = partial(nn.BatchNorm1d, eps=1e-3,momentum=0.01)
```









## 返回函数

- 返回函数时，注意循环变量，会被更新为最新数据
  - 外层结束 内层循环变量最新

```python
def lazy_sum(*args):
    def sum():
        ax = 0 
        for n in args:
            ax = ax + n
        return ax
    return sum
```

### nonlocal

- 读正常

```python
def inc():
    x = 0
    def fn():
        # 仅读取x的值:
        return x + 1
    return fn

f = inc()
print(f()) # 1
print(f()) # 1
```

但是，如果对外层变量赋值，由于Python解释器会把`x`当作函数`fn()`的局部变量，它会报错：

```python
def inc():
    x = 0
    def fn():
        # nonlocal x
        x = x + 1
        return x
    return fn

f = inc()
print(f()) # 1
print(f()) # 2
```

















# 函数与基础

## 类中

```py
_func(s1,s2...):			#受保护函数
__func			#名称重整  相当于私有    
__func__ （__len__ ...）				#魔术函数
```

- 这里，`__private_var` 被重命名为 `_MyClass__private_var`，这是一种 Python 机制，用于避免子类覆盖父类中的私有属性。但是，如果你知道重整后的名称，仍然可以访问它。

## 类外

- 局部变量  局部函数

```py
_var 	#在导入其他地方时不会导入
```











## del函数

什么都可以删





## 函数的默认参数

```python
def func(ex1,ex2,ex3=12,ex4='Beijing')
```

## 可变参数

```python
def func(*numbers)
```

## 关键字参数

```python
def person(name, age, **kw):
    
    person('Adam', 45, gender='M', job='Engineer')
```

## dict

```python
dict1={
    'a':1,
    'b':2,
    'c':3
}
#   数组下标寻址
#   key寻址
```



## list 

```python
# 创建一个空列表
my_list = []
# 创建一个包含元素的列表
my_list = [1, 2, 3, 4, 5]
# 使用 list() 函数创建
my_list = list([10, 20, 30])


#切片访问
list[1:4:1] s.e.step

#list.pop()
#del
#remove(a)	


#list.append
#list.insert(a,idx)

#len


#list.index
#list.sort
#list.reverse
#list.count

in not-in
#解包
ass=[1,2,3,40]
aw,bw,cw,dw=ass
```



```python
list(range(a,b)) a~b-1
#数组生成
[x*x for x in range(1,11)]
```



## tuple

```python
# 创建一个空元组
empty_tuple = ()

# 创建一个包含多个元素的元组
my_tuple = (1, 2, 3, 4, 5)

# 不使用括号也可以创建元组（推荐使用括号以增加代码的可读性）
another_tuple = 10, 20, 30

# 创建单元素元组，注意要加逗号
single_element_tuple = (42,)  # 注意必须有逗号，否则会被当作普通数字
# 使用 tuple() 函数创建
from_list = tuple([1, 2, 3])  # 将列表转换为元组


#和数组差不多
#切片访问
tuple[a:b:1]  a -b-1 
#元组合并
tuples=t1+t2

```

## string

```python
a=''
#拼接
a+='asdw'
#join
a.join(iterator)
```

# 生成器

- `generator` 是一种特殊的 **迭代器**，通过延迟计算（惰性求值）逐个生成值，而不是一次性将所有值存储在内存中。
- for in = next = 调用yield

```python
g = (x * x for x in range(10))
```

```python
def fib(max):
    n, a, b = 0, 0, 1
    while n < max:
        yield b
        a, b = b, a + b
        n = n + 1
    return 'done'
```

```py
def data_loader(dataset):
    for item in dataset:
        yield preprocess(item)

for batch in data_loader(train_data):
    train_step(batch)
```



# 迭代器  

## Iterator   无限

```
def gen():
	for i in range(10):
		yield i
#在定义完yield后，返回一个生成器对象
```

## Iterable   有限









# decorator 装饰器

- 装饰器本质上是一个**高阶函数**，它接受一个函数作为参数，并返回一个新的函数（或者可调用对象）。装饰器通常用于添加或修改函数的行为，而不需要改变函数本身的代码。

- 先行注入





这种在代码运行期间动态增加功能的方式，称之为“装饰器”（Decorator）。

```python
#先定义一个装饰器
def log(func):
    @functools.wraps(func)
    def wapper(*args,**kw):
      	print('call %s():'(func.__name__))
        return func(*args,**kw)
    
#使用log
@log
def now():
    print(datetime.now())

```

- 三层函数
- 需要用wraps去修改返回函数名字

```python
import functools

def log(text):
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kw):
            print('%s %s():' % (text, func.__name__))
            return func(*args, **kw)
        return wrapper
    return decorator
def now():
    print('2015-3-25')


nows =log('execute')(now)
print(nows.__name__)
```



- 可以通过装饰器，传入参数，从而实现对 `** `kwargs或*args的注入













# 过滤器

## 布尔过滤器

`&` 是布尔运算符，表示“与”（and）。

`|` 是布尔“或”（or）运算符。

`~` 是布尔“非”（not）运算符。













# print

```python
print('string %s()' % (参数))
```







# 网络



## 协程 coroutine

协程是**用户态的轻量级线程**

```py
import asyncio 
async def coro1():
	await asyncio.sleep(1)
async def coro2():
	await asyncio.sleep(2)
	
async def main():
	await asyncio.gather(coro1(),coro2())
    task1 = asyncio.create_task(my_coroutine("Task 1"))
    
```







##  **HTTP 会话**

### 持久连接

- TCP连接可以复用，从而避免重复建立和断开连接开销

### 连接池

- 维护一定程度的空闲连接供重用
- 对于每一个服务器访问，都会分配一个对应的连接池

```py
import aiohttp
import asyncio

async def fetch(session,url):
    async with session.get(url) as response:
        sock=response.connection.transport.get_extra_info('peername')
        print(f"Request to {url} uses socket: {sock}")
        return await response.text()
    
    
async def main():
    urls=['1','2']
    async with aiohttp ClientSession() as session:
        tasks =[fetch(session,url) for url in urls]
        await asyncio.gather(*tasks)
        
        
     asyncio.run(main())
```















## aiohttp --客户端 /服务器端   asyncio

- `async` 和 `await` 来非阻塞地发起请求

```py
async def request():
	async with aiohttp.ClientSession as session:
		async with session.get("url") as resopnse:
            
        
```

- 创建一个ClientSession 然后通过session请求
  - **客户端**创建**session**
- session.get()
- session.post()





```
from aiohttp import web
web1 = web.Application()
```















## requests  --客户端

- get
- post

```python
import requests

data=requests.get("ss")
data.text
data.content #二进制
data.url   #最终URL
data.cookies	
data.headers 	#也有cookies
data.history 	#重定向记录
data.reason	#状态码

.json() 


```

### Response

```py
status_code #状态码
headers	#头
text content(二进制内容) #内容


cookies#
history#重定向历史记录
url #最终请求的url

reason #响应原语


#############
json()
raise_for_status()
###############

```

### Request

```py
method # 请求方法
url  #请求url
headers #头部
{
   'Content-type'
    'Accept'
}


cookies   #RequestsCookieJar 对象

body 







```







## http头

- 在建立完tcp连接后，包装http或https头

```
Authorization

User-agent


```









## 服务器端

- HttpResponse 对象
- status	#状态
- headers  {content-type}
- body(data)
- cookies
- json(可有)



- response()   **data,status,content-type**
- response.text() **data ,content-type**     
  - header{ "ContentDisposition":"attachment; filename=result.csv"}	//**可下载**





- HttpRequest对象
- json
- data
- headers
- cookies









### flask   ---服务器端

#### 应用上下文 (Application Context)

- 存储相关变量 和上下文信息

```py
local_stack()	
# 生成一个栈
with app.app_context()#创建上下文
```

#### 请求上下文 (Request Context)

- 服务器获取前端提交的request
- Werkzeug 构建



#### Response

- Flask 使用线程局部（thread-local）的上下文来提供对当前请求的访问。这意味着在每个请求处理期间，你可以通过 `flask.request` 来访问请求信息，而不需要显式地将其作为参数传递给视图函数。

- 所以各种方法可以从全局调用







- headers {content-type}
- cookies
- data
- json

```py

set_cookie(
	 key,
	 value='',
	 max_age=None, 
	 expires=None, 
    
    
    #生效域
    path='/',
    domain=None #域名
    
    #安全
    secure=False,
    httponly=False,
    samesite=None               
    # 限制 Cookie 的跨站点发送行为，可以为 'Lax', 'Strict', 'None'
)
#############
```





#### Request

```py
class flask.Request(environ,populate_request=True, shallow=False)
--flask.Request 是 werkzeug.wrappers.Request的一个子类
args  #查询参数				类似如下
   -`http://localhost:8000/query? \
   name=${encodeURIComponent(name)}`
url
headers
body
cookies
json
form #获取表单数据
text #用于获取文本数据
files

#-----
request.json.get(key)



```





#### route

- 路由路径变量  

```py
app.route('/user/<类型：类型名>')
```



### sanic   服务器端

- 没有线程局部性 所以需要显示传递





- response 模块 
  - 内置各种返回HTTPResponse函数方法





```py
app = Sanic("")
@app.route('/path/', methods=["GET"])
@app.get('/path')
async def func(request):


    
    
代理返回：
def json(body, status=200, headers=None, content_type="application/json", dumps=json.dumps, **kwargs):
    # 内部逻辑，处理 body 序列化为 JSON 字符串，并创建 HTTPResponse 对象
    return HTTPResponse(
        dumps(body, **kwargs),
        status=status,
        headers=headers,
        content_type=content_type
    )


```

#### response    

- body
- header
- cookies
- content-type
- status











```py
from werkzeug.local import LocalProxy
from flask.globals import _request_ctx_stack

# 创建全局的 request 代理对象
request = LocalProxy(lambda: _request_ctx_stack.top.request)
```

**`lambda: _request_ctx_stack.top.request`**：这个 lambda 表达式返回当前请求上下文中的 `request` 对象。因此，每次我们访问 `request` 时，`LocalProxy` 会动态地将调用引导到正确的请求对象。























## 表单增删查改



```py
db.query  #查询操作
	.filter_by()
    
    .first()
    .all()
    .count()
    .get(primary_key)
    .filter()	#条件表达式



```

```py
user = User.query.filter(User.username == 'example_user', User.password == 'example_password').first()

```













## json



### jsonify-flask 

```python
json-data=jsonify(s1="1",s2="2",s3...) #直接传键值对

json-data=jsonify({"message": "User registered successfully", "status": "success"})			#传字典


users_list = [
        {"username": "Alice", "age": 30},
        {"username": "Bob", "age": 25},
        {"username": "Charlie", "age": 35}
    ]
json-data-list=jsonify(users-list)		#传列表
```







### JSON

```py
json.loads()    #str ---> dict/list
json.dumps(ss:dict) #dict/list ---> str 

json.load()   #文件--->dict/list
with open("data.json","r") as f:
    data_loaded = json.load(f)
json.dump(obj,file) #对象---->无返回值    
with open("data.json", "w") as f:
    json.dump(data,f)    
```

#### responese

```python
Response(json_str, mimetype='application/json')
```



## JWT

```
import jwt
payload ={
	a1:'',
	a2:''
}
token=jwt.encode(payload,secret_key,algorithms='HS256')
payload=jwt.decode(token,secret_key,algorithms='HS256')
#后端生成token--->发给前端--->前端后续都带有token给后端验证
```







## Bcrypt --加密

```py
bcrypt.hashpw(password,salt)

#盐
salt =bcrypt.gensalt()



bcrypt.checkpw(checkpw,realpwhash):
```



## Socket  套接字

```
Socket(socket.AF_INET,socket.AF_STREAM)


connect(port,ip)        //bind and listen

sendall(内容)

recv(频率)

```















# 数据库

## 对象关系映射 orm   类



- 类的属性     表的属性
- 类的实例     表的一行
- 





- 表--映射-对象             

- 表中的一行  ---类的实例    

### 对象的持久化 Session





### flask的 flask_SQLAlchemy

- 内部封装 SQLAlchemy



- session管理

```py
self.session = self._make_scoped_session(session_options)
```





## 原生流程

SQLALLocEnv：初始化ODBC环境，返回**环境句柄**；  

```py
from sqlalchemy import create_engine
engine = create_engine('mysql+pymysql://root:123456@{}:3306/robot'.format(tempRoot))
```

SQLALLocConnect：为连接句柄分配内存并返回连接句柄；  

SQLConnect：连接一个SQL数据资源；  

```
connection = engine.connect()
```

SQLDriverConnect：连接一个SQL数据资源，允许驱动器向用户询问信息； 

- 连接到具体的数据库  

```
mysql+pymysql://root:123456@{}:3306/robot
```





 SQLALLocStmt：为语句句柄分配内存, 并返回语句句柄；  

```python 
with engine.connect() as connection:
    result = connection.execute("SELECT * FROM table_name")
```

SQLExecDirect：把 SQL 语句送到数据库服务器，请求执行由SQL语句定义的数据库访问；  

SQLFetchAdvances：将游标移动到查询结果集的下一行(或第一行)； 



 SQLGetData：按照游标指向的位置，从查询结果集的特定的一列取回数据；  

SQLFreeStmt：释放与语句句柄相关的资源；

```
result.close()
```



SQLDisconnect：切断连接；  

```
connection.close()
```

SQLFreeConnect：释放与**连接句柄**相关的资源；  

SQLFreeEnv：释放与**环境句柄**相关的资源。

```
engine.dispose()
```

## 流程

- 创建db  --models中
- 创建
- 延迟绑定

```py
db = SQLAlchemy()




```

- 配置app的数据库相关信息

- ```py
  # 配置数据库连接信息
  app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:Mytked.021342@localhost:3306/robot'
  app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
  ```

- 绑定

```py
db.init_app(app)
```

- 后续操作







## SQLAlchemy ORM（对象关系映射）流程

- 对类的实例进行修改





- 通过db.session进行提交























# np

## 广播

- 从右向左比较形状   维度不同，则在小形状左侧填充1  （升维）

- 对每一个维度：尺寸相等或者 尺寸为1则重复  （重复）

  





## 类别

- ndarray np的数组





## 布尔

- np.array可以直接参与bool运算 

```
a = np.array([1,2,3])
b = np.array([1,0,3])



result = a==b



```

- 数组，布尔索引

```
a= np.array([1,2,4,5])
mask = a>2 #bool数组
result =a[mask]
```

- np.where

```
a = np.array([1, 2, 3, 4, 5])

# 返回满足条件的元素的索引
indices = np.where(a > 2)

```

- all,mean(计算比例),sum(计算数量)

```
arr = np.array([[True,False],[True,True]])
print(np.mean(arr))
print(np.mean(arr,axis=0))
```







## 索引

```py
arr=np.random,rand(3,4,1,2)
#一般索引
arr1 = arr[:,[1,3],:,:]	



#可以用数组进行索引 ---高维数组索引
arr1 = arr[arr2,:]
#可以用bool数组进行索引


np.unravel_index(np.argmax(instance_overlaps, axis=None), instance_overlaps.shape)


```



## 计算

```py
import numpy as np
np.dot(A,B)
#总是做匹配的算法
A@B   
#矩阵计算
-(n,m)  (m,) --> (n,m) (m,1)
#内积  
-一维乘积  标量
-(n,) (n,) --> 每个相乘 
-高维乘积  矢量  
#广播
```



### @ 矩阵乘法  



### T 转置









## 方法

### 生成

#### array

```python
numpy.array(object, dtype=None, copy=True, order='K', subok=False, ndmin=0)
np.asarray
```



#### 切片

```py
#切片
# 示例数组
arr = np.array([[1, 2, 3],
                [4, 5, 6],
                [7, 8, 9]])

#步长切片
arr[::2,::2]
arr[[start]:[end]:[step],...]
#-1负索引
#布尔索引
arr[arr > 5]
#访问

a=example[a,b]




```

- 用数组进行切片（或者等价于数组）
- **维度只是决定括号** 最终还得是赋值的内容决定





```py
# 找出在 shape=(3,4) 的二维数组中，第 5 个元素的二维索引位置
index = 5
shape = (3, 4)

result = np.unravel_index(index, shape)
print(result)  # 输出: (1, 1)


```



#### transpose np方法     .T类方法

```
arr = np.array([[1,2,3],[4,5,6]]).T  

np.transpose(arr)
np.transpose(arr, axis=(dim1, dim2, ...))   #指定顺序
# 交换两个维度的顺序

```

### 扩展

```
-expand_dims    np方法
np.expand_dims(arr,axis=n) 
b = np.expand_dims(a, axis=0)


-numpy.newaxis  np方法    
ss2 =ss1[:,:,np.newaxis]  
- 比如在最后一维度增加一个维度


```

#### squeeze unsqueeze np方法

```
s1 = np.squeeze(s1, 0) 移除第0维

```



#### tile   np方法

```
import numpy as np
cx = np.array([[1, 2, 3],
               [4, 5, 6]])

np.tile(cx[...,np.newaxis],anchor_num)
第一维（对应原数组的第一维）不重复（即重复1次）；
第二维（对应原数组的第二维）也不重复（即重复1次）；
新增的第三维（由 np.newaxis 创建）将被重复 anchor_num 次
```







#### np.vstack 针对np	 	 axis=0

```py
result=np.vstack((arr,arr1))



```

#### np.hstack   axis=-1

- 横向堆叠



#### np.concatenate() 针对np    --任意轴     

```py
result =np.concatenate((arr,arr1),axis=0)
```



#### np.stack

- 一般添加组合   -1（末尾）   沿最后维度堆叠
- batch 批次   0  （开头）   沿最开始增加维度

```
numpy.stack(arrays, axis=0)  沿某一个轴进行堆叠
```



#### np.pad    pad2d...





####  r_ 全局方法   

- 行拼接数组





### 索引方法

#### where 

```py
np.where(rings == ring_id)
→ 输出：
(array([2, 3]),array([2, 3]) ) #不同维度的indices




rings_indices = np.argwhere(rings  == ring_id)
```

#### argsort  返回排序的索引数组

```
 s_ss.argsort()[::-1]

```





#### unique 

```py
np.unique(np_array,axis,return_inverse=True,return_count=True )  
```

- 反向索引

#### np.digitize --设置分类

```py
y_binned = np.digitize(y, bins=np_array)  # 将y分为以np_array划分的类别  返回索引
```

#### np.unravel_index

```
id_pos = 5
shape = (3, 3)  # 矩阵形状
row, col = np.unravel_index(id_pos, shape)
#5 -->1,2
```



### random  

```py
np.random.randint(low,high=None,size=None,dtype=int)
//随机整数

//均匀分布
np.random.uniform(low,high,size,dtype)

-----------------指定  low high   size type------


//高斯正态随机
np.random.normal(n,m,x) 	#n为均值 m为标准差 x为个数








np.random.choice([0,1],size, p=0)   
np.random.choice(a,size=None,replace=True,p=weights) 
#p为全权重数组
#size 为返回的size  a为初始np矩阵
```









### shape    

```py
 x_data=np.array([[1,2,3,4,5],[2,3,4,5,6]])
 x_shape=x_data.shape
```

- 返回元组信息

```
(2, 5)
```







### arg 计算

```py

#返回索引
np.argmin

np.argmax   

np.argsort(a, axis=-1, kind='quicksort', order=None)


#返回具体的值
np.mean  #平均值

np.median	#中位数

np.std #标准差



np.sum	#总和

arr=[1,2,3,4,5]
mean=np.mean(arr)


np.floor

### np.bincount(y) --统计数量（非负数）





```





















# pd

```py
data = {
    'Name': ['Alice', 'Bob', 'Charlie', 'David'],
    'Age': [24, 27, 22, 32],
    'City': ['New York', 'San Francisco', 'Chicago', 'Boston']
}
#字典-数组
datas=pd.DataFrame(data)
# 数组-数组
data = [['Alice', 24, 'New York'],
        ['Bob', 27, 'San Francisco'],
        ['Charlie', 22, 'Chicago'],
        ['David', 32, 'Boston']]

df = pd.DataFrame(data, columns=['Name', 'Age', 'City'])

# 数组-字典  
data = [
    {'Name': 'Alice', 'Age': 24, 'City': 'New York'},
    {'Name': 'Bob', 'Age': 27, 'City': 'San Francisco'},
    {'Name': 'Charlie', 'Age': 22, 'City': 'Chicago'},
    {'Name': 'David', 'Age': 32, 'City': 'Boston'}
]
writer = csv.writer(f)
writer.writerow
writer.writerows
```

```py
df.read_csv
#按列存储为多个一维数组 ，然后通过 .iloc[i, j] 去定位具体元素
df[].values   # series
df.iloc	#基于位置的索引器
df.loc[i]# series

df.iterrows() 

```



# 矩阵运算

## tensor

```
matrix1*marix2 	#(n,c,w,h,7)  *(n,c,w,h,7)

#乘法 
torch.matmul(A,B)
A@B

#逐元素加
A+B
#逐元素减法
A-B
#逐元素除法
A/B

#矩阵的逆
 torch.inverse(B)

#A.T


```

## numpy

```
#广播
#直接赋值
(m,n) = (m,n)

-索引     (包括切片)
spatial_feature[:,indices] = pillars 



```













# 基本函数

## random

```py
s = Random.random(0)   
s.uniform()#生成
```



## zip

- 返回zip对象

```py
names = ["Alice", "Bob", "Charlie"]
scores = [88, 92, 85]
#按列合并
paired = zip(names, scores)
iter1,iter2 = zip(*paired)   #变回去
```

## Counter

### most_common

```py
list=Counter(Y).most_common(i)
```

## sorted

- 接收iterable

```
sorted(iterable,reverse=False  # 按升序排序)
```















# 基础

## 小计

````py
#三元表达式
expression_if_true if condition else expression_if_false
#生成器
gen = (print(num) for num in numbers if num > 5)
#列表推导式   带返回  会做额外内存分配
example = [expression for item in iterable if contdition]
--如下例子
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
squared_evens = [x**2 if x % 2 == 0 else x for x in numbers]
## 动态
#getattr  获取对象的属性name的具体内容
getattr(object,name[,default]) #属性名称(字符串) ,默认
#setattr
setattr(obj,name,value)
#hasattr
hasattr(object,name)
#get  dict.get()
get()

#匿名
lambda [x,y,z...]#参数 : f(x,y,z ...)#内容
add = lambda x, y: x + y
#callable
Callable[ [...参数类型列表...], 返回值类型 ]
TransformFn = Callable[[np.ndarray,np.ndarray],Tuple[np.ndarray,np.adarray]] 

````



## 输出

```py
print(*objects, sep=' ',end..,file=sys.stdout, flush=False) 
#打印的对象 可以是多个 
#file指定的是输出流    --默认标准
```

## 数组

- 可变对象

```python

#生成
n=[x for x in range(1,N+1)]		


[expression for item in iterable]


#list
n=list(range())		#迭代 iterable


#方法
n.append(item)
n.extend(iterable) #添加可迭代对象    -添加多个



n.count(value):    #计数
n.index(value ,start ,end)   #返回   返回value的index
    
n.insert(index, item)		#￥￥￥￥￥￥￥￥￥￥￥


n.remove(value):  #第一次出现的remove    
n.pop([index]):   #移除 并 返回      ￥￥￥￥￥￥￥￥     
n.clear()	


#直接赋值  直接是引用
    
    
#拼接
a=[1,3,4]
a=a+[1,3]    
    
    
    
n.copy()  #浅拷贝  #对内部的嵌套内容属于引用 

import copy  #深拷贝
n1 = copy.deepcopy(n)



n.sort(key=None, reverse=False):    排序
    
    
```





### 转化

```py

a_list1=b_list1.astype(int) 		# 数组b_list1为float转换为整形

```





### 高维数组

```py
img=[[],[],[]]		#二维数组 表示宽度和高度
```







### 高级索引

```
xyz[[1,3,4],[2,5,6],:]

```



### 布尔操作

```
a=[1,3,4,5,56]
b=a>=12 #b=[False,False,False,False,True]
 
 
 
 #all,any,sum,mean
print(any([False, False, True]))  # True
print(any([False, False, False])) # False
 
```











## 字典+OrderedDict 

```
a={
  'a1':'key1',
  'a2':'key2'
}
#获取key的value
a.get("a1")

#pop key的数据
a.pop("a1")
a.popitem()

#更新 插入
a.update({"example":"example_value"})

#循环
a.keys()  a.values()
对字典的访问
for i in a.items():
```











## enumerate

- 作用于iterable







## 引用

- 内存是引用计数

```python
n =list(range(N+1))
print(isinstance(n,Iterator))
ns=np.array(n)	#创建新内存

nm =np.array(ns,copy=False)	#引用计数+1
del ns	#引用计数-1，还有1，所以还有
```







## 格式化

- 输入出到控制台

```py
print("Name: %s, Age: %d" % (name, age))
#旧版控制

print("Name: {}, Age: {}".format(name, age))
print("Name: {0}, Age: {1}".format(name, age))  # 使用位置参数
print("Name: {name}, Age: {age}".format(name=name, age=age))  # 使用关键字参数
#.format()

print(f"Name: {name}, Age: {age}")
#f控制


```

- 输入输出到文件重定向

```py
with open("1.txt","w") as f:
	f.write(f"{varibles}")
```



## STR

- 使 用 Unicode 编码

```py
str.encode() #转换为byte
```



### 不同编码

- 字节编码只支持ASCII字符





## argprase

```py
parser = argparse.ArgumentParser()
parser.add_argument("--example", type=str,required=True,help="")  #添加规则  cmd识别内容    
#定义参数的名称或标志。可以是短选项（如 -h）或长选项（如 --help）。
#parser.add_argument("-f", "--file", help="input file")

args = parser.parse_args()	#解析
return args
--- args.example ....

```

- **`action`**：
  - 定义参数的行为。常见的值包括：
    - `'store'`：存储参数的值（默认行为）。
    - `'store_true'`：如果参数存在，则存储 `True`；否则存储 `False`。
    - `'store_false'`：如果参数存在，则存储 `False`；否则存储 `True`。
    - `'append'`：将参数的值追加到一个列表中。
    - `'count'`：统计参数出现的次数。

- `default`：指定参数的默认值。
- `choices`：限制参数的取值范围。



## 路径

### os

```py
os
#获取目录
pathroot=os.getcwd()


#列出内容  return  dict
contents = os.listdir('/path/to/directory')
#遍历
for root, dirs, files in os.walk('/path/to/directory'): #root是当前目录，dirs是当前目录下的子目录，files是当前目录下的文件
    for file in files:
        print(os.path.join(root, file))

#path
exists = os.path.exists('file_or_directory')
os.isdir
os.isfile

    #返回最后的文件名
    basename = os.path.basename(path)
    #返回path中的路径 
    dirname = os.path.dirname(path)
    full_path = os.path.join('/path/to', 'file.txt')
 
    

```

### Path

```py
ss = Path("data.csv")
ss.parent 
ss.mkdir(parents=True,exist_ok=Ttue)
ss.resolve() #转换为绝对路径
```









## 正则

```py
import re

expression
matcher =re.match(s1,expression)
matchs =re.search(s12,expression)		#re.match() 不同，re.search() 不要求匹配发生在字符串的开头
matcherf=re.fullmatch(s2,expression)	#完全匹配

#编译
pattern =re.compile(s3)
matchp=pattern .方法



#正则替换
re.sub(pattern, replacement, string, count=0, flags=0)
#flag 正则表达式的标志（如忽略大小写），默认是 0。
re.sub(r'\d+','X',epression,0)	# 默认位0，匹配所有




```

- 规则

  ```py
  . 匹配任何一个字符
  * 匹配前一个字符 0 次或多次
  + 匹配前一个字符 1 次或多次
  ？ 匹配前一个字符 0 次或 1 次
  ^ 字符串开始
  $ 字符串结束
  
  {n} 匹配前一个字符n次
  {n,}匹配前一个字符至少n次
  {n,m}匹配前一个字符n到m次
  
  
  ```

- re的转义

```
\d：匹配任何数字，等价于 [0-9]
\D：匹配任何非数字字符
\w：匹配任何字母、数字或下划线，等价于 [a-zA-Z0-9_]
\W：匹配任何非字母、数字或下划线字符
\s：匹配任何空白字符（空格、制表符、换行等）  
\S：匹配任何非空白字符

\b 单词边界
	匹配单词开头或结尾
	\bword：匹配单词 word，并确保 word 位于单词边界（即前面没有字母、数	  字或下划线）。
	word\b：匹配单词 word，并确保 word 后面是单词边界。


\.
\*
\^
\+
\?

\{

\}

```

- 分组

```
()：用于分组表达式，捕获匹配的子表达式
(?:...)：非捕获分组
\1、\2 等：引用前面的分组（捕获组的回溯引用）
```

- 字符类

  - ```
    - `[abc]`：匹配方括号内的任意字符（如`a`、`b`、`c`）
    - `[^abc]`：匹配不在方括号内的任意字符
    - `[a-z]`：匹配小写字母范围内的任意字符
    - `[A-Z]`：匹配大写字母范围内的任意字符
    - `[0-9]`：匹配数字范围内的任意字符
    ```

## * 解包

- 将iterable 解包为单独元素
- 将元组解包为参数
- 后面跟的参数必须以关键字传递

### 循环解包

- enumerate()
  - 返回一个元组  （索引 内容）

```
for (x1,x2) in enumerate(scenario_folders):
```











## **解包

- 将字典键值解包为参数



```
#函数定义使用
#自动将内容封装为一个dict，然后传入后解包

def myFunction(**kwargs):
	for key,value in kwargs.items():
		print(f"{key}:{value}")
#使用函数使用
#这个时候是直接使用dict
```



## 打包

```py
zip(list1,list2,list3)   #对于[i]打包为一个tuple
```









# 模块

```py
#更改绑定 别名引用

__init__.py中  
from .modules.module import Module  
# 导入nn.modules.module里的Module类

# 让 nn.Module 直接指向 Module 类
Module = Module
```

```py
my_package/ 
|
|---- mymath.py
|---__init__.py
|--- setup.py     #用pip来进行安装 

from setuptools import setup
setup(
	name= ,
    version =,
    py_modules=[]  #packahes=[]  etc.
)

#__doc__  输出 __init__.py的文档字符串
#__package__
#__loader__
加载器
#__spec__
导入规格说明
#__dict__
模块内所有变量和函数
#__all__

```

## 加载器







## importlib

```py
import importlib
backbone_name = "resnet"
model_filename = f"opencood.models.{backbone_name}"
model_lib = importlib.import_module(model_filename)   #返回一个模块
```

# 类

## meta-class   --- 元类

```py
Foo = type('Foo', (父类), {'x': 1})  
Foo.__new__(Foo, bases, attr)  #创建元类的时候需要



1.记录属性   cls.class_p
2.        if 'get' not in attrs:
            raise TypeError("必须实现 get() 方法")
3.attr 中修改内容
class FooMeta(type):
    def __new__(cls, name, bases, attrs):
        # 1. 添加 class_p 属性
        attrs['class_p'] = f"{name}_meta_property"

        # 2. 检查是否实现了 get 方法
        if 'get' not in attrs:
            raise TypeError(f"{name} 必须实现 get() 方法")

        # 3. 修改类中的方法或属性：比如包装函数
        if 'run' in attrs:
            original = attrs['run']
            def wrapped(self, *args, **kwargs):
                print(f"[{name}.run] before")
                return original(self, *args, **kwargs)
            attrs['run'] = wrapped

        # 创建类
        return super().__new__(cls, name, bases, attrs)

```

```py
class FunctionMeta(type):
	def __init__(cls,name,bases,attr):
		super(FunctionMeta,cls).__init__(name,bases,attr)
class Function(metaclass=FunctionMeta):
    pass

super(cls1,cls)   #在mro中，找从cls1开始，第一个继承自cls1（基本是本本身）的类，然后调用__init__
```



## 实例类

```
继承object
执行创建对象的时候：
object = Foo()   等价于
obj = Foo.__new__(Foo,*args,**k)	#创建一个对象
obj = Foo.__init__()
```

```py
class A(father):
    def __new__(self,*args,**kwargs):
        return super().__new__(cls)
    
	def __init__(self, args):
		super(A,self).__init__() #super().__init__()
----------------------------------------------------
#__new__
-最先调用的函数，用于实例化类
#__dict__
a = A()
print(a.__dict__) 
-输出所有的实例变量的内容
#__call__
class Adder:
    def __call__(self, x, y):
        return x + y
a = Adder()
print(a(3, 4)) 
#__getitem__
class MyList:
    def __init__(self, data):
        self.data = data

    def __getitem__(self, index):
        return self.data[index]
ml = MyList([10, 20, 30])
print(ml[1])  # 输出 20
#__str__    print(p)
def __str__(self):
        return f"Person(name={self.name})"
p = Person("Alice")
print(p)  # 输出：Person(name=Alice)
#__len__    len(p)  返回值
#__iter__   for i in c:     #__next__
class A:
    def __iter__(self):
        return self
    def __next__(self):
        if self.current <self.limit:
            val =  self.current
            self.current+= 1
            return val
        else:
            raise StopIteration
            
 c= A(3)
for i in c:  

#__getattr__ 转发默认不存在属性
#  模块 类 类实例 函数
getattr(object, name[,default])
```

## super

```py
super()  #推导外部类 推导当前对象实例
```





## 属性

### 类属性  

- **定义在类体中**，用于共享某些数据给所有实例使用。
- 相当于静态变量

### 对象属性

- **定义在对象的 `__init__` 方法中**，每次创建实例时都会为该对象赋予新的属性值。
- 相当于普通变量



## 方法

```python
setattr(object, name, value)   #作用：给对象 obj 动态设置一个属性名 name，值为 value
serattr(self,name,value)
```



### @property    ---动态属性  ----- 

`@property` 是一个**装饰器**，它将一个方法转变为**属性**的 getter 方法

```py
class rectangle:
	def __init__(self,width,height):
		self._width = width
		self._height = height
		
	@property
	def area(self):
		return self._width* self._height
```

### 描述符

- 允许把一个类属性，托管给一个类，这个属性就是一个「描述符」。

- 拥有以下定义则可以为描述符

- **`__get__(self, instance, owner)`**：在访问属性时调用，允许控制属性访问的返回值。

- **`__set__(self, instance, value)`**：在为属性赋值时调用，允许控制属性赋值行为。

- **`__delete__(self, instance)`**：在删除属性时调用，允许控制属性删除行为。

```py
class MethodDescriptor():
	def __get__(self, instance, owner):
        owner #自动识别
        instance #自动识别
        return self.method()
        
```



### 类方法

- 使用cls

- 相当于静态方法

```
  # 类方法
    @classmethod
    def get_species(cls):
        return cls.species
 	@staticmethod
```









## ABC   抽象基类

```py
@abstractmethod #抽象类修饰
```







#  函数

## setattr

```
setattr(object, name, value) #对象，属性名，值
```



## eval- compile

```py
expression = compile(expr,"<expr>","eval")
#compile(source,filename,mode)
filename: <string> <expr>
mode: "eval"单表达式,"exec"多行语句,"single"单行语句 
#eval(expression, globals=None, locals=None)
动态执行/求值expression，返回
```

## globals



## locals







## python函数调用机制

- 可以接收比定义更多的参数









## ...   

- 函数未定完

```py
def unfinished_function() -> None:
    ...
```



- **类型注解中的可选参数**

```py
#类型注解（Type Annotations）
def greet(name:Optional[str] = None):

```

- 变长参数

```
from typing import Tuple

def process_data(data: Tuple[int, ...]) -> None:
    for item in data:
        print(item)
# 测试
process_data((1, 2, 3))  # 输出: 1 2 3
```





- Ellipsis 对象，与数组索引相关联   

```py
import numpy as np

# 创建一个 3x3x3 的数组
arr = np.random.rand(3, 3, 3)

# 使用 ... 省略部分维度
print(arr[..., 0])  # 输出第一个维度的所有切片
```

## *   / 

 **`*` 和 `/` 限制参数传递方式**：

- *之后的参数必须通过关键字传递
- /之前的参数必须通过位置传递









# 文件

## yaml

```
with open as :
	cfg = yaml.safe_load(f)

```



```py
with open("ss.txt",r) as r:
	for line in r:
	
    
    line=file.readline()
    lines=file.readlines()
    ##########################
    reads=r.read(size)			#可以限制读取的大小
    ##########################
    st=r.seek(5)
	now=r.tail()	#当前位置    
	##########################
    r.write(string) #写操作
    ##########################
    r.close()   
    
    #对line操作
   line.strip()#用于删除每行末尾的换行符或其他空白字符。
   line.split()
	#默认不传入参数时，split() 会将字符串按照**任意数量的空白字符（包括空格、制表符 \t、换行符 \n 等）**进行拆分。
#它会自动忽略连续的多个空白字符，所以无论有多少空格或其他空白字符，它都能正确拆分。
   line.startswith(prefix)
   line.find(substring)
```

## StringIO

- 创建一个类似文件操作，但是是存储于内存的模块

```py
import StringIO
io=StringIO()
getvalue()
```

## CSV

### Writer

```
writer = csv.DictWriter(f, fieldnames=["x","y","label"])
writer.writeheader()
writer.writerow({"x":1,""...})	/rows
writer = csv.writer()
```

### Reader

```
import csv
with open("example.csv",'r') as file:
	reader = fcsv.DictReader(file)
	csv.Reader
```









# 视觉 CV

## 灰度图

```py
imggrey=cv2.imread(image_path,cv2.IMREAD_GRAYSCALE)
```

- 值在0-255之间



## PIL -img

- (height, width, channels)



## LBP

- 局部二值模式       

```py
skimage.feature.local_binary_pattern(
            img[:, :, colour_channel], 8, 1.0)
```

- 邻域点数量
- 邻域半径r

### LBP 的示例

假设有一个 3x3 的邻域，中心像素的灰度值为 `50`，周围像素的灰度值如下：

```
[60, 55, 45]
[70, 50, 40]
[80, 65, 35]
```

比较中心像素 `50` 与周围像素的灰度值，生成二进制模式：

- 从左上角开始，顺时针方向比较：`60 >= 50` → `1`，`55 >= 50` → `1`，`45 < 50` → `0`，`40 < 50` → `0`，`35 < 50` → `0`，`65 >= 50` → `1`，`80 >= 50` → `1`，`70 >= 50` → `1`。
- 二进制模式为：`1 1 0 0 0 1 1 1`。
- 转换为十进制：`1*2^7 + 1*2^6 + 0*2^5 + 0*2^4 + 0*2^3 + 1*2^2 + 1*2^1 + 1*2^0 = 231`。

因此，该中心像素的 LBP 值为 `231`。









# OS

## 多进程

```py
import os

pid=os.fork()
if pid == 0:
	do something
else:


#等操作
os.wait()


#子进程
import subprocess
```







### windows-pool

```py
from multiprocessing import Process
from multiprocessing import Pool


p1=Process(target=func, args=())

p1.start() 	

p1.join()


############

p=Pool(4)

#apply_async(func,*args)
p.apply_async()


p.close()
p.join()

```





# 事件循环

- 调度和执行协程









# 异常

- 属于高级语言层面的中断

```
try:
	do_something
except:
	do_something
```

- raise 可以抛出错误然后继续传递
- try执行模块在发现错误后，会匹配一个except，然后执行内部代码





# python

```
1一切都为对象 
-基本类型：字符串、数字、布尔值等
-容器类型：列表、元组、字典等
-函数：普通函数、lambda 函数
-类：类对象本身（不是实例）
-类实例：类的实例化对象
-模块：导入的模块

```



## 作用域与闭包

local---Enclosing---Global---Built-in

- 遵循LEGB规则，闭包可以访问外部函数的局部变量

- 内层闭包更改外部变量

  ```py
  # nonlocal    
  --修改外层变量     
  # global
  ----修改global变量
  ```
  

## 结构

### 函数

```py
-函数的调用栈帧    #stack frame  
# f_locals      局部变量
# f_globals     全局变量 
# f_builtins    内置变量 
```

## 类

```py
#MRO 类解析顺序
[<class 'D'>, <class 'B'>, <class 'C'>, <class 'A'>, <class 'object'>]  
```







## 代码检查工具

- flake8





## 加载

```py
#python模块搜索sys.path
只决定 Python  import 的“顶层模块/包”从哪儿找（.py、.pyc、.so 这些文件本身的路径）
-内建/冻结模块（如 sys）-当前包/工作目录-sys.path 列表里的各个目录（site-packages、PYTHONPATH 等）
（ep：PYTHONPATH=/path/extra python script.py 增加搜索目录）
-当 import 的是 C 扩展模块（比如 foo.cpython-37m-x86_64-linux-gnu.so），Python 只是用 sys.path 找到这个 .so 文件的“路径”，但是接下来的so中找其他so文件路径，由动态链接器管理

```





# cpp动态库



## C Extension

- 支持加载.so/.pyd动态库

```cpp
// mymodule.cpp
#include <pybind11/pybind11.h>
namespace py = pybind11;
py::module_ m;
int add(int a, int b) { return a + b; }

// PyObject*
//PyObject_Call  PyCFunction
python - << 'EOF'
import carla

client = carla.Client('localhost', 2000)
client.set_timeout(5.0)
world = client.get_world()

tl = world.get_actors().filter('traffic.traffic_light*')[0]

print('before:', tl.get_elapsed_time())

tl.set_elapsed_time(3.0)

for i in range(40):
    world.wait_for_tick()
    print(f'after tick {i+1}:', tl.get_elapsed_time())
EOF


//注册c-extension模块
PYBIND11_MODULE(mymodule, m) {
    m.def("add", &add);
    py::class_<MyCounter)>(m,"Counter")
        .def(py::init<int>())
        .def("inc", &MyCounter::inc)
        .def("get", &MyCounter::get);
    	.def_property("val", &MyCounter::get, [](MyCounter& self, int v) { self = MyCounter(v); });
}
```



## PyMODINIT_FUNC



## Pybind11







# PYTORCH

## 数据

```py
#数据流动
train_loader = DataLoader(train_ds,batch_size)---
for batch in train_loader:  #委托迭代器 iterator
---- __iter__ #生成索引数组 --shuffle --batch_idxs = idxs[start:end]


```

