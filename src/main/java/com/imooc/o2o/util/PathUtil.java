package com.imooc.o2o.util;

public class PathUtil {
    /**
     * 根据不同的操作系统，设置存储图片文件不同的根目录
     */
    private static String separator =System.getProperty("file.separator");

    public static String getImgBasePath(){
        String os = System.getProperty("os.name");
        String basePath ="";
        if(os.toLowerCase().startsWith("win")){
            basePath="E:\\图片\\images";
        }else {
            basePath="/home/o2o/image";
        }
        basePath = basePath.replace("/",separator);
        return basePath;
    }

    //根据不同的业务需求返回不同的子路径
    public static String getShopImagePath(long shopId){
        String imagePath = "/upkoad/item/shop/"+shopId+"/";
        return imagePath.replace("/",separator);
    }
}
