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
