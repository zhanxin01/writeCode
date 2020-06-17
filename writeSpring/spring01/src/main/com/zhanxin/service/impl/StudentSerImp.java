package com.zhanxin.service.impl;

import com.zhanxin.annotation.Service;
import com.zhanxin.service.StudentSer;
@Service
public class StudentSerImp implements StudentSer {
    @Override
    public String getName(String id) {
        System.out.println("id:"+id);
        return "张三";
    }
}
