**继承关系**
```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
    
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
            
            // 将 “r的左孩子” 设为 “p的右孩子”；
            p.right = r.left;
            
            // 如果r的左孩子非空，将 “p” 设为 “r的左孩子的父亲”
            if (r.left != null)
                r.left.parent = p;
                
            // 将 “p的父亲” 设为 “r的父亲”    
            r.parent = p.parent;
            
            // 如果 “p的父亲” 是空节点，则将r设为根节点
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;  // 如果 p是它父节点的左孩子，则将r设为“p的父节点的左孩子”
            else
                p.parent.right = r; // 如果 p是它父节点的右孩子，则将r设为“p的父节点的右孩子”
                
            // 将 “p” 设为 “r的左孩子”
            r.left = p;
            // 将 “r” 设为 “p的父节点”
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
 
    
    }
```
