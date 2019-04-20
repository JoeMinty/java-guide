```java
/** put的时候在putVal()中调用，重写了newNode() */
Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
  LinkedHashMap.Entry<K,V> p = new LinkedHashMap.Entry<K,V>(hash, key, value, e);
  linkNodeLast(p);
  return p;
}
    
// 将新增的节点，连接在链表的尾部
private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
  LinkedHashMap.Entry<K,V> last = tail;
  tail = p;
  if (last == null)
    head = p;
  else {
    p.before = last;
    last.after = p;
  }
}
```


回调函数*afterNodeAccess*
```java
/** 将当前被访问到的节点e，移动至内部的双向链表的尾部 */
void afterNodeAccess(Node<K,V> e) { // move node to last
        // 原尾节点
        LinkedHashMap.Entry<K,V> last;
        // 如果accessOrder为true ，且原尾节点不等于e
        if (accessOrder && (last = tail) != e) {
            // 节点e强转成双向链表节点p
            LinkedHashMap.Entry<K,V> p = (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            
            // p是尾节点，后置节点一定是null
            p.after = null;
            
            // 如果p的前置节点是null，则p以前是头结点，所以现在的头结点是p的后置节点a
            if (b == null)
                head = a;
            else
                b.after = a; // 否则更新p的前直接点b的后置节点为a
            
            // 如果p的后置节点不是null，则更新后置节点a的前置节点为b
            if (a != null)
                a.before = b;
            else
                last = b;  // 如果原本p的后置节点是null，则p就是尾节点。 此时更新last的引用为p的前置节点b
               
            // 原本尾节点是null，则链表中就一个节点   
            if (last == null)
                head = p;
            else {
                // 否则更新当前节点p的前置节点为原尾节点last，last的后置节点是p
                p.before = last;
                last.after = p;
            }
            // 尾节点的引用赋值成p
            tail = p;
            ++modCount;
        }
    }
```
ps:afterNodeAccess()方法会修改modCount，处于accessOrder=true的时候迭代LinkedHashMap，如果同时查询访问数据，会导致fail-fast，因为迭代的顺序已经改变。
