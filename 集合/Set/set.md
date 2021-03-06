# HashSet
`HashSet`是一个不允许存储相同元素的元素，底层的方法依赖`HashMap`，所有要想明白`HashSet`的工作原理，先可了解`HashMap`

### 特点
- 允许null值
- 不能重复
- 非线程安全

### 类定义
```java
public class HashSet<E> extends AbstractSet<E>
    implements Set<E>, Cloneable, java.io.Serializable {
  // ...
}
```

由此可以看出`Set`是一个接口，里面定义了集合类的所有方法

### 成员变量
```java

  /** 用hashmap来存储 */
  private transient HashMap<E,Object> map;

  /** 存入map中的value值都唯一 */
  private static final Object PRESENT = new Object();
```

### 构造函数
```java
   /** 
    * 默认的无参构造器，构造一个空的HashSet。 
    *  
    * 实际底层会初始化一个空的HashMap，并使用默认初始容量为16和加载因子0.75。 
    */  
   public HashSet() {  
      map = new HashMap<>();
   }  
 
   /** 
    * 构造一个包含指定collection中的元素的新set。 
    * 
    * 实际底层使用默认的加载因子0.75和足以包含指定 
    * collection中所有元素的初始容量来创建一个HashMap。 
    * @param c 其中的元素将存放在此set中的collection。 
    */  
   public HashSet(Collection<? extends E> c) {  
      map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));  
      addAll(c);  
   }  
 
   /** 
    * 以指定的initialCapacity和loadFactor构造一个空的HashSet。 
    * 
    * 实际底层以相应的参数构造一个空的HashMap。 
    * @param initialCapacity 初始容量。 
    * @param loadFactor 加载因子。 
    */  
   public HashSet(int initialCapacity, float loadFactor) {  
      map = new HashMap<>(initialCapacity, loadFactor);  
   }  
 
   /** 
    * 以指定的initialCapacity构造一个空的HashSet。 
    * 
    * 实际底层以相应的参数及加载因子loadFactor为0.75构造一个空的HashMap。 
    * @param initialCapacity 初始容量。 
    */  
   public HashSet(int initialCapacity) {  
      map = new HashMap<>(initialCapacity);  
   }  
 
   /** 
    * 以指定的initialCapacity和loadFactor构造一个新的空链接哈希集合。 
    * 此构造函数为包访问权限，不对外公开，实际只是是对LinkedHashSet的支持。 
    * 
    * 实际底层会以指定的参数构造一个空LinkedHashMap实例来实现。 
    * @param initialCapacity 初始容量。 
    * @param loadFactor 加载因子。 
    * @param dummy 标记。 
    */  
   HashSet(int initialCapacity, float loadFactor, boolean dummy) {  
      map = new LinkedHashMap<>(initialCapacity, loadFactor);  
   }  
 
```

