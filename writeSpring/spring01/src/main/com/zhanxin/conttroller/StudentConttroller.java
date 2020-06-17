package com.zhanxin.conttroller;

import com.zhanxin.annotation.Autowried;
import com.zhanxin.annotation.Conttroller;
import com.zhanxin.annotation.RequestMapping;
import com.zhanxin.service.StudentSer;
@Conttroller
public class StudentConttroller {

    @Autowried
    private StudentSer studentSer;

    @RequestMapping("/getName")
    public String getName(String id){
        String name = studentSer.getName(id);
        return name;
    }

}
