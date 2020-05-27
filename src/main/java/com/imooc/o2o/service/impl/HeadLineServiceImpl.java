package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.HeadLineDao;
import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.service.HeadLineSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class HeadLineServiceImpl implements HeadLineSevice {
    @Autowired
    private HeadLineDao headLineDao;

    public List<HeadLine> getHeadLine(HeadLine headLindCondition)throws IOException{
        return headLineDao.queryHeadLine(headLindCondition);
    }

}
