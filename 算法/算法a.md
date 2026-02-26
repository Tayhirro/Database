---
title: 查找
date: "2026-02-04"
categories:
  - 算法
description: 使用ll和rr界定左右窗口范围
---
## 滑动窗口

- 使用ll和rr界定左右窗口范围









# 查找

## 线性搜索

- min  max
- 选择其中第k小的数
  - 滑动窗口
  - pivot划分 ---结合快排



## 二分

- 成功查找
- 不成功查找
  - 返回较小的值   （一般来说）
  - 返回较大的值

```cpp
int q;
int sorted[1000];		//待查找有序数列

int halffind(int q,int sorted[],int ll,int rr){
    while(ll<rr){
        int mid = (ll+rr)>>1;
        if(sorted[mid]==q){
            return mid;
        }else if(sorted[mid]>=q){//如果有重复元素  
            rr=mid-1;
        }else
            ll=mid+1;
        }//返回最小的那一个，所以会造成如此    
    }
	if(sorted[ll]>=q){
        return ll;
    }else{
        return ll+1;
    }
	
    return ll;
}


```

- 对于此方法   返回的是最后一个小于target的后面一个位置 反之也行
  - 因为对于循环执行，我们知道ll=rr退出，则还没有判断这个点与搜寻的点的比较

```
int  binary_search(int target,vector<int> vs){
    int lenvs = vs.size();
    int ll=0,rr = lenvs-1;
    
    while(ll<rr){
        int mid  = (ll+rr)>>1;
        if(vs[mid]>=target){
            rr = mid;
        }else{
            ll = mid+1;
        }
    }
    if(vs[ll]>target){
        return ll-1;
    }else{
        return ll;
    }
}
```

- ll一定为mid+1 



- 出去判断







### 重复数据

- 返回重复数据最开始的数据











# 排序

## stl排序

```cpp
#include<iostream>
#include<algorithm>

int main(){
    std::vector<int> vec = {1,5,2,3,5};
    std::sort(vec.begin(),vec.end());
    std::sort(vec.begin(),vec.end(),[](int a,int b){return a>b;});
    
    
    for(int num:vec){
    }
    
    
}
```

















## 原地置换排序

```cpp
void sort(vector<int>& dos){
	int i=0;
	for(;i<dos.size();i++){
		if(dos[i]!=i){
			int temp=dos[i];
			dos[i]=dos[temp];
			dos[temp]=temp;
			/*
			int temp=dos[dos[i]];
			dos[dos[i]]=dos[i];
			dos[i]=temp;
			*/
		}
	}
	

}

```



## 快速排序

- 其中之一不参与排序，则初始**他没参与比较，最后需要比较**

  - 由于可能出现初始就l=r的情况

  

  ```cpp
     void quicksort(vector<int>&documents,int lefts,int rights){
               if(lefts>=rights){
                  return;
              }
              int div=documents[lefts];
              int left=lefts+1;
              int right=rights;
              while(left<right){
                  while(left<right){
                      if(documents[left]>div){
                          int temp=documents[left];
                          documents[left]=documents[right];   
                          documents[right]=temp;
                          right--;
                          break;
                      }else{
                          left++;
                      }
  
                  }
                  while(left<right){
                      if(documents[right]<div){
                          int temp=documents[right];
                          documents[right]=documents[left];
                          documents[left]=temp;   
                          left++;
                          break;
                      }else{
                          right--;
                      }
                  }
              }
              if(documents[left]>div){
                  int temp=documents[left-1];
                  documents[left-1]=documents[lefts];
                  documents[lefts]=temp;
                  quicksort(documents,0,left-2);
                  quicksort(documents,left,rights);
              }else{
                  int temp=documents[left];
                  documents[left]=documents[lefts];
                  documents[lefts]=temp;
                  quicksort(documents,0,left-1);
                  quicksort(documents,left+1,rights);
              }
  
      }
  ```

- 需考虑quicksort的迭代内容



