package vip.lovek.retrofit;

/**
 * author： yuzhirui@douban.com
 * date: 2022-04-02 10:19
 */
public abstract class ParameterHandler {
    abstract void apply(ServiceMethod serviceMethod, String value);

    static class FieldParameterHandler extends ParameterHandler {
        private String key;

        public FieldParameterHandler(String key) {
            this.key = key;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addFiledParameter(key, value);
        }
    }

    static class QueryParameterHandler extends ParameterHandler {
        private String key;

        public QueryParameterHandler(String key) {
            this.key = key;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addQueryParameter(key, value);
        }
    }
}
