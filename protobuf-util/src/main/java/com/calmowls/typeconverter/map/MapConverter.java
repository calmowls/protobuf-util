package com.calmowls.typeconverter.map;

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

public class MapConverter {
  public static <T extends GeneratedMessageV3, E extends Map<String, Object>> T toProtobuf(E map, Class<T> proto) {
    try {
      Method descMethod = proto.getDeclaredMethod("getDescriptor");
      com.google.protobuf.Descriptors.Descriptor des = (Descriptor) descMethod.invoke(null, null);
      GeneratedMessageV3.Builder<?> builder =
          (GeneratedMessageV3.Builder<?>) proto.getDeclaredMethod("newBuilder").invoke(null, null);
      List<FieldDescriptor> fields = des.getFields();
      for (int i = 0; i < fields.size(); i++) {
        FieldDescriptor field = fields.get(i);
        if (!map.containsKey(field.getJsonName())) {
          continue;
        }
        Object value = map.get(field.getJsonName());
        if (field.getJavaType() != JavaType.MESSAGE && !field.isRepeated()) {
          // simple type, string, int32...

          if (value != null) {
            builder.setField(field, value);
          }
        } else {
          if (field.isMapField() && field.isRepeated()) {
            // map
            String protoGetMethodName =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "Map";
            String protoSetMethodName =
                "putAll" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Type returnType = proto.getDeclaredMethod(protoGetMethodName).getGenericReturnType();
            java.util.Map<Object, Object> mapInstance = new HashMap<Object, Object>();
            if (value != null) {
              if (returnType instanceof ParameterizedType) {
                try {
                  ParameterizedType typename = (ParameterizedType) returnType;
                  Type keyType = typename.getActualTypeArguments()[0];
                  Type valueType = typename.getActualTypeArguments()[1];
                  for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                    Object key;
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) keyType)) {
                      key = MapConverter.toProtobuf(((Map) entry.getKey()), (Class) keyType);
                    } else {
                      key = entry.getKey();
                    }
                    Object subValue;
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) valueType)) {
                      subValue = MapConverter.toProtobuf((Map) entry.getValue(), (Class) valueType);
                    } else {
                      subValue = entry.getValue();
                    }
                    mapInstance.put(key, subValue);
                  }
                  if (mapInstance.size() > 0) {
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
            try {
              // TODO:List
              String protoGetMethodName =
                  "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "List";

              if (value != null) {
                List<?> lstValue = (List<?>) value;
                // init list
                List<Object> listInstance = new ArrayList<>();
                Type returnType = proto.getDeclaredMethod(protoGetMethodName).getGenericReturnType();
                if (returnType instanceof ParameterizedType) {
                  ParameterizedType typename = (ParameterizedType) returnType;
                  Type elmType = typename.getActualTypeArguments()[0];
                  for (int j = 0; j < lstValue.size(); j++) {
                    Object listValue = lstValue.get(j);
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) elmType)) {
                      Object protoListValue = MapConverter.toProtobuf((Map) listValue, (Class) elmType);
                      listInstance.add(protoListValue);
                    } else {
                      // repeated int
                      listInstance.add(listValue);
                    }
                  }
                } else {
                  // repeated string
                  // TODO: check type, if pojo is Integer, proto is String
                  for (int j = 0; j < lstValue.size(); j++) {
                    Object listValue = lstValue.get(j);
                    listInstance.add(listValue);
                  }
                }
                if (listInstance.size() > 0) {
                  builder.setField(field, listInstance);
                }
              }
            } catch (Exception noex) {
              // noex.printStackTrace();
            }
          } else {
            // Message or google pre-defined Message
            try {
              String protoGetMethodName =
                  "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
              Class<?> clazz = proto.getDeclaredMethod(protoGetMethodName).getReturnType();
              if (GeneratedMessageV3.class.isAssignableFrom(clazz)) {
                if (DoubleValue.class.equals(clazz) || Int64Value.class.equals(clazz) || UInt64Value.class.equals(clazz)
                    || FloatValue.class.equals(clazz) || Int32Value.class.equals(clazz)
                    || UInt32Value.class.equals(clazz) || BoolValue.class.equals(clazz)
                    || StringValue.class.equals(clazz)) {
                  GeneratedMessageV3.Builder<?> googleBuilder =
                      (GeneratedMessageV3.Builder<?>) clazz.getDeclaredMethod("newBuilder").invoke(null);
                  googleBuilder = (GeneratedMessageV3.Builder<?>) googleBuilder.getClass()
                      .getDeclaredMethod("setValue", (Class) value.getClass().getDeclaredField("TYPE").get(value))
                      .invoke(googleBuilder, value);
                  builder.setField(field, googleBuilder.build());
                } else {
                  Object subProtoValue = MapConverter.toProtobuf((Map) value, (Class) clazz);
                  builder.setField(field, subProtoValue);
                }
              }
            } catch (Exception noex) {
              // noex.printStackTrace();
            }
          }
        }
      }
      return (T) builder.build();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
