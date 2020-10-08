/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
public class TbProductCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 涓婚敭
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 鍚嶇О
     */
    private String name;

    /**
     * 鐖剁骇鐩綍id
     */
    @TableField("parentId")
    private Integer parentId;

    /**
     * 绾у埆(1:涓€绾?2锛氫簩绾?3锛氫笁绾?
     */
    private Integer type;

    /**
     * 鍥炬爣
     */
    @TableField("iconClass")
    private String iconClass;
}
