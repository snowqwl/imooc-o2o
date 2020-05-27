package com.imooc.o2o.service;

import com.imooc.o2o.entity.ShopCategory;

import java.io.IOException;
import java.util.List;

public interface ShopCategoryService {
    List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) throws IOException;
}
