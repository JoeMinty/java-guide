## 跟踪调试参数
### 跟踪垃圾回收
**-XX:+PrintGC**：gc就会打印日志

**-XX:+PrintGCDetails**：更详细的信息

**-XX:+PrintHeapAtGc**：更全面的堆信息

**-XX:PrintGCApplicationConcurrentTime**：打印应用程序的执行时间

**-XX:PrintGCApplicationConcurrentTime**：打印应用程序由于GC产生的停顿时间

**-XX:PrintReferenceGC**：跟踪系统内的软引用，弱引用，虚引用和Finallize队列

**-Xloggc:log/gc.log**：将GC日志以文件的形式输出
