package com.calmowls.typeconverter.plainobject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.protobuf.BoolValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

public class PlainObjectConverter {
  public static <T extends GeneratedMessageV3, E> T toProtobuf(E pojo, Class<T> proto) {
    try {
      Method descMethod = proto.getDeclaredMethod("getDescriptor");
      com.google.protobuf.Descriptors.Descriptor des = (Descriptor) descMethod.invoke(null, null);
      GeneratedMessageV3.Builder<?> builder =
          (GeneratedMessageV3.Builder<?>) proto.getDeclaredMethod("newBuilder").invoke(null, null);
      List<FieldDescriptor> fields = des.getFields();
      for (int i = 0; i < fields.size(); i++) {
        FieldDescriptor field = fields.get(i);
        if (field.getJavaType() != JavaType.MESSAGE && !field.isRepeated()) {
          // simple type, string, int32...
          String pojoGetMethodName =
              "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
          // TODO:Boolean get method will be different depending on field name
          if (com.google.protobuf.Descriptors.FieldDescriptor.Type.BOOL.equals(field.getType())) {
            pojoGetMethodName =
                "is" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
          }
          try {
            Method pojoGetMethod = pojo.getClass().getDeclaredMethod(pojoGetMethodName);
            Object value = pojoGetMethod.invoke(pojo);
            System.out.println(value);
            if (value != null) {
              builder.setField(field, value);
            }
          } catch (NoSuchMethodException e) {

          }

        } else {
          if (field.isMapField() && field.isRepeated()) {
            // TODO:Map
            String protoGetMethodName =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "Map";
            String protoSetMethodName =
                "putAll" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            String pojoGetMethodName =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Type returnType = proto.getDeclaredMethod(protoGetMethodName).getGenericReturnType();
            Method pojoGetMethod = pojo.getClass().getDeclaredMethod(pojoGetMethodName);
            Map<?, ?> pojoValue = (Map<?, ?>) pojoGetMethod.invoke(pojo);
            java.util.Map<Object, Object> mapInstance = new HashMap<Object, Object>();
            if (pojoValue != null) {
              if (returnType instanceof ParameterizedType) {
                try {
                  ParameterizedType typename = (ParameterizedType) returnType;
                  Type keyType = typename.getActualTypeArguments()[0];
                  Type valueType = typename.getActualTypeArguments()[1];
                  for (Map.Entry<?, ?> entry : pojoValue.entrySet()) {
                    Object key;
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) keyType)) {
                      key = PlainObjectConverter.toProtobuf(entry.getKey(), (Class) keyType);
                    } else {
                      key = entry.getKey();
                    }
                    Object value;
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) valueType)) {
                      value = PlainObjectConverter.toProtobuf(entry.getValue(), (Class) valueType);
                    } else {
                      value = entry.getValue();
                    }
                    mapInstance.put(key, value);
                  }
                  if (mapInstance.size() > 0) {
                    // builder.setField(field, mapInstance);
                    Method protoSetMethod =
                        builder.getClass().getDeclaredMethod(protoSetMethodName, (Class) typename.getRawType());
                    protoSetMethod.invoke(builder, mapInstance);
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            }

          } else if (field.isRepeated() && !field.isMapField()) {
            // List
            String protoGetMethodName =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "List";
            // Pojo setter
            String pojoGetMethodName =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Method pojoGetMethod = pojo.getClass().getDeclaredMethod(pojoGetMethodName);
            List<?> pojoValue = (List<?>) pojoGetMethod.invoke(pojo);
            if (pojoValue != null) {
              // init list
              List<Object> listInstance = new ArrayList<>();
              Type returnType = proto.getDeclaredMethod(protoGetMethodName).getGenericReturnType();
              if (returnType instanceof ParameterizedType) {
                ParameterizedType typename = (ParameterizedType) returnType;
                Type elmType = typename.getActualTypeArguments()[0];
                for (int j = 0; j < pojoValue.size(); j++) {
                  Object listValue = pojoValue.get(j);
                  if (GeneratedMessageV3.class.isAssignableFrom((Class) elmType)) {
                    Object protoListValue = PlainObjectConverter.toProtobuf(listValue, (Class) elmType);
                    listInstance.add(protoListValue);
                  } else {
                    // repeated int
                    listInstance.add(listValue);
                  }
                }
              } else {
                // repeated string
                // TODO: check type, if pojo is Integer, proto is String
                for (int j = 0; j < pojoValue.size(); j++) {
                  Object listValue = pojoValue.get(j);
                  listInstance.add(listValue);
                }
              }
              if (listInstance.size() > 0) {
                builder.setField(field, listInstance);
              }
            }

          } else {
            // Message or google pre-defined Message
            try {
              String protoGetMethodName =
                  "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
              String pojoGetMethodName =
                  "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
              Class<?> clazz = proto.getDeclaredMethod(protoGetMethodName).getReturnType();

              if (GeneratedMessageV3.class.isAssignableFrom(clazz)) {
                Method pojoGetMethod = pojo.getClass().getDeclaredMethod(pojoGetMethodName);
                Object pojoValue = pojoGetMethod.invoke(pojo);
                if (DoubleValue.class.equals(clazz) || Int64Value.class.equals(clazz) || UInt64Value.class.equals(clazz)
                    || FloatValue.class.equals(clazz) || Int32Value.class.equals(clazz)
                    || UInt32Value.class.equals(clazz) || BoolValue.class.equals(clazz)
                    || StringValue.class.equals(clazz)) {
                  GeneratedMessageV3.Builder<?> googleBuilder =
                      (GeneratedMessageV3.Builder<?>) clazz.getDeclaredMethod("newBuilder").invoke(null);
                  googleBuilder = (GeneratedMessageV3.Builder<?>) googleBuilder.getClass()
                      .getDeclaredMethod("setValue",
                          (Class) pojoValue.getClass().getDeclaredField("TYPE").get(pojoValue))
                      .invoke(googleBuilder, pojoValue);
                  builder.setField(field, googleBuilder.build());
                } else {
                  Object subProtoValue = PlainObjectConverter.toProtobuf(pojoValue, (Class) clazz);
                  builder.setField(field, subProtoValue);
                }
              }
            } catch (Exception noex) {
              // noex.printStackTrace();
            }
          }
        }
      } ;
      return (T) builder.build();
    } catch (NoSuchMethodException e) {
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
