package com.v5.test.worker.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.v5.base.event.EventPublisher;
import com.v5.base.utils.AsyncInvokeExceptoin;
import com.v5.base.utils.SimpleCallback;
import com.v5.test.worker.bean.TaskSnapshort;
import com.v5.test.worker.bean.User;
import com.v5.test.worker.constant.EventPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created by piguangtao on 14-2-18.
 */
public class HttpService implements InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    private String httpUrl = "http://192.168.1.232:9101";

    @Autowired
    private EventPublisher eventPublisher;

    private AsyncHttpClient asyncHttpClient;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        asyncHttpClient = new AsyncHttpClient();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    public void bindDevice(final String mobile, boolean isAuthcode,final SimpleCallback<User,AsyncInvokeExceptoin> callback) throws IOException {

        AsyncHttpClient.BoundRequestBuilder requestBuilder = asyncHttpClient.preparePost(httpUrl + "/api/user/bind/device");
        requestBuilder.addParameter("mobile", mobile).addParameter("device_type", "2")
                .addParameter("app_id", "0").addHeader("client-version", "faceshow-1.0").addHeader("api-version", "1.0");
        if (isAuthcode) {
            requestBuilder.addParameter("authcode", "8888");
        }

        requestBuilder.execute(new AsyncCompletionHandler<Object>() {

            @Override
            public Object onCompleted(Response response) throws Exception {
                if (200 != response.getStatusCode()) {
                    LOGGER.error("mobile:{} fails to http login.", mobile);
                } else {
                    String responseBody ;
                    try {
                        responseBody = response.getResponseBody();
                        User user1;
                        try {
                            user1 = objectMapper.readValue(responseBody, User.class);
                            userService.saveSession(user1.getSessionId(),user1.getId());
                            eventPublisher.send(EventPath.USER_HTTP_LOGIN_SUCCESS, user1);
                            if(null != callback){
                               callback.success(user1);
                            }
                        } catch (Exception e) {
                            if(null != callback){
                                callback.failure(new AsyncInvokeExceptoin(e.getMessage(),e));
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("mobile:{} response body error.", mobile, e);
                        callback.failure(new AsyncInvokeExceptoin(e.getMessage(),e));
                    }
                }
                return null;
            }
        });
    }

    public void login(final String name, final String password, final SimpleCallback<User, AsyncInvokeExceptoin> callback) throws IOException {

        TaskSnapshort.getInstance().getHttpLoginUserNum().getAndIncrement();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpUrl).append("/api/user/login")
                .append("?name=")
                .append(name).append("&password=").append(password);


        asyncHttpClient.prepareGet(stringBuilder.toString()).execute(new AsyncCompletionHandler<Object>() {
            @Override
            public Object onCompleted(Response response) {
                if (200 != response.getStatusCode()) {
                    LOGGER.error("userName:{} fails to http login.", name);
                } else {
                    String responseBody = null;
                    try {
                        responseBody = response.getResponseBody();
                        Gson gson = new Gson();
                        User user = gson.fromJson(responseBody, User.class);
                        eventPublisher.send(EventPath.USER_HTTP_LOGIN_SUCCESS, user);
                        if (null != callback) {
                            callback.success(user);
                        }
                    } catch (IOException e) {
                        LOGGER.error("userName:{} response body error.", name, e);
                    }
                }
                return null;
            }
        });
    }


}