- 其中之一参与排序,则可以节省，需要保存这个标志，空间可以用作移动,当前恐空间已经被移动到另外一个地方，所以可以修改

  ```cpp
  void quicksort(vector<int>& ar,int l,int r){
      if(ll>=rr){
          return 0;
      }
  	int ref=ar[l];
      int ll=l;
      int rr=r;
      while(ll<rr){
      	while(ll<rr){
              if(a[rr]<ref){
  				a[ll]=a[rr];
                  ll++;
                  break;
              }else{
                  rr--;
              }
          }
      	while(ll<rr){
              if(a[ll]>ref){
                  a[rr]=a[ll];
                  rr--;
                  break;
              }else{
                  ll++;
              }
          }
  	}
      a[ll]=ref;
      quicksort(ar,l,ll-1);
      quicksort(ar,rr+1,r);
  }
  ```

## 二路归并排序

```cpp
void tworoadsort(vector<int>&ps,int ll,int rr){
    int mid=(ll+rr)/2;
    if(ll>=rr){
        return;
    }
    tworoadsort(ps,ll,mid);
    tworoadsort(ps,mid+1,rr);
    
    
    int ln=ll;
    int rn=mid+1;

    vector<int> res;
    while(ln<=mid&&rn<=rr){
        if(ps[ln]<ps[rn]){
            res.push_back(ps[ln]);
            ln++;
        }else{
            res.push_back(ps[rn]);
            rn++;
        }
    }
    while(ln<=mid){
        res.push_back(ps[ln]);
        ln++;
    }
    while(rn<=rr){
        res.push_back(ps[rn]);
        rn++;
    }
    for(int j=ll;j<=rr;j++){
        ps[j]=res[j-ll];
    }
}
```

- 可以通过俩数组来回倒 加速合并速度







## shell排序





## 堆排序

- 0-n-1   
  - 左子树 2*i+1
  - 右子树 2*i+2

- 1-n
  - 左 2*n
  - 右2*n+1
- 考虑临界范围 left---right是否可以取的到











# 正则

```cpp
#include<regex>
using namespace std;
smatch res;	//存放结果

string str("123455555");
string pattern("4445232");


regex r(pattern);	//初始化类

```

## 方法

### regex_match

```cpp
  /**
   * @brief Determines if there is a match between the regular expression @p e
   * and a C-style null-terminated string.
   *
   * @param __s  The C-style null-terminated string to match.
   * @param __m  The match results.
   * @param __re The regular expression.
   * @param __f  Controls how the regular expression is matched.
   *
   * @retval true  A match exists.
   * @retval false Otherwise.
   *
   * @throws an exception of type regex_error.
   */
  template<typename _Ch_type, typename _Alloc, typename _Rx_traits>
    inline bool
    regex_match(const _Ch_type* __s,
		match_results<const _Ch_type*, _Alloc>& __m,
		const basic_regex<_Ch_type, _Rx_traits>& __re,
		regex_constants::match_flag_type __f
		= regex_constants::match_default)
    { return regex_match(__s, __s + _Rx_traits::length(__s), __m, __re, __f); }
```

- regex_match(std::string,smatch results,regex r)
- 正则匹配函数

### regex_search

```cpp
smatch regex_search(std::string ss,smatch res,regex r);

smatch regex_search(string::iterator beg,string::iterator end,smatch res,regex r);


```

### regex_replace

```cpp
regex(std::string ss,regex r,std::string wait)
```

#### 捕获组

```cpp
string str2 = "Hello_2019!";
regex r2("(.{3})(.{2})_(\\d{4})!");  //匹配3个任意字符+2个任意字符+下划线+4个数字+!
cout << regex_replace(str2, r2, "$1$3") << endl;	//输出：Hel2019，将字符串替换为第一个和第三个表达式匹配的内容
cout << regex_replace(str2, r2, "$1$3$2") << endl;	//输出：Hel2019lo，交换位置顺序
```



## 细则方法

