/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.ixk.app.entity.Books;
import me.ixk.framework.annotation.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Otstar Lin
 * @date 2020/10/16 上午 10:17
 */
@Mapper
public interface BooksMapper extends BaseMapper<Books> {
    @Select("SELECT * FROM books")
    IPage<Books> getByPage(Page<Books> page);
}
