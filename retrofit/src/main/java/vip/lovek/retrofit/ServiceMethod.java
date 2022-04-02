package vip.lovek.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import vip.lovek.retrofit.annotation.Field;
import vip.lovek.retrofit.annotation.GET;
import vip.lovek.retrofit.annotation.POST;
import vip.lovek.retrofit.annotation.Query;

/**
 * 解析注解，记录请求类型参数
 * author： yuzhirui@douban.com
 * date: 2022-04-02 09:54
 */
public class ServiceMethod {
    private HttpUrl baseUrl;
    private Call.Factory callFactory;
    private String httpMethod;
    private String relativeUrl;
    private boolean hasBody;
    private ParameterHandler[] parameterHandlers;
    private FormBody.Builder formBodyBuilder;
    private HttpUrl.Builder urlBuilder;

    public ServiceMethod(Builder builder) {
        baseUrl = builder.retrofit.baseUrl;
        callFactory = builder.retrofit.callFactory;
        httpMethod = builder.httpMethod;
        relativeUrl = builder.relativeUrl;
        hasBody = builder.hasBody;
        parameterHandlers = builder.parameterHandlers;

        if (hasBody) {
            formBodyBuilder = new FormBody.Builder();
        }
    }

    public Object invoke(Object[] args) {
        for (int i = 0; i < parameterHandlers.length; i++) {
            ParameterHandler parameterHandler = parameterHandlers[i];
            parameterHandler.apply(this, args[i].toString());
        }

        // 最终的请求地址
        HttpUrl url;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        url = Objects.requireNonNull(urlBuilder).build();

        // 请求体
        FormBody formBody = null;
        if (formBodyBuilder != null) {
            formBody = formBodyBuilder.build();
        }
        Request request = new Request.Builder().url(url).method(httpMethod, formBody).build();
        return callFactory.newCall(request);
    }

    public void addFiledParameter(String key, String value) {
        formBodyBuilder.add(key, value);
    }

    public void addQueryParameter(String key, String value) {
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        Objects.requireNonNull(urlBuilder).addQueryParameter(key, value);
    }

    public static class Builder {
        private Retrofit retrofit;
        private Annotation[] annotations;
        private Annotation[][] parameterAnnotations;
        private String httpMethod;
        private String relativeUrl;
        private boolean hasBody;
        ParameterHandler[] parameterHandlers;

        public Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            // 获取方法上的注解
            annotations = method.getAnnotations();
            // 获取参数注解
            parameterAnnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            // 解析方法注解
            for (Annotation annotation : annotations) {
                if (annotation instanceof POST) {
                    this.httpMethod = "POST";
                    this.relativeUrl = ((POST) annotation).value();
                    this.hasBody = true;
                } else if (annotation instanceof GET) {
                    this.httpMethod = "GET";
                    this.relativeUrl = ((GET) annotation).value();
                    this.hasBody = false;
                }
            }

            // 解析方法参数注解
            int length = parameterAnnotations.length;
            parameterHandlers = new ParameterHandler[length];
            // TODO 检查请求方法参数必须有注解一一对应
            for (int i = 0; i < length; i++) {
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                // parameterAnnotation 参数上的注解，可以多个
                for (Annotation annotation : parameterAnnotation) {
                    if (annotation instanceof Field) {
                        //TODO 检查 Field 只能用于 POST
                        String key = ((Field) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.FieldParameterHandler(key);
                    } else if (annotation instanceof Query) {
                        String key = ((Query) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.QueryParameterHandler(key);
                    }
                }
            }
            return new ServiceMethod(this);
        }
    }
}
