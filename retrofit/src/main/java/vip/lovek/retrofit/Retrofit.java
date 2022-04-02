package vip.lovek.retrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * author： yuzhirui@douban.com
 * date: 2022-04-02 08:55
 */
public class Retrofit {
    private final ConcurrentHashMap<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    HttpUrl baseUrl;
    Call.Factory callFactory;

    public Retrofit(HttpUrl baseUrl, Call.Factory callFactory) {
        this.baseUrl = baseUrl;
        this.callFactory = callFactory;
    }

    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 解析注解信息，实现网络请求
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.invoke(args);
            }
        });
    }

    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null) return result;
        // 多线程下避免重复解析
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public static final class Builder {
        private HttpUrl baseUrl;
        private Call.Factory callFactory;

        public Builder createFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Retrofit build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }
            Call.Factory callFactory = this.callFactory;
            if (callFactory == null) {
                callFactory = (Call.Factory) new OkHttpClient();
            }
            return new Retrofit(baseUrl, callFactory);
        }
    }
}
