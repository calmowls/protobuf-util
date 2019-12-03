package com.calmowls.protobuf.test;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.calmowls.protobuf.Test.Abc;
import com.calmowls.protobuf.Test.MyObj;
import com.calmowls.typeconverter.map.MapConverter;
import com.calmowls.typeconverter.plainobject.PlainObjectConverter;
import com.calmowls.typeconverter.protobuf.ProtobufConverter;
import com.google.protobuf.DoubleValue;

public class ProtoTest {

  @Test
  public void getPojo() {
    MyObj.Builder builder = MyObj.newBuilder();
    // MyObj.getDescriptor()
    builder.setStr("test");
    builder.setMyNum(100);
    builder.setMyLong(10L);
    builder.setMyDouble(10.1);
    Abc.Builder abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("jack");
    abcBuilder.setEnabled(true);
    builder.setMyUser(abcBuilder.build());
    abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("abc");
    abcBuilder.setEnabled(false);
    java.util.Map<java.lang.String, Abc> values = new HashMap<>();
    values.put("first", abcBuilder.build());
    builder.putAllMyMap(values);
    List<Abc> users = new ArrayList<>();
    abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("e1");
    abcBuilder.setEnabled(false);
    users.add(abcBuilder.build());
    abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("e2");
    abcBuilder.setEnabled(true);
    users.add(abcBuilder.build());
    builder.addAllUsersStatus(users);
    List<String> strArray = new ArrayList<>();
    strArray.add("s1");
    strArray.add("s2");
    builder.addAllStrArray(strArray);
    List<Integer> intArray = new ArrayList<>();
    intArray.add(100);
    intArray.add(200);
    builder.addAllIntArray(intArray);
    builder.setWrapDouble(DoubleValue.newBuilder().setValue(1.2).build());
    builder.setNonExist("nonExist");
    builder.build().hasWrapDouble();

    MyObjModel model =  ProtobufConverter.toPlainObject(builder.build(), MyObjModel.class);
    System.out.println(model.getStr());
    System.out.println(model.getMyNum());
    System.out.println(model.getMyLong());
    System.out.println(model.getMyDouble());
    System.out.println(model.getMyUser().getUserId());
    System.out.println(model.getMyUser().isEnabled());
    System.out.println(model.getMyMap().get("first").getUserId());
    System.out.println(model.getMyMap().get("first").isEnabled());
    System.out.println(model.getUsersStatus().get(0).getUserId() + "|" + model.getUsersStatus().get(0).isEnabled());
    System.out.println(model.getUsersStatus().get(1).getUserId() + "|" + model.getUsersStatus().get(1).isEnabled());
    System.out.println(model.getStrArray().get(1));
    System.out.println(model.getIntArray().get(1));
    System.out.println(model.getWrapDouble());
  }

  @Test
  public void testToMap() {
    MyObj.Builder builder = MyObj.newBuilder();
    // MyObj.getDescriptor()
    builder.setStr("test");
    builder.setMyNum(100);
    Abc.Builder abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("myUserId");
    abcBuilder.setEnabled(true);
    builder.setMyUser(abcBuilder.build());
    abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("abc");
    abcBuilder.setEnabled(false);
    java.util.Map<java.lang.String, Abc> values = new HashMap<>();
    values.put("first", abcBuilder.build());
    builder.putAllMyMap(values);
    Map<Integer, String> simpleMap = new HashMap<>();
    simpleMap.put(1, "jack");
    builder.putAllSimpleMap(simpleMap);
    builder.setWrapDouble(DoubleValue.newBuilder().setValue(2.2).build());
    builder.addStrArray("str1");
    abcBuilder = Abc.newBuilder();
    abcBuilder.setUserId("listAbc");
    builder.addUsersStatus(abcBuilder.build());
    MyObj proto = builder.build();

    HashMap<String, Object> map = ProtobufConverter.toMap(proto, HashMap.class);

    assertEquals("test", map.get("str"));
    assertEquals(100, map.get("myNum"));
    assertEquals("myUserId", ((Map<String, Object>)map.get("myUser")).get("userId"));
    assertEquals("jack", ((Map<Integer, String>) map.get("simpleMap")).get(1));
    Map<String, Object> abc = ((Map<Integer, Map<String, Object>>) map.get("myMap")).get("first");
    assertEquals("abc", abc.get("userId"));
    assertEquals(false, abc.get("enabled"));
    assertEquals(2.2, map.get("wrapDouble"));
    List<String> ls = (List<String>) map.get("strArray");
    assertEquals("str1", ls.get(0));
    List<Map<String, Object>> userStatus = (List<Map<String, Object>>) map.get("usersStatus");
    assertEquals("listAbc", userStatus.get(0).get("userId"));

    Map<String, Object> imap = ProtobufConverter.toMap(proto, Map.class);
    assertEquals("test", imap.get("str"));
    assertEquals(100, imap.get("myNum"));
    assertEquals("myUserId", ((Map<String, Object>)imap.get("myUser")).get("userId"));
    assertEquals("jack", ((Map<Integer, String>) imap.get("simpleMap")).get(1));
    abc = ((Map<Integer, Map<String, Object>>) imap.get("myMap")).get("first");
    assertEquals("abc", abc.get("userId"));
    assertEquals(false, abc.get("enabled"));
    assertEquals(2.2, imap.get("wrapDouble"));
    ls = (List<String>) imap.get("strArray");
    assertEquals("str1", ls.get(0));
    userStatus = (List<Map<String, Object>>) imap.get("usersStatus");
    assertEquals("listAbc", userStatus.get(0).get("userId"));
  }

