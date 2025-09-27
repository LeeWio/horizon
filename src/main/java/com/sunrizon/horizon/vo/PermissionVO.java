package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.PermissionType;

import lombok.Data;

@Data
public class PermissionVO {
  PermissionType name;
  String description;
}
