package com.example.zuuldemo.filter;

import com.example.zuuldemo.util.AESUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理请求参数filter
 *
 * @author :liuqi
 * @date :2018-08-29 14:11.
 */
@Component
public class SignFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(SignFilter.class);

    /**
     * pre：路由之前
     * routing：路由之时
     * post： 路由之后
     * error：发送错误调用
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * filterOrder：过滤的顺序
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * run：过滤器的具体逻辑。
     * 要把请求参数进行验签（解密）之后传给后续的微服务，首先获取到request，但是在request中只有getParameter()而没有setParameter()方法
     * 所以直接修改url参数不可行，另外在reqeust中虽然可以使用setAttribute(),但是可能由于作用域（request）的不同，一台服务器中才能getAttribute
     * 在这里设置的attribute在后续的微服务中是获取不到的，因此必须考虑另外的方式：即获取请求的输入流，并重写，即重写json参数，
     * ctx.setRequest(new HttpServletRequestWrapper(request) {})，这种方式可重新构造上下文中的request
     *
     * @return
     */
    @Override
    public Object run() {

        // 获取到request
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        // 获取请求参数name
        String name = "";
        try {

            // 请求方法
            String method = request.getMethod();
            log.info(String.format("%s >>> %s", method, request.getRequestURL().toString()));
            // 获取请求的输入流
            InputStream in = request.getInputStream();
            String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
            // 如果body为空初始化为空json
            if (StringUtils.isBlank(body)) {
                body = "{}";
            }
            log.info("body" + body);
            // 转化成json
            JSONObject json = JSONObject.fromObject(body);

            // get方法和post、put方法处理方式不同
            if ("GET".equals(method)) {

                // 获取请求参数name
                name = request.getParameter("name");

                if (name != null) {
                    // 关键步骤，一定要get一下,下面才能取到值requestQueryParams
                    request.getParameterMap();
                    Map<String, List<String>> requestQueryParams = ctx.getRequestQueryParams();
                    if (requestQueryParams == null) {
                        requestQueryParams = new HashMap<>();
                    }
                    List<String> arrayList = new ArrayList<>();
                    String key = "key";
                    String aes_decodedStr = AESUtil.getInstance().decode(name, key);
                    arrayList.add(aes_decodedStr + "");
                    requestQueryParams.put("decodename", arrayList);
                    ctx.setRequestQueryParams(requestQueryParams);
                }
            }// post和put需重写HttpServletRequestWrapper
            else if ("POST".equals(method) || "PUT".equals(method)) {

                // 获取请求参数name
                name = json.getString("name");

                if (name != null) {

                    String key = "key";
//                String aes_encodedStr = AESUtil.getInstance().encode(name, key);
//                log.info("加密：" + aes_encodedStr);
//                json.put("decodename", aes_decodedStr);
                    String aes_decodedStr = AESUtil.getInstance().decode(name, key);
                    log.info("解密：" + aes_decodedStr);

                    // 把解密之后的参数放到json里
                    json.put("decodename", aes_decodedStr);
                    String newBody = json.toString();
                    log.info("newBody" + newBody);
                    final byte[] reqBodyBytes = newBody.getBytes();

                    // 重写上下文的HttpServletRequestWrapper
                    ctx.setRequest(new HttpServletRequestWrapper(request) {
                        @Override
                        public ServletInputStream getInputStream() throws IOException {
                            return new ServletInputStreamWrapper(reqBodyBytes);
                        }

                        @Override
                        public int getContentLength() {
                            return reqBodyBytes.length;
                        }

                        @Override
                        public long getContentLengthLong() {
                            return reqBodyBytes.length;
                        }
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
