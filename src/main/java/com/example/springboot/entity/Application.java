package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;



/**
 * <p>
 * 实体类
 * </p>
 */

@Data
@TableName(value = "application")
public class Application {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String address;

    private String place;

    private String phone;

    private String sex;

    private String age;

    private String house;

    private String family;

    private String status;

    private Integer userId;

    private String instruction;

    private Integer petId;

    private String wechat;

    private String economy;

    private String animal;

    private String category;

    private Integer fromId;

}