```cpp
regex::icase//表示匹配时忽略大小写
```







# BFS

- 状态转移



- 附加内容的定义









# 单调

## 单调栈

- 要求**栈内**的**顶**到**底**是单调的

```cpp
#include<bits/stdc++.h>
#define stack_size 100
typedef struct stack{
	int size;
	int stack[stack_size];
	int top;
}stack;
```







## 单调队列

- 单调队列和滑动窗口关联 
  - 滑动窗口确实需要弹出数据，但是由于使用单调队列，所以单调队列**不一定弹出**









# 数据结构

## 队列

```cpp
int queue[N];
int hh,tt;

//hh==tt 表示有一个数
//tt+1=hh表示队空
```

- 循环队列





## 堆

### 对顶堆

- ```
  priority_queue<int,vector<int>,less<int>> a;//大根堆
  priority_queue<int,vector<int>,greater<int>> b;//小根堆
  ```

  - 1----k 小的数存放在a

  - 比第k个数大的存放在b

- 可以动态调整

```
#include<bits/stdc++.h>
using namespace std;
priority_queue<int,vector<int>,less<int>> a; 
priority_queue<int,vector<int>,greater<int>> b;
int length=0;
void insert(int num){
	if(a.empty()||num<=a.top()){
		a.push(num);
	}else{
		b.push(num);
	}
	length++;	 //调整中位数这个位置
	if(a.size()>length/2+1){
		b.push(a.top());
		a.pop();
	}else if(b.size()>length/2){
		a.push(b.top());
		b.pop();
	}
}
int query(){
 	return a.top();
}
int main(){
    //维护中位数
    int n;
    cin>>n; //插入n个数
    int length=0;
    while(n--){
        string s;
        cin>>s;
        if(s[0]=='I'){
            int num;
            cin>>num;
            insert(num);
        }else if (s[0]=='Q'){
            cout<<query()<<endl;
        }
    }
    return 0;
}
```



### 配对堆

- 对于树状堆型结构，都是顶层到底层逐层（增加或者减少）



- 可并堆   可以将两个堆合并为一个新的堆，同时保留性质

- 注意堆的堆顶都只有**一个元素**









#### 边界条件

- 尽量多考虑   然后返回就行

- 合并兄弟    是     x  y  z  

  - 合并 xy   

  - 递归合并z  然后 合并xy  ---z

  - 递归，所以从右向左顺序

```cpp
#include<bits/stdc++.h>
using namespace std;


template <typename T>
struct Node{
    T value;
    struct Node * next;
};
template <typename T>
struct MatchHeap{   
    T value;
    Node<T> * child,*sibling;
};


int main(){
    return 0;
}

Node* meld(Node* x, Node* y){
	if(x==nullptr){
        return y;
    }
    if(y==nullptr){
        return x;
    }
    if (x->v > y->v) std::swap(x, y);  // swap后x为权值小的堆，y为权值大的堆
  // 将y设为x的儿子
  y->sibling = x->child;
  x->child = y;
  return x;  // 新的根节点为 x
    
    
}

Node* merges(Node* x){
    if(x->next==nullptr||x->next==nullptr){
        return x;
    }
    Node* y = x->next;
    Node* c = y->next;
    x->next=y->next=nullptr;
    return meld(merges(c),meld(x,y));
}

Node* delete_min(Node* x) {
  Node* t = merges(x->child);
  delete x;  // 如果需要内存回收
  return t;
}
```

- 合并  最新的开始合并
- merges返回一棵**子树**     
- merges(c),meld(x,y) 即老儿子构成一颗子树

```
Node *decreade_key(Node* root Node * x,V value){
	//判断是否为根
	
	//维护父节点和该节点   //如果为sibling 可以直接减少

}


```











## 数组扩展

一维数组----二维数组

```cpp
 int k;
 int kx=k/3;
 int ky=k%3;
```

## 矩阵压缩存储

### 三角矩阵









## 哈希表存储















## 图



## 邻接表

```cpp


```



