### 概述
线程安全的`ArrayList`，是一个可变的数组，与`ReentrantReadWriteLock`读写锁思想类似。

**`CopyOnWriteArrayList`动态数组机制**

**`CopyOnWriteArrayList`线程安全机制**

### 应用场景
读操作远远大于写操作

### 基本属性
```java
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    /** 用锁来保护异常突变，保证线程安全 */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray. */
    private transient volatile Object[] array;
    
    /** 获取列表 */
    final Object[] getArray() {
        return array;
    }

    /** 设置列表 */
    final void setArray(Object[] a) {
        array = a;
    }
}
```

### 构造函数
```java
    /**
     * 创建一个空的列表
     */
    public CopyOnWriteArrayList() {
        setArray(new Object[0]);
    }

    public CopyOnWriteArrayList(Collection<? extends E> c) {
        Object[] elements;
        if (c.getClass() == CopyOnWriteArrayList.class)
            elements = ((CopyOnWriteArrayList<?>)c).getArray();
        else {
            elements = c.toArray();
            if (elements.getClass() != Object[].class)
                elements = Arrays.copyOf(elements, elements.length, Object[].class);
        }
        setArray(elements);
    }

    public CopyOnWriteArrayList(E[] toCopyIn) {
        setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
    }
```
### 获取元素
读取操作不加同步和锁操作，因为内部的array数组可以保证数据安全，其他同步操作只会复制array操作，然后替换
```java
    @SuppressWarnings("unchecked")
    private E get(Object[] a, int index) {
        return (E) a[index];
    }

    public E get(int index) {
        return get(getArray(), index);
    }
```

### 添加元素
`add()`方法加锁保证了同步，避免多线程写的时候会copy出多份副本
```java
    /** 在数组末尾添加元素 */
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        // 获取锁
        lock.lock();
        try {
            // 获取原始volatile数组
            Object[] elements = getArray();
            int len = elements.length;
            // 新建数组，将原始数据拷贝其中
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            // 将新增元素保存在新建数组末尾
            newElements[len] = e;
            // 用新建数组替换原始数据
            setArray(newElements);
            return true;
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    /** 在数组指定位置添加元素 */
    public void add(int index, E element) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (index > len || index < 0)
                throw new IndexOutOfBoundsException("Index: "+index+", Size: "+len);
            Object[] newElements;
            // 计算需要从数组的哪个位置开始移动
            int numMoved = len - index;
            if (numMoved == 0) // 如果在数组末尾，逻辑和向末尾添加元素相同
                newElements = Arrays.copyOf(elements, len + 1);
            else {
                // 否则，以该下标为基点，将原始数组左右两侧的数据复制到新数组指定位置
                newElements = new Object[len + 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index, newElements, index + 1, numMoved);
            }
            // 在指定位置添加需要添加的元素
            newElements[index] = element;
            setArray(newElements);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }
```

### 修改元素
```java
    public E set(int index, E element) {
        final ReentrantLock lock = this.lock;
        // 加锁
        lock.lock();
        try {
            Object[] elements = getArray();
            // 获取指定位置的元素
            E oldValue = get(elements, index);
            // 如果原数据和修改的元素不同
            if (oldValue != element) {
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len);
                // 将新元素替换旧元素
                newElements[index] = element;
                setArray(newElements);
            } else {
                // Not quite a no-op; ensures volatile write semantics
                setArray(elements);
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }
```

### 删除元素
```java
    public E remove(int index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            E oldValue = get(elements, index);
            // 计算需要移动的元素的起始位置
            int numMoved = len - index - 1;
            if (numMoved == 0) // 如果被删除的是最后一个元素，那么不需要新建数组
                setArray(Arrays.copyOf(elements, len - 1));
            else {
                // 新建数组，进行相应拷贝操作
                Object[] newElements = new Object[len - 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index + 1, newElements, index,
                                 numMoved);
                setArray(newElements);
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }
```

### 遍历
