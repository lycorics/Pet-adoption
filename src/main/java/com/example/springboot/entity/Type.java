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
@TableName(value = "type")
public class Type {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String origin;

    private String body;

    private String height;

    private String hair;

    private String lifespan;

    private Integer trainability;

    private Integer shed;

    private String img;

    private String info;

    private String content;


}
