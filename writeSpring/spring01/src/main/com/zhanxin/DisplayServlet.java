package com.zhanxin;

import com.zhanxin.annotation.Autowried;
import com.zhanxin.annotation.Conttroller;
import com.zhanxin.annotation.Service;
import com.zhanxin.conttroller.StudentConttroller;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DisplayServlet extends HttpServlet {

    private Properties properties = new Properties();


    private Map<String, Object>  beanMap = new ConcurrentHashMap();

    private List<String> classNames = new ArrayList();


    @Override
    public void init(ServletConfig config) throws ServletException {
        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //加载
        doScanner(properties.getProperty("scanner"));

        //注册
        doInstance();

        //注入
        doAutowried();

        StudentConttroller studentConttroller = (StudentConttroller) beanMap.get("studentConttroller");
        String name = studentConttroller.getName("55");
        System.out.println(name);

        //mapper
        initHandMapper();
    }

    private void doLoadConfig(String location){
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));
            properties.load(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void doScanner(String path){
        try {
            URL url = this.getClass().getClassLoader().getResource("/"+path.replace(".", "/"));
            File files = new File(url.getFile());
            for (File file: files.listFiles()) {
                if(file.isDirectory()){
                    doScanner(path + "."+file.getName());
                }else{
                    classNames.add(path + "." + file.getName().replace(".class",""));
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void doInstance(){
        if(classNames.isEmpty()) return;

        try {
            for (String className: classNames) {
                Class clazz = Class.forName(className);
                String beanName = lowFirst(clazz.getSimpleName());

                if(clazz.isAnnotationPresent(Conttroller.class)){
                    beanMap.put(beanName, clazz.newInstance());

                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    if(!service.value().equals("")){
                        beanName = service.value();
                    }
                    Object inst = clazz.newInstance();
                    beanMap.put(beanName, inst);

                    Class[] interfaces = clazz.getInterfaces();
                    for (Class inter: interfaces) {
                        beanMap.put(lowFirst(inter.getSimpleName()), inst);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doAutowried(){
        if(beanMap.isEmpty()) return;

        for (Map.Entry<String, Object> entry : beanMap.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field: fields) {
                if(field.isAnnotationPresent(Autowried.class)){
                    Autowried autowried = field.getAnnotation(Autowried.class);
                    String beanName = autowried.value().trim();
                    if(beanName.equals("")){
                        beanName = field.getName();
                    }
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), beanMap.get(beanName));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    private void initHandMapper(){

    }




    private static String lowFirst(String str){
        char [] chars= str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


}
