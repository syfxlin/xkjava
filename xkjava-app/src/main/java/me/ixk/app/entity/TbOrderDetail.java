/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class TbOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @TableField("orderId")
    private Integer orderId;

    @TableField("productId")
    private Integer productId;

    private Integer quantity;

    private BigDecimal cost;
}