## 邻接矩阵

```cpp
int a[N+1][N+1]={}
//其中 Axy为x到y的距离

//也可以使用 N N

```





## 链式前向星

```cpp
head[]	//头指针
//h[x]表示以x开头的所有出去的边

//边数组，存储所有边 
edge{    
    edgeto[]
    edgew[]	
    edgenext[]  
}

```

- head存头
  - 指向第一条边
- edge存所有边
  - to
  - value





















# 并查集

## 路径压缩

- 在find的过程中进行路径压缩





## 阿克曼函数

![image-20241016010326143](C:\Users\Tayhirro\AppData\Roaming\Typora\typora-user-images\image-20241016010326143.png)









# 树

















## 多叉树

- 兄弟-儿子 表示法

- 存储双亲节点指针

- 多重链表表示法

  ```cpp
  struct TreeNode{
  	int value;
  	TreeNode* children[3];
  	TreeNode(int val):value(val){
  	for(int i=0;i<=2;i++){
  	children+i = nullptr;
  	}
  	}
  }
  ```

  

```cpp
template <typename T>
struct Node{
	struct Node* child,sibling;
	T v;
}

//插入
//根节点合并
y->sibling=x->child;
x->child  = y;

```











## 左偏树

- dist   --->到**子树**的**外节点**的经过**边数**

- 左儿子的dist>=右儿子的dist
- 每个节点的dist = 右儿子dist+1



- 空 和 点合并 --最终条件
- 外节点（子节点<=2）
- 空节点 （二叉树 null 数组）
  - dist=-1 |0



对于结构体

- fa
- ch1，2
- d







### 操作

- 合并  插入

- 删除头

- 删除任意节点 

- ```
  int& rs(int x) { return t[x].ch[t[t[x].ch[1]].d < t[t[x].ch[0]].d]; }
  
  // 有了 pushup，直接 merge 左右儿子就实现了删除节点并保持左偏性质
  int merge(int x, int y) {
    if (!x || !y) return x | y;
    if (t[x].val < t[y].val) swap(x, y);
    int& rs_ref = rs(x);
    rs_ref = merge(rs_ref, y);
    t[rs_ref].fa = x;
    t[x].d = t[rs(x)].d + 1;
    return x;
  }
  
  void pushup(int x) {   //存有dist更新
    if (!x) return;
    if (t[x].d != t[rs(x)].d + 1) {
      t[x].d = t[rs(x)].d + 1;
      pushup(t[x].fa);
    }
  }
  
  void erase(int x) {
    int y = merge(t[x].ch[0], t[x].ch[1]);
    t[y].fa = t[x].fa;
    if (t[t[x].fa].ch[0] == x)
      t[t[x].fa].ch[0] = y;
    else if (t[t[x].fa].ch[1] == x)
      t[t[x].fa].ch[1] = y;
    pushup(t[y].fa);
  }
  ```

  



## 二叉树





## 二叉查找树

```py
class BinarySearchTree:
	def __init__(self):
		self.root = None
	def insert(self,kvalue):
     	if self.root == None
        	self.root = BinaryNode(kvalue)
        else:
            _insert(self.root,kvalue)
    
    #node   value
    def _insert(self,node,kvalue):
        if node == None:
            node = new BinaryNode(kvalue)
        elif kvalue < node.value:
            _insert(node.left,kvalue)
        elif kvalue >= node.value:
            _intert(node.right,kvalue)
    	
        
        
class BinaryNode:
    def __init__(self,value):
		self.value = value
        self.left = None
        self.right = None
        
```

- 插入的递归
  - 根据**本节点**空来判断是否创建节点
  - 根据**左右子节点**空判断是否创建节点



### 删除

- 对树，避免对root节点讨论，直接定义parent_node 为None



