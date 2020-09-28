/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@EqualsAndHashCode
@Accessors(chain = true)
public class Visitors implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Integer id;

    private Long counts;
}
