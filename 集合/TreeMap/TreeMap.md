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
