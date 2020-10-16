/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Otstar Lin
 * @date 2020/10/16 上午 10:14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Books implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("author")
    private String author;

    @TableField("publisher")
    private String publisher;

    @TableField("time")
    private LocalDateTime time;

    @TableField("isbn")
    private String isbn;

    @TableField("introduction")
    private String introduction;
}
