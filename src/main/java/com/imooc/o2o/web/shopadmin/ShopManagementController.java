package com.imooc.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/getshopmanagementinfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopManagementInfo(HttpServletRequest request){
        //管理session相关的操作
        Map<String,Object> modelMap = new HashMap<String,Object>();
        //获取店铺id
        long shopId = HttpServletRequestUtil.getLong(request,"shopId");
        if(shopId<=0){
            //如果前端没有传id的话就从seesion中获取
            Object currentShopObj = request.getSession().getAttribute(
                    "currentShop");
            if(currentShopObj==null){
                //重定向 没登录就直接到/shoplist店铺列表中
                modelMap.put("redirect",true);
                modelMap.put("url","/shopadmin/shoplist");
            }else {
                Shop currentShop= (Shop)currentShopObj;
                modelMap.put("redirect",false);
                modelMap.put("shopId",currentShop.getShopId());
            }
        }else {
            Shop currentShop = new Shop();
            currentShop.setShopId(shopId);
            request.getSession().setAttribute("currentShop",currentShop);
            modelMap.put("redirect",false);
        }
        return modelMap;
    }
    @RequestMapping(value = "/getshoplist",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object>getShopList(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String,Object>();
        //获取用户信息
        PersonInfo user = new PersonInfo();
        user.setUserId(1L);
        user.setName("test");
        request.getSession().setAttribute("user",user);
         user =(PersonInfo)request.getSession().getAttribute("user");

         try{
             //获取用户店铺列表
             Shop shopCondition = new Shop();
             shopCondition.setOwner(user);
             ShopExecution se = shopService.getShopList(shopCondition,0,
                     100);
             modelMap.put("shopList",se.getShopList());
             modelMap.put("user",user);
             modelMap.put("success",true);
         }catch (Exception e){
             modelMap.put("success",false);
             modelMap.put("errMsg",e.getMessage());
         }
         return modelMap;
    }

    @RequestMapping(value = "/getshopbyid",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopById(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String,Object>();
        Long shopId = HttpServletRequestUtil.getLong(request,"shopId");
        if(shopId>-1){
            try {
                //获取店铺信息
                Shop shop = shopService.getByShopId(shopId);
                //获取区域列表
                List<Area> areaList = areaService.getAreaList();
                modelMap.put("shop",shop);
                modelMap.put("areaList",areaList);
                modelMap.put("success",true);
            } catch (Exception e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getshopinitinfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopInitInfo(){
        Map<String,Object> modelMap = new HashMap<String,Object>();
        List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
        List<Area> areaList = new ArrayList<Area>();
        try{
            shopCategoryList=
                    shopCategoryService.getShopCategoryList(new ShopCategory());
            areaList=areaService.getAreaList();
            modelMap.put("shopCategoryList",shopCategoryList);
            modelMap.put("areaList",areaList);
            modelMap.put("success",true);
        } catch (IOException e) {
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //验证码
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        //1.接受并转化相关的参数，包括店铺信息以及图片信息
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = null;
        try {
            shop = mapper.readValue(shopStr, Shop.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        //接受图片信息
        CommonsMultipartFile shopImg =null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if(commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest =
                    (MultipartHttpServletRequest)request;
            shopImg =
                    (CommonsMultipartFile)multipartHttpServletRequest.getFile("shopImg");

        }
        //2.修改店铺信息
        if(shop!=null&&shop.getShopId()!=null){
            ShopExecution se = null;
            try {
                ImageHolder imageHolder =
                        new ImageHolder(shopImg.getOriginalFilename(),
                                shopImg.getInputStream());
                if(shopImg==null){
                    se = shopService.modifyShop(shop,null);
                }else {
                    se = shopService.modifyShop(shop, imageHolder);
                }

                if(se.getState()== ShopStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",se.getStateInfo());
                }
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
                e.printStackTrace();
            }

            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺id");
            return modelMap;
        }

    }

    @RequestMapping(value = "/registershop", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> listShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //验证码
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        //1.接受并转化相关的参数，包括店铺信息以及图片信息
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = null;
        try {
            shop = mapper.readValue(shopStr, Shop.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        //接受图片信息
        CommonsMultipartFile shopImg =null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if(commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest =
                    (MultipartHttpServletRequest)request;
            shopImg =
                    (CommonsMultipartFile)multipartHttpServletRequest.getFile("shopImg");

        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","上传图片不能为空");
            return modelMap;
        }
        //2.注册店铺
        if(shop!=null&&shopImg!=null){
            PersonInfo owner = (PersonInfo)request.getSession().getAttribute(
                    "user");
            shop.setOwner(owner);
            ShopExecution se = null;
            try {
                ImageHolder imageHolder =
                        new ImageHolder(shopImg.getOriginalFilename(),
                                shopImg.getInputStream());
                se = shopService.addShop(shop,
                        imageHolder);
                if(se.getState()== ShopStateEnum.CHECK.getState()){
                    modelMap.put("success",true);
                    //该用户可以操作的店铺列表
                    List<Shop> shopList =
                            (List<Shop>)request.getSession().getAttribute(
                                    "shopList");
                    if(shopList==null||shopList.size()==0){
                        shopList =new ArrayList<Shop>();
                    }
                    shopList.add(se.getShop());
                    request.getSession().setAttribute("shopList",shopList);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",se.getStateInfo());
                }
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
                e.printStackTrace();
            }

            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺信息");
            return modelMap;
        }

    }

}
