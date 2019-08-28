package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.Rules;

public interface NovaFlavorDao extends BaseDAO<NovaFlavor>{

	List<NovaFlavor> get();

	NovaFlavor getByImage(Rules entity);

}
