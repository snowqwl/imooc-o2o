package com.imooc.o2o.service.impl;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.service.AreaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class AreaServiceImplTest extends BaseTest {
    @Autowired
    private AreaService areaService;

    @Test
    public void getAreaList() {

        List<Area> areaList =areaService.getAreaList();
        assertEquals("广州",areaList.get(0).getAreaName());
    }
}