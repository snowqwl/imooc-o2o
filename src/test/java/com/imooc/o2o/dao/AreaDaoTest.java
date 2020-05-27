package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

import static junit.framework.TestCase.assertEquals;


public class AreaDaoTest  extends BaseTest {
    @Autowired
    private AreaDao areaDao;
    @Test
    public void queryArea() {
        List<Area> list = areaDao.queryArea();
        assertEquals(3,list.size());
    }
}