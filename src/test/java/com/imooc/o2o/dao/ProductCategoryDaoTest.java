package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductCategory;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

//安照名字的顺序执行
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductCategoryDaoTest extends BaseTest {
    @Autowired
    private ProductCategoryDao productCategoryDao;
    @Test
    public void testBqueryProductCategoryList() {
        long shopId=1;
        List<ProductCategory> productCategoryList =
                productCategoryDao.queryProductCategoryList(shopId);
        System.out.println("该店铺自定义类别数为："+productCategoryList.size());
    }
    @Test
    public void testABatchInsertProductCategory(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryName("商品类别1");
        productCategory.setPriority(1);
        productCategory.setCreateTime(new Date());
        productCategory.setShopId(1L);
        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setProductCategoryName("商品类别2");
        productCategory2.setPriority(2);
        productCategory2.setCreateTime(new Date());
        productCategory2.setShopId(1L);
        List<ProductCategory> productCategoryList=
                new ArrayList<ProductCategory>();
        productCategoryList.add(productCategory);
        productCategoryList.add(productCategory2);
        int effectedNum=
                productCategoryDao.batchInsertProductCategory(productCategoryList);
        System.out.println(effectedNum);
    }

    @Test
    public void testCDeleteProductCategory(){
        long shopId=1L;
        List<ProductCategory> productCategoryList=
                productCategoryDao.queryProductCategoryList(1);

        for(ProductCategory pc:productCategoryList){
            if("商品类别1".equals(pc.getProductCategoryName())||"商品类别2".equals(pc.getProductCategoryName()))
            {
                int effectNum =
                        productCategoryDao.deleteProductCategory(pc.getProductCategoryId(), pc.getShopId());
                System.out.println(effectNum);
            }
        }


    }
}