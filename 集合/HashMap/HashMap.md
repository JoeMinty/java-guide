- **capacity容量**

```java
  DEFAULT_INITIAL_CAPACITY = 1 << 4 // 默认16
  MAXIMUM_CAPACITY = 1 << 30; // 最大容量 =  2的30次方（若传入的容量过大，将被最大值替换）
```

- **loadFactor加载因子**

```java
  DEFAULT_LOAD_FACTOR = 0.75f
```
  loadFactor加载因子是控制数组存放数据的疏密程度

- **threshold扩容阈值**

  计算公式： **threshold = capacity \* loadFactor** ，**衡量数组是否需要扩增的标准**
  
- **桶的树化阈值**

  TREEIFY_THRESHOLD = 8
  
- **桶的链表还原阈值**

  UNTREEIFY_THRESHOLD = 6

- **最小树形化容量阈值**

  MIN_TREEIFY_CAPACITY = 64
  
  当哈希表中的容量 > 该值时，才允许树形化链表

- **链表节点**
```java
  static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;  // hash值，就是落于桶的位置
        final K key;     // 健
        V value;         // 值
        Node<K,V> next;  // 指向下一个节点的指针

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        ...
   }
```

- **TreeNode**
```java
  static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
          TreeNode<K,V> parent;  // red-black tree links
          TreeNode<K,V> left;
          TreeNode<K,V> right;
          TreeNode<K,V> prev;    // needed to unlink next upon deletion
          boolean red;
          ...
  }
```
- **构造函数**
```java
  /** 默认构造函数 */
  public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
  }
  
  /** 指定容量大小的构造函数 */
  public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }
  
  /** 指定容量大小和加载因子的构造函数 */
  public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        // 重新计算，设置扩容阈值
        this.threshold = tableSizeFor(initialCapacity);
    }
    
    /** 按传入的Map子类构造新的HashMap */
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
```

- **putMapEntries**方法

- **put**方法

```java
    public V put(K key, V value) {
        // 1.对key进行扰动函数计算hash值
        // 2.调用私有的putVal()添加数据
        return putVal(hash(key), key, value, false, true);
    }
    
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                       boolean evict) {
            Node<K,V>[] tab; Node<K,V> p; int n, i;
            
            // 若哈希表的数组tab为空，则通过resize()创建
            if ((tab = table) == null || (n = tab.length) == 0)
                n = (tab = resize()).length; 
            // 计算插入存储的数组索引i，插入时，需判断是否存在Hash冲突（是否为null）
            if ((p = tab[i = (n - 1) & hash]) == null)
                tab[i] = newNode(hash, key, value, null);
            else {
                Node<K,V> e; K k;
                // 判断table[i]的元素的key是否与插入的key一样，若相同则直接用新value覆盖旧value
                if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                    e = p;
                // 判断需插入的数据结构是红黑树 or 链表
                else if (p instanceof TreeNode)
                    e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
                else {
                    for (int binCount = 0; ; ++binCount) {
                        if ((e = p.next) == null) {
                            p.next = newNode(hash, key, value, null);
                            if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                                treeifyBin(tab, hash);
                            break;
                        }
                        if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                            break;
                        p = e;
                    }
                }
                if (e != null) { // existing mapping for key
                    V oldValue = e.value;
                    if (!onlyIfAbsent || oldValue == null)
                        e.value = value;
                    afterNodeAccess(e);
                    return oldValue;
                }
            }
            ++modCount;
            if (++size > threshold)
                resize();
            afterNodeInsertion(evict);
            return null;
        }
```
 
- **hash**
```java
  /** 根据key生成hash值 */
  static final int hash(Object key) {
    int h;
    // 1.取hashCode值： h = key.hashCode() 
    // 2.高位参与低位的运算：h ^ (h >>> 16)  
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
  }
```
  **为了提高存储key-value的数组下标位置的随机性 & 分布均匀性，应该尽量避免出现hash值冲突。即：对于不同key，存储的数组下标位置要尽可能不一样**
 
- **resize**方法

  扩容比较耗时，因为在此过程中，会伴随着一次hash分配，并且会遍历hash表中的所有元素。

