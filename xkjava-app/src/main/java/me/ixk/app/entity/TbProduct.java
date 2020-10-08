/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class TbProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 摆放位置
     */
    private String placement;

    /**
     * 库存
     */
    private BigDecimal stock;

    /**
     * 分类1
     */
    @TableField("categoryLevel1Id")
    private Integer categoryLevel1Id;

    /**
     * 分类2
     */
    @TableField("categoryLevel2Id")
    private Integer categoryLevel2Id;

    /**
     * 分类3
     */
    @TableField("categoryLevel3Id")
    private Integer categoryLevel3Id;

    /**
     * 文件名称
     */
    @TableField("fileName")
    private String fileName;

    /**
     * 是否删除(1：删除 0：未删除)
     */
    @TableField("isDelete")
    private Integer isDelete;

    /**
     * 创建者（userId）
     */
    @TableField("createdBy")
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField("creationDate")
    private LocalDateTime creationDate;

    /**
     * 更新者（userId）
     */
    @TableField("modifyBy")
    private Long modifyBy;

    /**
     * 更新时间
     */
    @TableField("modifyDate")
    private LocalDateTime modifyDate;
}
