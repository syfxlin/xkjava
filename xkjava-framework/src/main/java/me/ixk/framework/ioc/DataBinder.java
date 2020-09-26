/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import me.ixk.framework.annotations.DataBind;

public interface DataBinder {
  <T> T getObject(String name, Class<T> type);

  <T> T getObject(String name, Class<T> type, DataBind dataBind);
}
