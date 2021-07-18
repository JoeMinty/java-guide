## 为什么要调优？
- 防止出现OOM
- 解决OOM
- 减少GC，尤其是full gc出现的频率


## JVM不同类型调优参数写法
- Key-Value型（-XX:Key = Value）
- Bool型 （-XX: +/-UseXX）
- 简写 （-Xms 100m  -Xmx 100m -Xss）


## 调优的三个阶段
- 在项目部署上线之前，基于可能的并发量进行预估调优
- 在项目运行过程中，部署监控收集性能数据，分析日志进行调优
- 线上出现OOM，进行问题排查与调优
（防止内存在运行时抖动）


## 如何调优
`java -XX:+PrintFlagsFinal -version | grep xxx`

### 堆区调优
`-Xms 100m -Xmx 100m`

### 元空间
使用cglib模拟向元空间写入数据

### GC

### 直接内存（堆外内存） 
`MaxDirectMemorySize`默认（0）和堆(-Xmx)设置的大小一致
