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
public class TbBill implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账单编码
     */
    @TableField("billCode")
    private String billCode;

    /**
     * 商品编号
     */
    @TableField("productId")
    private Long productId;

    /**
     * 商品名称
     */
    @TableField("productName")
    private String productName;

    /**
     * 商品描述
     */
    @TableField("productDesc")
    private String productDesc;

    /**
     * 商品单位
     */
    @TableField("productUnit")
    private String productUnit;

    /**
     * 商品数量
     */
    @TableField("productCount")
    private BigDecimal productCount;

    /**
     * 商品总额
     */
    @TableField("totalPrice")
    private BigDecimal totalPrice;

    /**
     * 是否支付（1：未支付 2：已支付）
     */
    @TableField("isPayment")
    private Integer isPayment;

    /**
     * 创建者（userId）
     */
    @TableField("createdBy")
    private Long createdBy;

    @TableField("creationDate")
    private LocalDateTime creationDate;

    @TableField("modifyBy")
    private Long modifyBy;

    /**
     * 更新时间
     */
    @TableField("modifyDate")
    private LocalDateTime modifyDate;

    /**
     * 供应商ID
     */
    @TableField("providerId")
    private Integer providerId;
}
