### ArrayList 简介
+ ArrayList 继承了**AbstractList**，实现了**List**。它是一个数组队列，提供了相关的添加、删除、修改、遍历等功能。
+ ArrayList 实现了**RandomAccess 接口**， RandomAccess 是一个标志接口，表明实现这个这个接口的 List 集合是支持快速随机访问的。在 ArrayList 中，我们即可以通过元素的序号快速获取元素对象，这就是快速随机访问。
+ ArrayList 实现了**Cloneable 接口**，即覆盖了函数 clone()，能被克隆。
+ ArrayList 实现**java.io.Serializable 接口**，这意味着ArrayList支持序列化，能通过序列化去传输。
+ 和 Vector 不同，ArrayList 中的操作不是线程安全的！所以，建议在单线程中才使用 ArrayList，而在多线程中可以选择 Vector 或者 **CopyOnWriteArrayList**。

### 内部类

```java
     private class Itr implements Iterator<E>  
     private class ListItr extends Itr implements ListIterator<E>  
     private class SubList extends AbstractList<E> implements RandomAccess  
     static final class ArrayListSpliterator<E> implements Spliterator<E>  
```
+ **Itr** 是实现了 **Iterator接口**，同时重写了里面的**hasNext()**，**next()**，**remove()** 等方法；
+ **ListItr** 继承 **Itr**，实现了**ListIterator接口**，同时重写了**hasPrevious()**，**nextIndex()**，**previousIndex()**，**previous()**，**set(E e)**，**add(E e)** 等方法，所以这也可以看出了 **Iterator** 和 **ListIterator**的区别:**ListIterator**在**Iterator**的基础上增加了添加对象，修改对象，逆向遍历等方法，这些是Iterator不能实现的。

### System.arraycopy()和Arrays.copyOf()方法
```java
  /** 
   * 复制数组内容，如果超出部分，则用null填充 
   *
   * @param: 原始数组
   * @param: 复制数组的长度
   * @param: 返回的类型
   */
  public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
      @SuppressWarnings("unchecked")
      T[] copy = ((Object)newType == (Object)Object[].class)
          ? (T[]) new Object[newLength]
          : (T[]) Array.newInstance(newType.getComponentType(), newLength);
      System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
      return copy;
  }
```

`Arrays.copy`底层调用的还是`System.arraycopy`方法

 
```java
   /**
    *
    * @param      src      源数组
    * @param      srcPos   原数组的其实位置
    * @param      dest     目的数组
    * @param      destPos  目标数组的起始位置
    * @param      length   需要拷贝的长度
    */
   public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);
```