```java
  /**
   * 1.初始化哈希表
   * 2.当前数组容量过小，扩容
   */
  final Node<K,V>[] resize() {
      // 扩容前的数组
      Node<K,V>[] oldTab = table;
      
      // 扩容前的数组容量
      int oldCap = (oldTab == null) ? 0 : oldTab.length;
      // 扩容前数组的阈值
      int oldThr = threshold;
      int newCap, newThr = 0;
      if (oldCap > 0) {
          // 超过最大值就不会扩充
          if (oldCap >= MAXIMUM_CAPACITY) {
              threshold = Integer.MAX_VALUE;
              return oldTab;
          }
          // 没超过最大值，就扩充为原来的2倍
          else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY)
              newThr = oldThr << 1; // double threshold
      } // 下面两种判断都是在初始化哈希表的时候
      else if (oldThr > 0) // initial capacity was placed in threshold
          newCap = oldThr;
      else { 				// zero initial threshold signifies using defaults
          newCap = DEFAULT_INITIAL_CAPACITY;
          newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
      }
      // 计算新的resize上限
      if (newThr == 0) {
          float ft = (float)newCap * loadFactor;
          newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ? (int)ft : Integer.MAX_VALUE);
      }
      threshold = newThr;
      @SuppressWarnings({"rawtypes","unchecked"})
          Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
      table = newTab;
      if (oldTab != null) {
          // 把每个bucket都移动到新的buckets中
          for (int j = 0; j < oldCap; ++j) {
              Node<K,V> e;
              // 如果旧的bucket不为null，则进行移动操作
              if ((e = oldTab[j]) != null) {
                  oldTab[j] = null; // 将旧的数组对应bucket位置置为null
                  if (e.next == null)  // 如果对应bucket只有一个对象，则计算hash，并移动到新bucket中
                      newTab[e.hash & (newCap - 1)] = e;
                  else if (e instanceof TreeNode) // 如果是红黑树（即超过8个Node对象）
                      ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                  else { // 如果是链表形式
                      Node<K,V> loHead = null, loTail = null;
                      Node<K,V> hiHead = null, hiTail = null;
                      Node<K,V> next;
                      do {
                          next = e.next;
                          // 原索引
                          if ((e.hash & oldCap) == 0) {
                              if (loTail == null)
                                  loHead = e;
                              else
                                  loTail.next = e;
                              loTail = e;
                          }
                          // 原索引+oldCap
                          else {
                              if (hiTail == null)
                                  hiHead = e;
                              else
                                  hiTail.next = e;
                              hiTail = e;
                          }
                      } while ((e = next) != null);
                      // 原索引放到bucket里
                      if (loTail != null) {
                          loTail.next = null;
                          newTab[j] = loHead;
                      }
                      // 原索引+oldCap放到bucket里
                      if (hiTail != null) {
                          hiTail.next = null;
                          newTab[j + oldCap] = hiHead;
                      }
                  }
              }
          }
      }
      return newTab;
  }
  ```

- **treeifyBin方法**
```java
  /** 将桶内所有的 链表节点 替换成 红黑树节点 */
  final void treeifyBin(Node<K,V>[] tab, int hash) {
      int n, index; Node<K,V> e;
      // 如果当前哈希表为空，或者哈希表中数组的长度小于进行树形化的阈值(默认为 64)，执行新建或者扩容，没必要转换数据结构
      if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
          resize();
      else if ((e = tab[index = (n - 1) & hash]) != null) {
          // 如果哈希表中的数组的长度超过了树形化阈值，进行树形化
          // e 是哈希表中指定位置桶里的链表节点，从第一个开始
          TreeNode<K,V> hd = null, tl = null; // 红黑树的头、尾节点
          do {
              // 新建一个树形节点，内容和当前链表节点 e 一致
              TreeNode<K,V> p = replacementTreeNode(e, null);
              if (tl == null) // 确定树头节点
                  hd = p;
              else {
                  p.prev = tl;
                  tl.next = p;
              }
              tl = p;
          } while ((e = e.next) != null);  
          // 让桶的第一个元素指向新建的红黑树头结点，以后这个桶里的元素就是红黑树而不是链表了
          if ((tab[index] = hd) != null)
              hd.treeify(tab); // 最终构造红黑树
      }
  }
```

#### 参考

https://blog.csdn.net/wushiwude/article/details/75331926

https://blog.csdn.net/carson_ho/article/details/79373134

https://blog.csdn.net/weixin_42340670/article/details/80503863
