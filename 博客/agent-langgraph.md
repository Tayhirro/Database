stategraph构建
- 传入state定义



节点可以取到的数据
- 传入
- state获取


workflow 
- add_node
- add_edge
	- add_conditional_edges
		- 定义router函数
	- 节点内部定义command
	- 节点内部定义send 广播
	- interrupt
		- 图级别配置：graph.compile 
			- interrupt_before=["execute_tools"], # 执行工具前暂停，人来确认
			- interrupt_after=["agent_think"], # LLM 思考后暂停，人来看决策
		- 节点内部：
			- if risky_operation:


Runnable
invoke：运行它 能连续传


