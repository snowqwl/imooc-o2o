package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ShopCategoryDao;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.ShopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {

    @Autowired
    private ShopCategoryDao shopCategoryDao;

    /**
     * 根据查询条件获取ShopCategory列表
     * @param shopCategoryCondition
     * @return
     * @throws IOException
     */
    @Override
    public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) throws IOException {
        return shopCategoryDao.queryShopCategory(shopCategoryCondition);
    }
}
