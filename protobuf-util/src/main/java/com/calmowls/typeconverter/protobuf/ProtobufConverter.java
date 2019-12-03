package com.calmowls.typeconverter.protobuf;

import java.lang.reflect.Field;
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

public class ProtobufConverter {
  public static <T extends GeneratedMessageV3, E> E toPlainObject(T proto, Class<E> pojo) {
    /*
     * TODO: type error handle like: Map -> String
     * 
     */
    E e = null;
    try {
      Descriptor desc = proto.getDescriptorForType();

      if (pojo != null) {
        e = pojo.newInstance();
      }

      List<FieldDescriptor> fields = desc.getFields();
      for (int i = 0; i < fields.size(); i++) {
        FieldDescriptor field = fields.get(i);
        if (field.getJavaType() != JavaType.MESSAGE && !field.isRepeated()) {
          String setMethod =
              "set" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
          try {
            Object value = proto.getField(field);
            if (e != null) {
              Method setter = e.getClass().getDeclaredMethod(setMethod,
                  e.getClass().getDeclaredField(field.getJsonName()).getType());
              if (setter != null) {
                setter.invoke(e, value);
              }
            }
          } catch (Exception noex) {
          }
        } else {
          if (field.isMapField() && field.isRepeated()) {
            String getMethod =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "Map";
            String setMethod =
                "set" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Type returnType = proto.getClass().getDeclaredMethod(getMethod).getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
              try {
                ParameterizedType typename = (ParameterizedType) returnType;
                Type valueType = typename.getActualTypeArguments()[1];
                Field pojoField = e.getClass().getDeclaredField(field.getJsonName());
                java.util.Map<Object, Object> mapInstance;
                if (pojoField.getType().isInterface()) {
                  // java.util.Map, by default use HashMap
                  mapInstance = new HashMap<Object, Object>();
                } else {
                  // other types like HashMap, LinkedMap
                  mapInstance = (java.util.Map<Object, Object>) pojoField.getType().newInstance();
                }
                java.util.Map<?, ?> value =
                    (java.util.Map<?, ?>) proto.getClass().getDeclaredMethod(getMethod).invoke(proto);
                Type pojoFieldType = e.getClass().getDeclaredField(field.getJsonName()).getGenericType();
                if (pojoFieldType instanceof ParameterizedType) {
                  ParameterizedType pojoFieldTypeName = (ParameterizedType) pojoFieldType;
                  Type pojoValueType = pojoFieldTypeName.getActualTypeArguments()[1];
                  for (Map.Entry<?, ?> entry : value.entrySet()) {
                    // Check value type
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) valueType)) {
                      Object pojoValue = ProtobufConverter.toPlainObject((GeneratedMessageV3) entry.getValue(), (Class) pojoValueType);
                      mapInstance.put(entry.getKey(), pojoValue);
                    } else {
                      // No check for keys
                      mapInstance.put(entry.getKey(), entry.getValue());
                    }

                  }
                }

                if (e != null) {
                  Method setter = e.getClass().getDeclaredMethod(setMethod,
                      e.getClass().getDeclaredField(field.getJsonName()).getType());
                  if (setter != null) {
                    setter.invoke(e, mapInstance);
                  }
                }
              } catch (Exception noex) {
              }
            }
          } else if (field.isRepeated() && !field.isMapField()) {
            String getMethod =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "List";
            // Pojo setter
            String setMethod =
                "set" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Type returnType = proto.getClass().getDeclaredMethod(getMethod).getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
              ParameterizedType typename = (ParameterizedType) returnType;
              Type elmType = typename.getActualTypeArguments()[0];
              java.util.List<Object> listInstance;
              try {
                Field pojoField = e.getClass().getDeclaredField(field.getJsonName());

                if (pojoField.getType().isInterface()) {
                  // java.util.Map, by default use ArrayList
                  listInstance = new ArrayList<>();
                } else {
                  // other types like ArrayList, LinkedList
                  listInstance = (java.util.List<Object>) pojoField.getType().newInstance();
                }
                java.util.List<?> value =
                    (java.util.List<?>) proto.getField(field);
                Type pojoFieldType = e.getClass().getDeclaredField(field.getJsonName()).getGenericType();
                if (pojoFieldType instanceof ParameterizedType) {
                  ParameterizedType pojoFieldTypeName = (ParameterizedType) pojoFieldType;
                  Type pojoValueType = pojoFieldTypeName.getActualTypeArguments()[0];

                  for (Object entry : value) {
                    // Check value type
                    if (GeneratedMessageV3.class.isAssignableFrom((Class) elmType)) {
                      Object pojoValue = ProtobufConverter.toPlainObject((GeneratedMessageV3) entry, (Class) pojoValueType);
                      listInstance.add(pojoValue);
                    } else {
                      // No check for keys
                      listInstance.add(entry);
                    }

                  }
                }
                if (e != null) {
                  Method setter = e.getClass().getDeclaredMethod(setMethod,
                      e.getClass().getDeclaredField(field.getJsonName()).getType());
                  if (setter != null) {
                    setter.invoke(e, listInstance);
                  }
                }
              } catch (Exception noex) {
              }
            } else {
              // primitive repeated string
              try {
                Field pojoField = e.getClass().getDeclaredField(field.getJsonName());
                java.util.List<Object> listInstance;
                if (pojoField.getType().isInterface()) {
                  // java.util.Map, by default use ArrayList
                  listInstance = new ArrayList<>();
                } else {
                  // other types like ArrayList, LinkedList
                  listInstance = (java.util.List<Object>) pojoField.getType().newInstance();
                }
                java.util.List<?> value =
                    (java.util.List<?>) proto.getClass().getDeclaredMethod(getMethod).invoke(proto);
                for (Object obj : value) {
                  listInstance.add(obj);
                }

                if (e != null) {
                  Method setter = e.getClass().getDeclaredMethod(setMethod,
                      e.getClass().getDeclaredField(field.getJsonName()).getType());
                  if (setter != null) {
                    setter.invoke(e, listInstance);
                  }
                }
              } catch (Exception noex) {
              }
            }
          } else {
            String getMethod =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);

            String setMethod =
                "set" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Class<?> clazz = proto.getClass().getDeclaredMethod(getMethod).getReturnType();
            try {
              if (GeneratedMessageV3.class.isAssignableFrom(clazz)) {
                Object obj = proto.getField(field);
                Object value = null;
                boolean canSet = true;
                // TODO: BytesValue
                if (DoubleValue.class.equals(clazz) || Int64Value.class.equals(clazz) || UInt64Value.class.equals(clazz)
                    || FloatValue.class.equals(clazz) || Int32Value.class.equals(clazz)
                    || UInt32Value.class.equals(clazz) || BoolValue.class.equals(clazz)
                    || StringValue.class.equals(clazz)) {
                  String protoHasMethod =
                      "has" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
                  boolean hasField = (boolean) proto.getClass().getDeclaredMethod(protoHasMethod).invoke(proto);
                  if (hasField) {
                    value = obj.getClass().getDeclaredMethod("getValue").invoke(obj);
                  } else {
                    canSet = false;
                  }
                } else {
                  value = ProtobufConverter.toPlainObject((GeneratedMessageV3) obj,
                      e.getClass().getDeclaredField(field.getJsonName()).getType());
                }

                if (canSet && e != null) {
                  Method setter = e.getClass().getDeclaredMethod(setMethod,
                      e.getClass().getDeclaredField(field.getJsonName()).getType());
                  if (setter != null) {
                    setter.invoke(e, value);
                  }
                }
              }
            } catch (Exception noex) {
            }
          }
        }
      }
    } catch (Exception ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    return e;
  }
  
  public static <T extends GeneratedMessageV3, E extends Map<String, Object>> E toMap(T proto, Class<E> pojo) {
    /*
     * TODO: type error handle like: Map -> String
     * 
     */
    E e = null;
    try {
      Descriptor desc = proto.getDescriptorForType();

      if (pojo.isInterface()) {
        e = (E) new HashMap<String, Object>();
      } else {
        e = pojo.newInstance();
      }

      List<FieldDescriptor> fields = desc.getFields();
      for (int i = 0; i < fields.size(); i++) {
        FieldDescriptor field = fields.get(i);
        if (field.getJavaType() != JavaType.MESSAGE && !field.isRepeated()) {
          try {
            Object value = proto.getField(field);
            if (e != null) {
              e.put(field.getJsonName(), value);
            }
          } catch (Exception noex) {
          }
        } else {
          if (field.isMapField() && field.isRepeated()) {
            String getMethod =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "Map";
            Type returnType = proto.getClass().getDeclaredMethod(getMethod).getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
              try {
                ParameterizedType typename = (ParameterizedType) returnType;
                Type valueType = typename.getActualTypeArguments()[1];
                java.util.Map<Object, Object> mapInstance;
                mapInstance = new HashMap<Object, Object>();
                java.util.Map<?, ?> value =
                    (java.util.Map<?, ?>) proto.getClass().getDeclaredMethod(getMethod).invoke(proto);
                for (Map.Entry<?, ?> entry : value.entrySet()) {
                  // Check value type
                  if (GeneratedMessageV3.class.isAssignableFrom((Class) valueType)) {
                    Object pojoValue = ProtobufConverter.toMap((GeneratedMessageV3) entry.getValue(), Map.class);
                    mapInstance.put(entry.getKey(), pojoValue);
                  } else {
                    // No check for keys
                    mapInstance.put(entry.getKey(), entry.getValue());
                  }

                }
                if (e != null) {
                  e.put(field.getJsonName(), mapInstance);
                }
              } catch (Exception noex) {
                noex.printStackTrace();
              }
            }
          } else if (field.isRepeated() && !field.isMapField()) {
            String getMethod =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1) + "List";
            Type returnType = proto.getClass().getDeclaredMethod(getMethod).getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
              ParameterizedType typename = (ParameterizedType) returnType;
              Type elmType = typename.getActualTypeArguments()[0];
              java.util.List<Object> listInstance;
              try {
                listInstance = new ArrayList<>();
                java.util.List<?> value = (java.util.List<?>) proto.getField(field);

                for (Object entry : value) {
                  // Check value type
                  if (GeneratedMessageV3.class.isAssignableFrom((Class) elmType)) {
                    Object pojoValue = ProtobufConverter.toMap((GeneratedMessageV3) entry, Map.class);
                    listInstance.add(pojoValue);
                  } else {
                    // No check for keys
                    listInstance.add(entry);
                  }

                }
                if (e != null) {
                  e.put(field.getJsonName(), listInstance);
                }
              } catch (Exception noex) {
              }
            } else {
              // primitive: repeated string
              try {
                java.util.List<Object> listInstance = new ArrayList<>();
                java.util.List<?> value =
                    (java.util.List<?>) proto.getClass().getDeclaredMethod(getMethod).invoke(proto);
                for (Object obj : value) {
                  listInstance.add(obj);
                }

                if (e != null) {
                  e.put(field.getJsonName(), listInstance);
                }
              } catch (Exception noex) {
              }
            }
          } else {
            String getMethod =
                "get" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
            Class<?> clazz = proto.getClass().getDeclaredMethod(getMethod).getReturnType();
            try {
              if (GeneratedMessageV3.class.isAssignableFrom(clazz)) {
                Object obj = proto.getField(field);
                Object value = null;
                boolean canSet = true;
                // TODO: BytesValue
                if (DoubleValue.class.equals(clazz) || Int64Value.class.equals(clazz) || UInt64Value.class.equals(clazz)
                    || FloatValue.class.equals(clazz) || Int32Value.class.equals(clazz)
                    || UInt32Value.class.equals(clazz) || BoolValue.class.equals(clazz)
                    || StringValue.class.equals(clazz)) {
                  String protoHasMethod =
                      "has" + field.getJsonName().substring(0, 1).toUpperCase() + field.getJsonName().substring(1);
                  boolean hasField = (boolean) proto.getClass().getDeclaredMethod(protoHasMethod).invoke(proto);
                  if (hasField) {
                    value = obj.getClass().getDeclaredMethod("getValue").invoke(obj);
                  } else {
                    canSet = false;
                  }
                } else {
                  value = ProtobufConverter.toMap((GeneratedMessageV3) obj, Map.class);
                }
                if (canSet && e != null) {
                  e.put(field.getJsonName(), value);
                }
              }
            } catch (Exception noex) {
            }
          }
        }
      }
    } catch (Exception ex) {
    }
    return e;
  }
}
