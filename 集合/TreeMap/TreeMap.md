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
**查找**
```java
    /** 对外暴露的接口 */
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
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
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
            
        // 4.回调函数，插入新节点可能会破坏红黑树性质
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }
```


**遍历重要方法**

**successor方法**
    此方法在进行循环遍历的时候会触发，红黑树是一个中序遍历的输出方式
```java
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
