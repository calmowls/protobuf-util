# protobuf-util
Protobuf converters.
## Usage

Convert protobuf to plain object.
```java
MyObj.Builder builder = MyObj.newBuilder();
//Set any values in protobuf object
MyObjModel model =  ProtobufConverter.toPlainObject(builder.build(), MyObjModel.class);
```

Protobuf to POJO.
```java
ProtobufConverter.toPlainObject(T proto, Class<E> pojo)
```
POJO to Protobuf.
```java
PlainObjectConverter.toProtobuf(E pojo, Class<T> proto)
```
Protobuf to Map.
```java
ProtobufConverter.toMap(T proto, Class<E> pojo)
```
Map to Protobuf
```java
MapConverter.toProtobuf(E map, Class<T> proto)
```