- 递归删除

  - 递归删除左右子树
  - 只删除根节点 
  - 保存parent_node

  ```py
   def _delete_recur(self,node,value,parent_node):
          if node.value ==value:
              if node.left == None and node.right == None:
                  if parent_node == None:
                      self.root = None
                  elif parent_node.left == node:
                      parent_node.left = None
                  else:
                      parent_node.right = None
              elif node.left == None:
                  if parent_node == None:
                      self.root = node.right
                  elif parent_node.left == node:
                      parent_node.left = node.right
                  else:
                      parent_node.right = node.right
              elif node.right == None:
                  if parent_node == None:
                      self.root = node.left
                  elif parent_node.left == node:
                      parent_node.left = node.left
                  else:
                      parent_node.right = node.left
              else:
                  node_temp = node.right
                  while node_temp.left != None:
                      node_temp = node_temp.left
                  node.value = node_temp.value
                  self._delete_recur(node.right,node_temp.value,node)
          elif value < node.value:
              self._delete_recur(node.left,value,node)
          else:
              self._delete_recur(node.right,value,node)
  ```

- 非递归删除
  - 找到node ，父节点
  - 删除一样的

- 删除过程
  - 左右有空 ---更改对应子树为父节点的左右值
  - 左右都有值  ---更改为左树右值 /右树左值





## AVL树













## BTree

- 原理：由于存储在硬盘的内容访问会很慢，所以需要扩展节点的存储   --数组

```py
class BTreeNode:
    def __init__(self, t, leaf=False):
        self.t = t              # 最小度数 (每个节点至少有 t-1 个键)
        self.keys = []          # 节点中的键
        self.children = []      # 子节点
        self.leaf = leaf        # 是否为叶子节点
 
```



```python
clas BTree:
	def __init__(self, t):
		self.root = BTreeNode(t, leaf=True)  # 初始化根节点
		self.t = t                           # 最小度数
	#在b树中插入k键
	def insert(self, k):
	"""在 B-Tree 中插入键 k"""
        root = self.root
		if len(self.key) == self.t*2 -1 :
            new_root = BTreeNode(self.t, leaf=False)
            new_root.children.append(root)  # 将原根作为新根的子节点
            self.split_child(new_root, 0)   # 分裂原根
            self.root = new_root            # 更新根节点
            self.insert_non_full(new_root, k)
        else:
            self.insert_non_full(root,k)	#递归插入逻辑
	

```

- 插入都需要递归，也就是最终归化到**该节点**上





### 分裂

- 分裂













-  m阶B树 
  - 每个节点最多有m个子节点
  - 每个非叶子节点 最少有「m/2」个子节点  ，至少有「m/2」-1个关键字

- 叶子节点 （外部节点） 无信息  



### B+树

- 叶子节点存储数据























```cpp
std::stack<T> stacks;

//到达树的最左端：
while(node->left!=nullptr){
    //lastVisited=node;
    node=node->left;
    stacks,push(node);
}

//遍历可以考虑基础情况



//到达最左端
     |
//回溯，中序遍历则先输出上节点
//回溯，后序则考虑去右节点
while(node->right==nullptr)
//在后序中，可能是右节点已经寻完，也可能没有寻过
   |
//增加lastVisited结点


```



# 块





# 寻路算法

- 多对1  1对多  反过来

- 从前向后   djistra   s--所有
- 从后向前  多阶段图   所有---d











## djistra

- 搜索最短的边 由于要更新最短路径，所以需要每次确保是局部最优
- 存储有所有距离的数组，可以直接利用







```cpp
void djistra(){
    //定义一个dis数组
    dis[N]=0x3f3f3f; 	//无穷远
    //定义一个found数组
    Found[N]=0; 表示没添加完
   //每次搜索图中最近的点----dis[N]数组
   int temp =find_closed();
   //然后去更新dis和Found
    
    
}
//使用Found去判断是否被访问过



```

- 点遍历
- 搜索周围点，每次更新可以到达的周围点的距离



```python
dis =[]
for(node in node_not_travel):	
	#dis更新
   i=head[node]
   while i!=-1
    dis[e[i].to]=min(dis[e[i].to],a+b)
   	i=e[i].ne
```

