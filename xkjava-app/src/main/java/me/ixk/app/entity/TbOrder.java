/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class TbOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @TableField("userName")
    private String userName;

    @TableField("customerPhone")
    private String customerPhone;

    @TableField("userAddress")
    private String userAddress;

    @TableField("proCount")
    private Integer proCount;

    private BigDecimal cost;

    @TableField("serialNumber")
    private String serialNumber;

    private Integer status;

    @TableField("payType")
    private Integer payType;

    @TableField("createdBy")
    private Long createdBy;

    @TableField("creationDate")
    private LocalDateTime creationDate;

    @TableField("modifyBy")
    private Long modifyBy;

    @TableField("modifyDate")
    private LocalDateTime modifyDate;
}