### 重要方法
```java
   /** 
    * 返回对此set中元素进行迭代的迭代器。返回元素的顺序并不是特定的。 
    *  
    * 底层实际调用底层HashMap的keySet来返回所有的key。 
    * 可见HashSet中的元素，只是存放在了底层HashMap的key上， 
    * value使用一个static final的Object对象标识。 
    * @return 对此set中元素进行迭代的Iterator。 
    */  
   public Iterator<E> iterator() {  
      return map.keySet().iterator();  
   }  
 
   /** 
    * 返回此set中的元素的数量（set的容量）。 
    * 
    * 底层实际调用HashMap的size()方法返回Entry的数量，就得到该Set中元素的个数。 
    * @return 此set中的元素的数量（set的容量）。 
    */  
   public int size() {  
      return map.size();  
   }  
 
   /** 
    * 如果此set不包含任何元素，则返回true。 
    * 
    * 底层实际调用HashMap的isEmpty()判断该HashSet是否为空。 
    * @return 如果此set不包含任何元素，则返回true。 
    */  
   public boolean isEmpty() {  
   return map.isEmpty();  
   }  
 
   /** 
    * 如果此set包含指定元素，则返回true。 
    * 更确切地讲，当且仅当此set包含一个满足(o==null ? e==null : o.equals(e)) 
    * 的e元素时，返回true。 
    * 
    * 底层实际调用HashMap的containsKey判断是否包含指定key。 
    * @param o 在此set中的存在已得到测试的元素。 
    * @return 如果此set包含指定元素，则返回true。 
    */  
   public boolean contains(Object o) {  
      return map.containsKey(o);  
   }  
 
   /** 
    * 如果此set中尚未包含指定元素，则添加指定元素。 
    * 更确切地讲，如果此 set 没有包含满足(e==null ? e2==null : e.equals(e2)) 
    * 的元素e2，则向此set 添加指定的元素e。 
    * 如果此set已包含该元素，则该调用不更改set并返回false。 
    * 
    * 底层实际将将该元素作为key放入HashMap。 
    * 由于HashMap的put()方法添加key-value对时，当新放入HashMap的Entry中key 
    * 与集合中原有Entry的key相同（hashCode()返回值相等，通过equals比较也返回true）， 
    * 新添加的Entry的value会将覆盖原来Entry的value，但key不会有任何改变， 
    * 因此如果向HashSet中添加一个已经存在的元素时，新添加的集合元素将不会被放入HashMap中， 
    * 原来的元素也不会有任何改变，这也就满足了Set中元素不重复的特性。 
    * @param e 将添加到此set中的元素。 
    * @return 如果此set尚未包含指定元素，则返回true。 
    */  
   public boolean add(E e) {  
      return map.put(e, PRESENT)==null;  
   }  
 
   /** 
    * 如果指定元素存在于此set中，则将其移除。 
    * 更确切地讲，如果此set包含一个满足(o==null ? e==null : o.equals(e))的元素e， 
    * 则将其移除。如果此set已包含该元素，则返回true 
    * （或者：如果此set因调用而发生更改，则返回true）。（一旦调用返回，则此set不再包含该元素）。 
    * 
    * 底层实际调用HashMap的remove方法删除指定Entry。 
    * @param o 如果存在于此set中则需要将其移除的对象。 
    * @return 如果set包含指定元素，则返回true。 
    */  
   public boolean remove(Object o) {  
      return map.remove(o)==PRESENT;  
   }  
 
   /** 
    * 从此set中移除所有元素。此调用返回后，该set将为空。 
    * 
    * 底层实际调用HashMap的clear方法清空Entry中所有元素。 
    */  
   public void clear() {  
      map.clear();  
   }  
 
   /** 
    * 返回此HashSet实例的浅表副本：并没有复制这些元素本身。 
    * 
    * 底层实际调用HashMap的clone()方法，获取HashMap的浅表副本，并设置到HashSet中。 
    */  
   public Object clone() {  
       try {  
           HashSet<E> newSet = (HashSet<E>) super.clone();  
           newSet.map = (HashMap<E, Object>) map.clone();  
           return newSet;  
       } catch (CloneNotSupportedException e) {  
           throw new InternalError();  
       }  
   }  
```

# CopyOnWriteArraySet

### 特点
线程安全的无序集合，但和`HashSet`不同的是，`CopyOnWriteArraySet`内部是通过`CopyOnWriteArrayList`实现的，并不是`HashMap`，和`HashSet`一样，只有增删，没有改查

### 适用场景
- 大量读，遍历读
- 线程安全

### 类定义
```java
    public class CopyOnWriteArraySet<E> extends AbstractSet<E>
        implements java.io.Serializable {
        // ...    
    }
```

### 重要参数
```java
    // 通过CopyOnWriteArrayList来控制线程安全
    private final CopyOnWriteArrayList<E> al;
```

### 构造函数
```java
    /** 创建一个空的CopyOnWriteArrayList */
    public CopyOnWriteArraySet() {
        al = new CopyOnWriteArrayList<E>();
    }

    /**
     * Creates a set containing all of the elements of the specified
     * collection.
     *
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if the specified collection is null
     */
    public CopyOnWriteArraySet(Collection<? extends E> c) {
        if (c.getClass() == CopyOnWriteArraySet.class) {
            @SuppressWarnings("unchecked") CopyOnWriteArraySet<E> cc =
                (CopyOnWriteArraySet<E>)c;
            al = new CopyOnWriteArrayList<E>(cc.al);
        }
        else {
            al = new CopyOnWriteArrayList<E>();
            al.addAllAbsent(c);
        }
    }
```

### 重要方法
```java
    /** 删除 */
    public boolean remove(Object o) {
        return al.remove(o);
    }
    
    /** 新增 */
    public boolean add(E e) {
        return al.addIfAbsent(e);
    }
    
    /** Iterator遍历 */
    public Iterator<E> iterator() {
        return al.iterator();
    }
    
    /** forEach遍历 */
    public void forEach(Consumer<? super E> action) {
        al.forEach(action);
    }
```
