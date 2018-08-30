package com.example.zuulclientdemo;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 接收经过zuul处理（解密）的参数，并返回
 *
 * @author :liuqi
 * @date :2018-08-29 12:12.
 */
@RestController
public class HiController {

    /**
     * get方式
     * @RequestParam注解方式
     *
     * @param decodename
     * @return
     */
    @GetMapping("/hi")
    public String getName(@RequestParam("decodename") String decodename){
        return decodename;
    }

    /**
     * post方式
     * @RequestBody注解方式获取
     *
     * @param param
     * @return
     */
    @PostMapping("/hello")
    public String postName(@RequestBody Map<String,String> param){
        String name = param.get("decodename");
        return name;
    }

    /**
     * post方式
     * 获取请求的输入流，并转化成json
     *
     * @param request
     * @return
     */
    @PostMapping("/hello1")
    public String postName1(HttpServletRequest request){
        String name = "";
        try {
            InputStream in = request.getInputStream();
            String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
            if(StringUtils.isNotBlank(body)){
                JSONObject jsonObject = JSONObject.fromObject(body);
                name = (String)jsonObject.get("decodename");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }


    /**
     * post方式
     * @RequestBody注解方式获取
     *
     * @param param
     * @return
     */
    @PutMapping("/howareyou")
    public String putName(@RequestBody Map<String,String> param){
        String name = param.get("decodename");
        return name;
    }
}
