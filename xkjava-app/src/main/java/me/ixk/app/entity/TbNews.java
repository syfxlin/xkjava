/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author syfxlin
 * @since 2020-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TbNews implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    @TableField("createdBy")
    private Long createdBy;

    @TableField("creationDate")
    private LocalDateTime creationDate;

    @TableField("modifyBy")
    private Long modifyBy;

    @TableField("modifyDate")
    private LocalDateTime modifyDate;
}