- 更新距离点为所有点//或者为连接的周围点也可以
- a+b>c







### 堆优化







## bellman-ford

- 边遍历
- 搜索所有边，每次更新所有点距离

- 优化k次所有边，然后看可否到达

```
void bellman-ford (orgin dest){
	distance[s] ={∞}
	distance[0] ={0}
	if distance[u]+w(u,v)<distance[v], then distance[v]=distance[u]+w(u,v)
	
}




```

- 只经过所有点一次   最多v-1条边
- 松弛操作 ---松弛所有边    ==实际上是想松弛起点到终点的路











## spfa---宽搜优化

- 在判断负环时，由于n个点之间最多有n条边相联系，大于n之后说明一个点经过两次，代表存在负环

- n限制的时候，由于有bfs所以可以判断至少k次到达终点



- 使用一个队列
- 更新点优化

- 弹出点去松弛周围点









### 判断负环

- 加入所有点，可能不连通
- 宽搜去松弛周围边，负环是通过记录
  - 边数是否重复   边少
  - 点数是否重复   点少
- cnt是向上一个cnt的位置继承





```cpp
queue q；

//q push条件是一旦有点可以优化，则存入

//否则只需要更新dis就可以
   
```







## floyd 

- 一般邻接矩阵来存储







# 拓扑

- 入度 --指向该边的数量
- 出度 --从该边出去的数量



```py
import queue
queues=queue.Queue()

while queues.empty()==False:
    #弹出队列的第一个元素
    node=queues.get()
    #删与其相连的边的to的度数
#...
    
    #将度数为0的点加入队列

    for node in nodes:
        if nodes_in_0(node):
                queue.push(node)

```



# 生成树

## prim

- 注意dist数组更新，不是到第一个点，直接更新距离就行





- 图的稠密和稀疏





## Kruskal

-  并查集





# 图论

## 二分图

- G为二部图的充要条件是G中的每一个圈的长度都是偶数。
- 二部图的最大匹配

### 最大匹配

- match[x]==0 ||find(match[x])
- st每次重置











# DP

- 集合分化

- 考虑分化后的限制条件



- 倒序可以增加条件







## 闫式DP分析



- 完全背包

![image-20241107175411926](C:\Users\Tayhirro\AppData\Roaming\Typora\typora-user-images\image-20241107175411926.png)

- 推理

```c
f[i][j]=max(f[i][j-1],f[i-1][j-v[i]]+w[i])

//完全背包
f[i][j]=max(f[i][j-1],f[i][j-v[i]]+w[i])

```

- 也可以这样理解，递推关系
- 分化》 有0个i的基础上的前i个物品---有1个i的基础上的前i个物品
- 由于有1个i的基础上的前i个物品》递推分化

```c
f[i][j]=max(f[i][j-1],f[i][j-v[i]]+w[i])
```



- 对背包的优化


- 降维---在算后面的时候如果前面被算出来，则可以优化
- 减枝---去掉不可到达的路径



## 多重背包

```
	int ns,vs;
    cin>>ns>>vs;
    int v[N]={0};
    int w[N]={0};
    int s[N]={0};

    int dp[N][N]={0};    
    int vi,wi,si;
    for(int i =1;i<=ns;i++){
        cin>>v[i]>>w[i]>>s[i];
    }
    for(int i=1;i<=ns;i++){
        for(int j=1;j<=vs;j++){
            for(int k=1;k<=s[i];k++){
                if(j>=k*v[i]){
                    t = max(dp[i-1][j],dp[i-1][j-k*v[i]]+k*w[i]);
                    dp[i][j] = max(t, dp[i][j]);
                }else{
                    dp[i][j] = max(dp[i][j],dp[i-1][j]);
                }
            }
        }
    }



```



- 对于多重背包的多次更新，需要考虑到后一个和前面所有的max取值

- 优化

