**继承关系**
```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {}
```

**重要参数**
```java
    /** 默认table表的容量大小 */
    private static final int DEFAULT_CAPACITY = 16;
    
    /** 默认加载因子 */
    private static final float LOAD_FACTOR = 0.75f;
    
    /** 初始化的表，在第一次插入的时候才真正初始化 */
    transient volatile Node<K,V>[] table;
    
    /** 用于扩容的时候，扩容完后会置为null */
    private transient volatile Node<K,V>[] nextTable;
    
    /** 默认为0，用来控制table的初始化和扩容操作*/
    private transient volatile int sizeCtl;

```


**构造函数**
```java
    public ConcurrentHashMap() {
    }

    public ConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException();
        int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ?
                   MAXIMUM_CAPACITY :
                   tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
        this.sizeCtl = cap;
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this.sizeCtl = DEFAULT_CAPACITY;
        putAll(m);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (initialCapacity < concurrencyLevel)   // Use at least as many bins
            initialCapacity = concurrencyLevel;   // as estimated threads
        long size = (long)(1.0 + (long)initialCapacity / loadFactor);
        int cap = (size >= (long)MAXIMUM_CAPACITY) ?
            MAXIMUM_CAPACITY : tableSizeFor((int)size);
        this.sizeCtl = cap;
    }
```

**初始化table**
这是一个懒加载的实现，在ConcurrentHashMap初始化的时候并不会初始化`table`变量，而是等第一次`put`的时候才会
```java
    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        // 如果表为空，才进行初始化
        while ((tab = table) == null || tab.length == 0) {
            // sizeCtl小于零，证明已经有线程正在进行初始化操作，那么当前线程应该放弃CPU的使用
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) { // 开始初始化
                try {
                    // 再次判断是否为空
                    if ((tab = table) == null || tab.length == 0) {
                        // 初始化容量
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        // 初始化table
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        // 计算阈值
                        sc = n - (n >>> 2);
                    }
                } finally {
                    // 设置阈值
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```

**计算hash值**
```java
    /** concurrenthashmap */
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }
    
    // hashmap
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

**添加**
```java

    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        // 参数校验
        if (key == null || value == null) throw new NullPointerException();
        
        // 计算hash值
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            // 如果table还没初始化，先初始化
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) { // 如果为空，那么以CAS无锁添加一个节点
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED) // 扩容
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        // 向普通的链表中添加元素
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }

```

https://www.cnblogs.com/yangming1996/p/8031199.html
