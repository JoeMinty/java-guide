**继承关系**
```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable {
}
    
public interface NavigableMap<K,V> extends SortedMap<K,V> {
  /** 导航方法 */
  Map.Entry<K,V> lowerEntry(K key);

  K lowerKey(K key);

  K higherKey(K key);
  
  /** 排序方法*/
  /**
   * 返回包含键值在 [minKey, toKey) 范围内的 Map
   */
  SortedMap<K,V> headMap(K toKey);();

  /**
   * 返回包含键值在 [fromKey, toKey) 范围内的 Map
   */
  SortedMap<K,V> subMap(K fromKey, K toKey);

}
```

**构造函数**
```java

```

**查找**
```java
    /** 对外暴露的接口 */
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }
    
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }
```

**遍历重要方法**

```
TreeMap 可以保证键的有序性，默认是正序。所以在遍历过程中， TreeMap 会从小到大输出键的值。那么，接下来就来分析一下keySet方法，以及在遍历 keySet 方法产生的集合时，TreeMap 是如何保证键的有序性的
```

```java
    /** 具体操作 */
    
     /**
     * Base class for TreeMap Iterators
     */
    abstract class PrivateEntryIterator<T> implements Iterator<T> {
        Entry<K,V> next;
        Entry<K,V> lastReturned;
        int expectedModCount;

        PrivateEntryIterator(Entry<K,V> first) {
            expectedModCount = modCount;
            lastReturned = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K,V> nextEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            // 寻找节点e的后继节点
            next = successor(e);
            lastReturned = e;
            return e;
        }

        final Entry<K,V> prevEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            next = predecessor(e);
            lastReturned = e;
            return e;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            // deleted entries are replaced by their successors
            if (lastReturned.left != null && lastReturned.right != null)
                next = lastReturned;
            deleteEntry(lastReturned);
            expectedModCount = modCount;
            lastReturned = null;
        }
    }
    
    final class KeyIterator extends PrivateEntryIterator<K> {
        KeyIterator(Entry<K,V> first) {
            super(first);
        }
        public K next() {
            return nextEntry().key;
        }
    }
    
    Iterator<K> keyIterator() {
        return new KeyIterator(getFirstEntry());
    }
    
    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> m;
        KeySet(NavigableMap<E,?> map) { m = map; }

        public Iterator<E> iterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).keyIterator();
            else
                return ((TreeMap.NavigableSubMap<E,?>)m).keyIterator();
        }
        
        // ... 省略
    }
    
    /** 按升序返回key set */
    public Set<K> keySet() {
        return navigableKeySet();
    }
    
    public NavigableSet<K> navigableKeySet() {
        KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
    }
```

**successor方法**
```java
    /** 此方法在进行循环遍历的时候会触发，红黑树是一个中序遍历的输出方式 */
    static <K,V> TreeMap.Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            // 有右子树的节点，后继节点就是右子树的“最左节点”
            // 因为“最左子树”是右子树的最小节点
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            // 如果右子树为空，则寻找当前节点所在左子树的第一个祖先节点
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }
```

**predecessor方法**
```java
    /** 获取前一个节点 */
    static <K,V> Entry<K,V> predecessor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Entry<K,V> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

```


**插入**
```java
    public V put(K key, V value) {
        Entry<K,V> t = root;
        // 1.如果根节点为 null，将新节点设为根节点
        if (t == null) {
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        
        // split comparator and comparable paths
        // 2.为 key 在红黑树找到合适的位置
        // 如果cpr不为null，采用定制排序
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            // 如果是自定义的比较器
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            if (key == null)
                throw new NullPointerException();
            // 默认比较器
            @SuppressWarnings("unchecked")
                Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        
        // 3.将新节点链入红黑树中
        Entry<K,V> e = new Entry<>(key, value, parent);
        // 如果新插入 key 小于 parent 的 key，则 e 作为 parent 的左子节点
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
            
        // 4.回调函数，插入新节点可能会破坏红黑树性质，修复红黑树
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }
```
##### 插入会碰到五种情况
- x 是根节点
- x 的父节点是黑色
- x 的父节点是红色，叔叔节点也是红色
- x 的父节点是红色，叔叔节点是黑色，且 N 是 P 的右孩子
- x 的父节点是红色，叔叔节点是黑色，且 N 是 P 的左孩子

