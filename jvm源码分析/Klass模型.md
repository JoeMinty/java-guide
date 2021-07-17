```
MetaspaceObj
  |
  |———— Metadata
            |
            |———— Klass
                    |
                    |———— InstanceKlass
                    |            |
                    |            |———— InstanceMirrorKlass（描述java.lang.Class的实例）
                    |            |
                    |            |———— InstanceRefKlass （描述java.lang.ref.Refenece的子类）
                    |            |
                    |            |———— InstanceClassLoaderKlass
                    |            
                    |———— ArrayKlass 
                                 |
                                 |———— TypeArrayKlass（描述java中基本类型数组的数据结构）
                                 |
                                 |———— ObjArrayKlass （描述java中引用类型数组的数据结构）
```

```
oopDesc
   |———— MarkOopDesc（存放锁信息，分代年龄...）
   |
   |———— InstanceOopDesc（非数组对象）
   |
   |———— ArrayOopDesc（数组对象）
              |
              |———— TypeArrayOopDesc（描述java中基本数据类型数组）
              |
              |———— ObjArrayOopDesc（描述java中引用类型数组）
```