```
for(int i =1 ;i<=ns;i++){
	for(int j =1;j<=vs;j++){
		for(int k =0 ;k<=s[i]&&j>=k*v[i];k++){
			dp[i][j] = max(dp[i][j],dp[i][j-k*v[i]]+w[i]);
		}
	}
}
```

## 分组背包



## 二进制优化

- 将s个拆分后化为0-1背包

## 区间DP







## matrix

- 状态 
  - 子问题长度 
  - 从 i--j
- 划分

















## 表述优化

```
    for i in range(x + 1, x + m + 1):
        for j in range(y, y + i - x + 1):
            # 从上方来
            from_top = dp[i - 1][j] if j < len(tritangle_list[i - 1]) else 0
            # 从左上方来
            from_top_left = dp[i - 1][j - 1] if j - 1 >= 0 and j - 1 < len(tritangle_list[i - 1]) else 0
            # 当前点的值
            current_value = tritangle_list[i][j]
            
            # DP状态转移
            dp[i][j] = max(from_top, from_top_left) + current_value

```

- 完美变量替代







## 最长子序列

- dp考虑状态     以i结尾的
- 以i开头的    --倒推



### 最大子矩阵

- 循环两次就行

### 最大m字段和



# 基础理解

## 结构

### 属性

### 方法

- 输入(参数)   返回值 (递归)   

#### 理解

- 可以将返回的内容看作一个整体 中间过程作为黑盒



## 取整

- 对于取整的问题    --`(n-5)/10`  --- 基本上是减半







## 数组

- 向下取整

- 考虑个数  +1   考虑gap  +0（）  **length-1=gap**
- 只能对len进行更改，/2之前













- 对边界条件分析
  - 确定实体 
  - 实体 绑定在矩阵 数组中  分布

```py
def recurMatrixs(i,j): #i j 是长度        
    n = j-i     
    dp = [[0]*n for _ in range(n)]  
    for gap in range(1,n): # 涵盖长度   gap 表示也行
        for i in range(n-gap):
            j = i + gap 
            dp[i][j] = float('inf')
            for k in range(i,j):
                dp[i][j] = min(dp[i][j],dp[i][k]+dp[k+1][j]+matrix_list[i]*matrix_list[k+1]*matrix_list[j+1])
    return dp[0][n-1]
```

- i j   可以考虑  为  矩阵       --->   矩阵 从0开始   --- >i，j从0开始 
- gap 为1---n-1



















## 循环

- 范围循环

```
for(declaration : expression){
}
```

- 声明变量来循环可迭代对象



## 并查集

- father
- 等效于右father结构体



# 贪心

```cpp
//数组问题
-最大覆盖  --允许重叠 
sort(edges.begin(),edges.end(),[](const pair<int, int>& a, const pair<int, int>& b) {
    return a.first < b.first;
});



```





# 并行



## reduce

- 它提供了一种高效的方式来在范围上执行并行或串行的加总、积累等操作





## equal

```cpp
template<class InputIt1,class InputIt2>
bool equal(InputIt1 first1, InputIt1 last1,
           InputIt2 first2);
//比较到第一个结束位置

template <class InputIt1, class InputIt2>
bool equal(InputIt1 first1, InputIt1 last1,
           InputIt2 first2, InputIt2 last2);

//比较到较短的那一个为止


template <class InputIt1, class InputIt2, class BinaryPredicate>
bool equal(InputIt1 first1, InputIt1 last1,
           InputIt2 first2, BinaryPredicate p);

p自定义谓词
```







# 优化

## 计算

- 求和   用一个sum=temp1+temp2 这俩temp可变，解耦

- 遍历   start     to     start+step



## 结构

- 维护的时候，可以封装一个结构  
- 关于封装，一般来说只需要封装一个节点即可  对于整体结构，项目复杂程度来进行封装









## 主结构确定



## 模板

- 通常定义在头文件中



- 模板类 内部再定义模板函数  需要**单独声明**





## 友元

- 友元函数适合 工具函数 ，跨类操作 ，运算符重载

