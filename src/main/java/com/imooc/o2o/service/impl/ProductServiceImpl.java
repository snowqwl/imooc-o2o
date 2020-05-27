package com.imooc.o2o.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productdao;

    @Autowired
    private ProductImgDao productImgDao;


    @Override
    @Transactional
    //1.处理缩略图
    //2.往tb_product写入商品信息，获取productId
    //3.结合productId批量处理商品详情图
    //4.将商品详情图列表批量插入tb_product_img中
    public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList) throws ProductOperationException {
        //空值判断
        if(product!=null&&product.getShop()!=null&&product.getShop().getShopId()!=null){
            //给商品设置上默认属性
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            //默认为上架状态
            product.setEnableStatus(1);
            //若商品缩略图不为空则添加
            if(thumbnail!=null){
                addThumbnail(product,thumbnail);
            }
            try{
                //创建商品信息
                int effectedNum = productdao.insertProduct(product);
                if(effectedNum<=0){
                    throw new ProductOperationException("创建商品失败");
                }
            }catch (Exception e){
                throw new ProductOperationException("创建商品失败："+e.toString());
            }
            //若商品详情图不为空则添加
            if(productImgList!=null&&productImgList.size()>0){
                addProductImgList(product,productImgList);
            }
            return new ProductExecution(ProductStateEnum.SUCCESS,product);
        }else{
            //传参为空则返回空值错误信息
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    @Override
    public Product getProductById(long productId) {
        return productdao.queryProductByProductId(productId);
    }


    @Override
    @Transactional
    //1.若缩略图参数有值，则处理缩略图
    //若原先存在缩略图，则先删除再添加新图，之后获取缩略图相对路径并赋值给product
    //2.若商品详情图列表参数有值，对商品详情图片列表进行相同操作
    //3.将tb_product_img下面的该商品原先的商品详情图全部删除
    //4.更新tb_product的信息
    public ProductExecution modifyProduct(Product product, ImageHolder thumbnail,
                                          List<ImageHolder> productImgs) throws RuntimeException {
        //空值判断
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            //给商品设置上默认属性
            product.setLastEditTime(new Date());
            //若商品缩略图不为空且原有缩略图不为空则删除原有缩略图并添加
            if (thumbnail != null) {
                //先获取一遍原有信息，因为原来的信息有原图片地址
                Product tempProduct =
                        productdao.queryProductByProductId(product.getProductId());
                if (tempProduct.getImgAddr() != null) {
                    //删除原先的图片
                    ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
                }
                addThumbnail(product, thumbnail);
            }
            //如果有新存入的商品详情图，则将原先的删除，并添加新图片
            if (productImgs != null && productImgs.size() > 0) {
                deleteProductImgs(product.getProductId());
                addProductImgs(product, productImgs);
            }
            try {
                //更新商品信息
                int effectedNum = productdao.updateProduct(product);
                if (effectedNum <= 0) {
                    throw new RuntimeException("更新商品信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS, product);
            } catch (Exception e) {
                throw new RuntimeException("更新商品信息失败:" + e.toString());
            }
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        //页码转换成数据库的行码，并调用dao层取回指定页码的商品列表
        int rowIndex= PageCalculator.calculateRowIndex(pageIndex,pageSize);
        List<Product> productList =
                productdao.queryProductList(productCondition,rowIndex,pageSize);
        //基于相同的查询条件返回该查询条件下的商品总数
        int count = productdao.queryProductCount(productCondition);
        ProductExecution pe = new ProductExecution();
        pe.setProductList(productList);
        pe.setCount(count);
        return pe;
    }

    private void addProductImgs(Product product, List<ImageHolder> productImgs) {
        //获取图片存储路径，这里直接存放到相应店铺的文件夹底下
        String dest =
                PathUtil.getShopImagePath(product.getShop().getShopId());
        List<ProductImg> productImgList = new ArrayList<ProductImg>();
        //遍历图片一次去处理，并添加进productImg实体类里
        for (ImageHolder productImgHolder:productImgs) {
            String imgAddr =ImageUtil.generateNormalImg(productImgHolder,dest);
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());
            productImgList.add(productImg);
        }
        //如果确实是有图片需要添加的，就执行批量添加操作
        if (productImgList.size()>0) {
            try {
                int effectedNum = productImgDao.batchInsertProductImg(productImgList);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品详情图片失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品详情图片失败:" + e.toString());
            }
        }
    }

    private void deleteProductImgs(long productId) {
        //根据productId获取原来的图片
        List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
        //删除原来的图片
        for (ProductImg productImg : productImgList) {
            ImageUtil.deleteFileOrPath(productImg.getImgAddr());
        }
        productImgDao.deleteProductImgByProductId(productId);
    }
    /**
     * 添加缩略图
     */
    public void addThumbnail(Product product,ImageHolder thumbnail){
        String dest= PathUtil.getShopImagePath(product.getShop().getShopId());
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);
        product.setImgAddr(thumbnailAddr);
    }

    /**
     * 批量添加图片
     */
    private void addProductImgList(Product product,
                                   List<ImageHolder> productImgHolderList){
        //获取图片存储路径，这里直接存放到相应店铺的文件夹底下
        String dest= PathUtil.getShopImagePath(product.getShop().getShopId());
        List<ProductImg> productImgList = new ArrayList<ProductImg>();
        //遍历图片一次去处理，并添加进productImg实体类中
        for(ImageHolder productImgHolder:productImgHolderList){
            String imgAddr = ImageUtil.generateNormalImg(productImgHolder,dest);
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());
            productImgList.add(productImg);
        }
        //如果确实是有图片需要添加的，就执行批量添加操作
        if(productImgList.size()>0){
            try {
                int effectedNum =
                        productImgDao.batchInsertProductImg(productImgList);
                if(effectedNum<=0){
                    throw new ProductOperationException("创建商品详情图片失败");
                }
            } catch (ProductOperationException e) {
               throw new ProductOperationException("创建商品详情图片失败"+e.toString());
            }
        }

    }

}
