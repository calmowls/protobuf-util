# protobuf-util
Protobuf converters. Supporting recursive message convertion, repeated message and pre-defined google message.
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
Recursive object support
```protobuf
message Abc {
 	string user_id = 1;
	bool enabled = 2;
}
message MyObj {
  Abc my_user = 1;
  repeated Abc users_status = 2;
}
```
```java
public class AbcModel {
  private String userId;
  private boolean enabled;
  //getters and setters
}

public class MyObjModel {
  private AbcModel myUser;
  private List<AbcModel> usersStatus;
  //getters and setters
}

MyObj.Builder builder = MyObj.newBuilder();
Abc.Builder abcBuilder = Abc.newBuilder();
abcBuilder.setUserId("jack");
abcBuilder.setEnabled(true);
builder.setMyUser(abcBuilder.build());
List<Abc> users = new ArrayList<>();
abcBuilder = Abc.newBuilder();
abcBuilder.setUserId("e1");
abcBuilder.setEnabled(false);
users.add(abcBuilder.build());
MyObjModel model =  ProtobufConverter.toPlainObject(builder.build(), MyObjModel.class);
model.getMyUser().getUserId();//"jack"
model.getMyUser().isEnabled();//true
model.getUsersStatus().get(0).getUserId();//e1
model.getUsersStatus().get(0).isEnabled();//false
```
