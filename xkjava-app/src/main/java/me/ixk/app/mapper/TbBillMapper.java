/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import me.ixk.app.entity.TbBill;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author syfxlin
 * @since 2020-10-08
 */
public interface TbBillMapper extends BaseMapper<TbBill> {
    @Select("SELECT * FROM tb_bill")
    List<TbBill> getAll();
}
