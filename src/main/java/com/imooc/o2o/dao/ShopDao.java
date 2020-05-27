package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopDao {
    /**
     * 分页查询店铺，可输入的条件有，店铺名(模糊)查询，店铺状态，店铺类别，区域id，owner
     * @param shopCondition
     * @param roeIndex 从第几行开始取数据
     * @param pageSize 返回的条数
     * @return
     */
    List<Shop> queryShopList(@Param("shopCondition")Shop shopCondition,
                             @Param("rowIndex")int roeIndex,
                             @Param("pageSize")int pageSize);

    /**
     * 返回queryShopList总数
     * @param shopCondition
     * @return
     */
    int queryShopCount(@Param("shopCondition")Shop shopCondition);
    /**
     * 通过owner id 查询店铺
     */
    Shop queryByShopId(long shopId);

    /**
     * 新增店铺
     */
    int insertShop(Shop shop);

    /**
     * 更新店铺
     */
    int updateShop(Shop shop);
}