  @Test
  public void testMapToProto() {
    Map<String, Object> map = new HashMap<>();
    map.put("str", "strValue");
    map.put("myNum", 100);
    map.put("myLong", 1000L);
    map.put("myDouble", 2.55d);
    map.put("wrapDouble", 3.33d);
    Map<String, Object> myUser = new HashMap<>();
    myUser.put("userId", "myUserId");
    myUser.put("enabled", true);
    map.put("myUser", myUser);
    Map<Integer, String> simpleMap = new HashMap<>();
    simpleMap.put(1, "1s");
    map.put("simpleMap", simpleMap);
    Map<String,Map<String,Object>> myMap = new HashMap<>();
    Map<String, Object> abc = new HashMap<>();
    abc.put("userId", "abcUserId");
    abc.put("enabled", true);
    myMap.put("m1", abc);
    map.put("myMap", myMap);
    List<Integer> intArray = new ArrayList<>();
    intArray.add(201);
    map.put("intArray", intArray);
    List<String> strArray = new ArrayList<>();
    strArray.add("s201");
    map.put("strArray", strArray);
    List<Map<String,Object>> usersStatus = new ArrayList<Map<String,Object>>();
    Map<String, Object> userStatus = new HashMap<>();
    userStatus.put("userId", "lstUserId");
    userStatus.put("enabled", true);
    usersStatus.add(userStatus);
    map.put("usersStatus", usersStatus);
    
    MyObj myObj = MapConverter.toProtobuf(map, MyObj.class);
    
    assertEquals("strValue", myObj.getStr());
    assertEquals(100, myObj.getMyNum());
    assertEquals(1000L, myObj.getMyLong());
    assertEquals(2.55d, myObj.getMyDouble(), 0);
    assertEquals(3.33d, myObj.getWrapDouble().getValue(), 0);
    assertEquals("myUserId", myObj.getMyUser().getUserId());
    assertEquals(true, myObj.getMyUser().getEnabled());
    assertEquals("1s", myObj.getSimpleMapMap().get(1));
    assertEquals("abcUserId", myObj.getMyMapMap().get("m1").getUserId());
    assertEquals(true, myObj.getMyMapMap().get("m1").getEnabled());
    assertEquals(201, myObj.getIntArray(0));
    assertEquals("s201", myObj.getStrArray(0));
    assertEquals("lstUserId", myObj.getUsersStatus(0).getUserId());
  }

  @Test
  public void testpojo2Proto() {
    MyObjModel model = new MyObjModel();
    model.setMyNum(100);
    model.setStr("abc");
    model.setMyFlag2(true);
    List<AbcModel> usersStatus = new ArrayList<>();
    AbcModel abcModel = new AbcModel();
    abcModel.setUserId("u1");
    abcModel.setEnabled(true);
    usersStatus.add(abcModel);
    model.setUsersStatus(usersStatus);
    ArrayList<String> strArray = new ArrayList<>();
    strArray.add("s1");
    model.setStrArray(strArray);
    model.setWrapDouble(2.2);
    AbcModel myUser = new AbcModel();
    myUser.setUserId("myuser");
    model.setMyUser(myUser);
    model.setMyDouble(33.3);
    List<Integer> intArray = new ArrayList<>();
    intArray.add(500);
    model.setIntArray(intArray);
    Map<Integer, String> simpleMap = new HashMap<>();
    simpleMap.put(1, "mv1");
    model.setSimpleMap(simpleMap);
    {
      Map<String, AbcModel> myMap = new HashMap<>();
      AbcModel abcModelValue = new AbcModel();
      abcModelValue.setUserId("abcModelValue");
      myMap.put("abcModelKey", abcModelValue);
      model.setMyMap(myMap);
    }
    MyObj myObj = PlainObjectConverter.toProtobuf(model, MyObj.class);
    System.out.println(myObj.getMyNum());
    System.out.println(myObj.getStr());
    System.out.println(myObj.getMyLong());// by default long is 0 in POJO, and by default in proto
                                          // long will return 0
    // It's better to use Long on both side
    System.out.println(myObj.getUsersStatusList().get(0).getUserId());
    System.out.println(myObj.getUsersStatusList().get(0).getEnabled());
    System.out.println(myObj.getStrArrayList().get(0));
    System.out.println(myObj.getWrapDouble().getValue());
    System.out.println(myObj.getMyUser().getUserId());
    System.out.println(myObj.getMyDouble());
    System.out.println(myObj.getIntArrayList().get(0));
    System.out.println(myObj.getSimpleMapMap().get(1));
    System.out.println(myObj.getMyMapMap().get("abcModelKey").getUserId());
    System.out.println("end");

  }

}
