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
@TableName(value = "pet")
public class Pet {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String age;

    private String sex;

    private String health;

    private String img;

    private String imgList;

    private String content;

    private String address;

    private String time;

    private String status;

    private Integer typeId;

    private Integer userId;

    private String category;

}
