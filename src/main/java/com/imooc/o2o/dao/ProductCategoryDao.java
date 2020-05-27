package com.imooc.o2o.dao;

import com.imooc.o2o.entity.ProductCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCategoryDao {
    /**
     * 通过商品shop id 查询店铺商品类别
     * @param shopId
     * @return
     */
    List<ProductCategory> queryProductCategoryList(long shopId);

    /**
     *批量新增商品类别
     */
    int batchInsertProductCategory(List<ProductCategory> productCategoryList);

        /**
         * 删除指定商品类型
         * @param productCategoryId
         * @param shopId
         * @return
         */
        int deleteProductCategory(@Param("productCategoryId")long productCategoryId,@Param("shopId")long shopId);
}
