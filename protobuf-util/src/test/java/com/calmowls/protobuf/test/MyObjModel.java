package com.calmowls.protobuf.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyObjModel {
  private String str;
  private int myNum;
  private AbcModel myUser;
  private Map<String,AbcModel> myMap;
  private List<AbcModel> usersStatus;
  private Long myLong;
  private double myDouble;
  private ArrayList<String> strArray;
  private List<Integer> intArray;
  private Double wrapDouble;
  private Boolean myFlag;
  private boolean myFlag2;
  private Map<Integer,String> simpleMap;

  public String getStr() {
    return str;
  }

  public void setStr(String str) {
    this.str = str;
  }

  public int getMyNum() {
    return myNum;
  }

  public void setMyNum(int myNum) {
    this.myNum = myNum;
  }

  public AbcModel getMyUser() {
    return myUser;
  }

  public void setMyUser(AbcModel myUser) {
    this.myUser = myUser;
  }

  public Map<String, AbcModel> getMyMap() {
    return myMap;
  }

  public void setMyMap(Map<String, AbcModel> myMap) {
    this.myMap = myMap;
  }

  public List<AbcModel> getUsersStatus() {
    return usersStatus;
  }

  public void setUsersStatus(List<AbcModel> usersStatus) {
    this.usersStatus = usersStatus;
  }

  public Long getMyLong() {
    return myLong;
  }

  public void setMyLong(Long myLong) {
    this.myLong = myLong;
  }

  public double getMyDouble() {
    return myDouble;
  }

  public void setMyDouble(double myDouble) {
    this.myDouble = myDouble;
  }

  public ArrayList<String> getStrArray() {
    return strArray;
  }

  public void setStrArray(ArrayList<String> strArray) {
    this.strArray = strArray;
  }

  public List<Integer> getIntArray() {
    return intArray;
  }

  public void setIntArray(List<Integer> intArray) {
    this.intArray = intArray;
  }

  public Double getWrapDouble() {
    return wrapDouble;
  }

  public void setWrapDouble(Double wrapDouble) {
    this.wrapDouble = wrapDouble;
  }

  public Boolean getMyFlag() {
    return myFlag;
  }

  public void setMyFlag(Boolean myFlag) {
    this.myFlag = myFlag;
  }

  public boolean isMyFlag2() {
    return myFlag2;
  }

  public void setMyFlag2(boolean myFlag2) {
    this.myFlag2 = myFlag2;
  }

  public Map<Integer, String> getSimpleMap() {
    return simpleMap;
  }

  public void setSimpleMap(Map<Integer, String> simpleMap) {
    this.simpleMap = simpleMap;
  }
  
}