- 私有函数  +friend  适合跨类合作  ，内部工具函数





# 项目结构

- 类   函数声明  放于头文件
- 模板定义 放于头文件









# 递归

- 考虑终止条件
- 执行流程
  - 递归前
  - 递归时
  - 递归后
  - 返回

## 回溯

- 解向量  --解空间
- 显式约束
- 隐式约束 之间



- 子集树
  - 分为取或不取---不同情况
- 排列树







### 分支限界

```
if(constraint(t)&&bound(t)){
	backtrack(t+1);
}
```

- 约束函数   --满足约束条件 
- 限界函数



- 根据情况来看是否进行分支限界







## DFS

- 对于终止条件在最后特定情况（如最后遍历某一个点），他的更新只能在最后发生    --所以没必要在中间的时候利用返回值（比如最短子路径）去更新
  - 但是要注意**回溯**



-  使用dfs时，可以带有信息

```cpp
int globalinfo=0;
int dfs(int i){
	st[i]=true;	// 标记访问 
	//可以在此对全局信息处理


	//
	return 相关信息
}
```

- 对树的dfs有比较好的单向性，删除一节点后子树会完全隔开





### DFS推广

- DFS中间分流   

```
    void traverseDFS(TreeNode* leftChild, TreeNode* rightChild, int level) {
        if (leftChild == nullptr || rightChild == nullptr) {
            return;
        }
        // If the current level is odd, swap the values of the children.
        if (level % 2 == 0) {
            int temp = leftChild->val;
            leftChild->val = rightChild->val;
            rightChild->val = temp;
        }

        traverseDFS(leftChild->left, rightChild->right, level + 1);
        traverseDFS(leftChild->right, rightChild->left, level + 1);
    }

```



## 递归返回函数



## 递归返回内容













# 循环

- 应该考虑**最优先级   主要循环影响**  （最合理的循环）



```
给定一个含有 n 个正整数的数组和一个正整数 target 。

找出该数组中满足其和 ≥ target 的长度最小的 连续子数组 [numsl, numsl+1, ..., numsr-1, numsr] ，并返回其长度。如果不存在符合条件的子数组，返回 0 。

```

- 考虑滑动窗口，主循环  为total>target
- 每一次扫描保留上一个mins即可，然后继续更新下一个是可以优化的







- 循环 次数 







# 边界问题

- 考虑首尾情况，需要特殊考虑















# 队列

- rr是否存值
  - 决定rr的范围
  - 决定判空    



















# NP完全

- P类问题是指可以在多项式时间内解决的决策问题。这里的“多项式时间”意味着解决问题所需的时间与输入规模n的关系是n的某个多项式的函数

- NP类问题是指那些在多项式时间内可以验证其解正确与否的问题。换句话说，如果有人给出了一个问题的解，我们可以在多项式时间内检查这个解是否正确。这并不意味着找到解本身必须在多项式时间内完成，**而只是说验证解的过程需要多项式时间**。

- **NP完全问题**  其余NP都可以规约到此





# 启发式算法







##                  







##                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            



# 数学

## 鸽巢原理

- bellman-fold





## 局部性原理

- 时间局部性
- 空间局部性



# 指针和引用

- 引用必须绑定到一个有效的对象，不能为 `nullptr`。







# 题目

### dfs的理解

- dfs是一个接收-输出过程   可以看作一个接收一个需要dfs的入口

- 对树分支进行dfs    

```
   void traverseDFS(TreeNode* leftChild, TreeNode* rightChild, int level) {
        if (leftChild == nullptr || rightChild == nullptr) {
            return;
        }
        // If the current level is odd, swap the values of the children.
        if (level % 2 == 0) {
            int temp = leftChild->val;
            leftChild->val = rightChild->val;
            rightChild->val = temp;
        }

        traverseDFS(leftChild->left, rightChild->right, level + 1);
        traverseDFS(leftChild->right, rightChild->left, level + 1);
    }

```

## 标志位
