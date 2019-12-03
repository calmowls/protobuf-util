# protobuf-util
Protobuf converters.
## How to use
Convert protobuf to plain object.
MyObj.Builder builder = MyObj.newBuilder();
//Set any values in protobuf object
MyObjModel model =  ProtobufConverter.toPlainObject(builder.build(), MyObjModel.class);

It's supporting:
Protobuf to POJO
ProtobufConverter.toPlainObject(T proto, Class<E> pojo)
POJO to Protobuf
PlainObjectConverter.toProtobuf(E pojo, Class<T> proto)
Protobuf to Map
ProtobufConverter.toMap(T proto, Class<E> pojo)
Map to Protobuf
MapConverter.toProtobuf(E map, Class<T> proto)
