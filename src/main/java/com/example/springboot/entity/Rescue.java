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
@TableName(value = "rescue")
public class Rescue {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String userName;

    private String phone;

    private String description;

    private String address;

    private String time;

    private String content;

    private Integer userId;

    private String status;

}