```java
  private void fixAfterInsertion(Entry<K,V> x) {
        x.color = RED;

        // x节点的父节点不是根，且x的父节点不是红色
        while (x != null && x != root && x.parent.color == RED) {
        
            // 如果x的父节点是其父节点的左子节点
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                // 获取叔叔节点
                Entry<K,V> y = rightOf(parentOf(parentOf(x)));
                
                // 叔叔节点是红色，情况三
                if (colorOf(y) == RED) {
                    // 将x的父节点设置为黑色
                    setColor(parentOf(x), BLACK);
                    // 将叔叔节点设置为黑色
                    setColor(y, BLACK);
                    // 将祖父节点设置为红色
                    setColor(parentOf(parentOf(x)), RED);
                    // 向上调整祖父节点
                    x = parentOf(parentOf(x));
                } else { // 叔叔节点是黑色
                
                    // x是其父节点的右节点
                    if (x == rightOf(parentOf(x))) {
                        // 将x的父节点设置为x
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));
                }
            } else {// 如果x的父节点是其父节点的右子节点
            
                // 获取叔叔节点
                Entry<K,V> y = leftOf(parentOf(parentOf(x)));
                // 如果叔叔节点是红色
                if (colorOf(y) == RED) {
                    // 将x的父节点设置为黑色
                    setColor(parentOf(x), BLACK);
                    // 将x的叔叔节点设置为黑色
                    setColor(y, BLACK);
                    // 将x的祖父节点设置为红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else { // 叔叔节点是黑色
                
                    // x是其父节点的左子节点
                    if (x == leftOf(parentOf(x))) {
                        // 将x的父节点设置为x
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        // 将根节点设为黑色
        root.color = BLACK;
    }
```

**左旋**
```java
/*
 * 对红黑树的节点(p)进行左旋转
 *
 * 左旋示意图(对节点p进行左旋)：
 *      pp                              pp
 *     /                               /
 *    p                               r                
 *   /  \        --(左旋)-->         / \                
 *  lp   r                          p  rr     
 *     /   \                       /  \
 *    lr   rr                     lp  lr  
 *
 */
 
    private void rotateLeft(Entry<K,V> p) {
        if (p != null) {
            // 设置p的右孩子为r
            Entry<K,V> r = p.right;
            
            // 将r的左孩子设为p的右孩子
            p.right = r.left;
            
            // 如果r的左孩子非空，将p设为r的左孩子的父亲
            if (r.left != null)
                r.left.parent = p;
                
            // 将p的父亲设为r的父亲    
            r.parent = p.parent;
            
            // 如果p的父亲是空节点，则将r设为根节点
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;  // 如果p是它父节点的左孩子，则将r设为p的父节点的左孩子
            else
                p.parent.right = r; // 如果p是它父节点的右孩子，则将r设为p的父节点的右孩子
                
            // 将p设为r的左孩子
            r.left = p;
            // 将r设为p的父节点
            p.parent = r;
        }
    }
```

**右旋**
```java
/* 
 * 对红黑树的节点(p)进行右旋转
 *
 * 右旋示意图(对节点p进行右旋)：
 *            pp                               pp
 *           /                                /
 *          p                                l                  
 *         /  \       --(右旋)-->           /  \                     
 *        l   rp                           ll   p  
 *       / \                                   / \                   
 *      ll  rl                                rl  rp
 * 
 */
    private void rotateRight(Entry<K, V> p) {
        // 设置l是当前节点p的左孩子
        Entry<K, V> l = p.left;
        
        // 将l的右孩子设为p的左孩子
        p.left = l.right;
        if (l.right != null)
            l.right.parent = p;  // 如果l的右孩子不为空的话，将p设为l的右孩子的父亲
            
        // 将p的父亲设为l的父亲
        l.parent = p.parent;
        if (p.parent == null)  // 如果p的父亲是空节点，则将l设为根节点
            root = l;
        else if (p.parent.left = p)  // 如果p是它父节点的左孩子，则将l设为p的父节点的左孩子
            p.parent.left = l;
        else
            p.parent.right = r;  // 如果p是它父节点的右孩子，则将l设为p的父节点的右孩子
        
        // 将p设为l的右孩子
        l.right = p;
        // 将p的父节点设为l
        p.parent = l;
    } 
    
```

**删除**
