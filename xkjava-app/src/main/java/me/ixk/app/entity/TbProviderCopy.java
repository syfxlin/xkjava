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
 * <p>
 *
 * </p>
 *
 * @author syfxlin
 * @since 2020-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TbProviderCopy implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 供应商编码
     */
    @TableField("proCode")
    private String proCode;

    /**
     * 供应商名称
     */
    @TableField("proName")
    private String proName;

    /**
     * 供应商详细描述
     */
    @TableField("proDesc")
    private String proDesc;

    /**
     * 供应商联系人
     */
    @TableField("proContact")
    private String proContact;

    /**
     * 联系电话
     */
    @TableField("proPhone")
    private String proPhone;

    /**
     * 地址
     */
    @TableField("proAddress")
    private String proAddress;

    /**
     * 传真
     */
    @TableField("proFax")
    private String proFax;

    /**
     * 创建者（userId）
     */
    @TableField("createdBy")
    private Long createdBy;

    @TableField("creationDate")
    private LocalDateTime creationDate;

    /**
     * 更新时间
     */
    @TableField("modifyDate")
    private LocalDateTime modifyDate;

    /**
     * 更新者（userId）
     */
    @TableField("modifyBy")
    private Long modifyBy;

    /**
     * 企业营业执照的存储路径
     */
    @TableField("companyLicPicPath")
    private String companyLicPicPath;

    /**
     * 组织机构代码证的存储路径
     */
    @TableField("orgCodePicPath")
    private String orgCodePicPath;
}
