## 跟踪调试参数
### 跟踪垃圾回收
**-XX:+PrintGC**：gc就会打印日志

**-XX:+PrintGCDetails**：更详细的信息

**-XX:+PrintHeapAtGc**：更全面的堆信息

**-XX:+PrintGCApplicationConcurrentTime**：打印应用程序的执行时间

**-XX:+PrintGCApplicationConcurrentTime**：打印应用程序由于GC产生的停顿时间

**-XX:+PrintReferenceGC**：跟踪系统内的软引用，弱引用，虚引用和Finallize队列

**-Xloggc:log/gc.log**：将GC日志以文件的形式输出

### 类加载／卸载的跟踪
**-verbose:calss**：跟踪类的加载和卸载

**-XX:+TraceClassLoading**：跟踪类的加载

**-XX:+TraceClassUnloading**：跟踪类的卸载

### 系统参数查看
**-XX:+PrintVMOptions**：可以在程序运行时，打印虚拟机接受到的命令行显示参数

**-XX:+PrintCommandLineFlags**：可以打印传递给虚拟机的显示和隐式参数

**-XX:+PrintFlagsFinal**：打印所有的系统参数的值

## 堆的参数配置
### 最大堆和初始堆的设置
**-Xms**：设置初始堆空间

如果初始堆空间耗尽，虚拟机将会对堆空间进行扩展

**-Xmx**：设置最大堆空间

ps：在实际工作中，可以直接将初始堆-Xms与最大堆-Xmx设置相等。这样的好处就是可以减少程序运行时进行的垃圾回收次数，从而提高程序的性能

### 新生代的配置
**-Xmn**：设置新生代的大小，大小一般设置为整个堆空间的1/3，1/4左右

**-XX:SurvivorRatio**：用来设置新生代中eden空间和from／to空间的比例关系

`-XX:SurvivorRatio=eden/from=eden/to`

ps：尽可能将对象预留在新生代，减少老年代GC的次数

**-XX:NewRatio=老年代／新生代**：设置老年代和新生代的比例

### 堆溢出处理
在java程序的运行过程中，如果堆空间不足，则有可能抛出内存溢出错误（**OOM**，out of memory）

## 非堆内存堆的参数配置
### 方法区配置
JDK1.8中，永久区被彻底移除，使用类新的元数据区存放类的元数据。默认情况下，元数据区只受系统可用内存的限制

**-XX:MaxMataspaceSize**：指定永久区的最大可用值，默认情况下，

### 栈配置
**-Xss**

### 直接内存配置
**-XX:MaxDirectMemorySize**：设置最大可用直接内存，如不设置，默认值为最大堆空间，即-Xmx












