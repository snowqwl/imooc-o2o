package com.imooc.o2o.service;

import com.imooc.o2o.entity.HeadLine;

import java.io.IOException;
import java.util.List;

public interface HeadLineSevice {
    /**
     * 根据传入的条件返回指定的头条列表
     */
    List<HeadLine> getHeadLine(HeadLine headLineCondition)throws IOException;
}
