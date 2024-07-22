package test.wx;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.mp.api.impl.WxMpOAuth2ServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Login {
    interface Client{}
    interface User{
        default long id(){return 0;}
        default String openId(){return "";}
        static User create(){ return new User() {};}
    }
    String domainName;
    WxOAuth2Service wxOAuth2Service = new WxMpOAuth2ServiceImpl(new WxMpServiceImpl());
    @Seq(1)
    void loginAndRedirect(Client c,boolean base,String bd,String uname,String pwd){
        String redirectUri =String.format("%s/api/%s?webUrl=%s"
                ,domainName
                ,base?"baseCallback":"infoCallback"
                ,"/bd/case/public")
                ;
        if (Objects.nonNull(bd))redirectUri = redirectUri +"&bdUserId="+bd;
        final String scope = !base? WxConsts.OAuth2Scope.SNSAPI_USERINFO:WxConsts.OAuth2Scope.SNSAPI_BASE;
        String authorizationUrl =
                wxOAuth2Service.buildAuthorizationUrl(redirectUri, scope, "STATE");
    }
    Map<String,Long> openIdUserId = new ConcurrentHashMap<>();
    Map<Long,User> userIdToUser = new ConcurrentHashMap<>();
    Map<String,WxOAuth2UserInfo> openIdToInfo = new ConcurrentHashMap<>();
    Map<String,Long> openIdInfoTime = new ConcurrentHashMap<>();
    void infoCallback(String webUrl,String code, String state) throws WxErrorException {
        callback(false,webUrl,code,state);
    }
    void baseCallback(String webUrl,String code, String state) throws WxErrorException {
        callback(true,webUrl,code,state);
    }
    @Seq(2)
    synchronized void callback(boolean base,String webUrl,String code, String state) throws WxErrorException {
        if(Objects.isNull(code)||code.isEmpty())return;
        if(checkRepeatedCode(code))return;
        long now = now();
        setRepeatedCodeFlag(code);
        WxOAuth2AccessToken token = wxOAuth2Service.getAccessToken(code);
        String openId = token.getOpenId();
        WxOAuth2UserInfo info=openIdToInfo.get(openId);
        if(!base){
            Long limit;
            if(Objects.isNull(limit = openIdInfoTime.get(openId))
                    ||now-limit>10*1000){
                info = wxOAuth2Service.getUserInfo(token, "zh_CN");
                openIdToInfo.put(openId,info);
                openIdInfoTime.put(openId,now);
            }
        }
        Long id = openIdUserId.get(openId);
        User user;
        if(Objects.isNull(id)){
            user = User.create();
            openIdUserId.put(user.openId(),user.id());
            userIdToUser.put(user.id(),user);
        }else{
            user = userIdToUser.get(id);
        }
        boolean hasUserInfo = null!=info;
        String tk = newToken();
        Map<String,String> data = new HashMap<>();
        data.put("hasUserInfo",""+hasUserInfo);
        data.put("token",tk);
        data.put("webUrl",webUrl);
        data.put("uid",user.id()+"");
        StringBuilder builder = new StringBuilder(data.get("webUrl"));
        for (Map.Entry<String, String> e : data.entrySet()) {
            if (!Objects.equals("webUrl",e.getKey())) {
                final char pre = builder.isEmpty()?'?':'&';
                builder.append(pre)
                        .append(e.getKey())
                        .append('=')
                        .append(e.getValue());
            }
        }
        tokenUser.put(tk,user);
        tokenTime.put(tk,now());
        String redirectUrl = builder.toString();
        System.out.println(redirectUrl);
    }
    Map<String,User> tokenUser = new ConcurrentHashMap<>();
    Map<String,Long> tokenTime = new ConcurrentHashMap<>();

    Map<String,Long> usedCode = new ConcurrentHashMap<>();
    @Seq(3)
    void logout(String token){
        if(Objects.isNull(token))return;
        User user = tokenUser.get(token);
        tokenUser.remove(token);
        tokenTime.remove(token);
        //notify logout
    }
    private void setRepeatedCodeFlag(String code){
        usedCode.put(code,now());
    }
    private boolean checkRepeatedCode(String code){
        return usedCode.containsKey(code);
    }
    void update(){
        long last = now();
        for (Map.Entry<String, Long> e : usedCode.entrySet()) {
            if(last - e.getValue()>=30*1000){
                usedCode.remove(e.getKey());
            }
        }
        for (Map.Entry<String, Long> e : tokenTime.entrySet()) {
            if(last-e.getValue()>=30*1000){
                String token = e.getKey();
                User user = tokenUser.get(token);
                tokenTime.remove(token);
                tokenUser.remove(token);
                //notify user's token expired
            }
        }
    }
    long now(){
        return System.currentTimeMillis();
    }
    String newToken(){
        return UUID.randomUUID().toString();
    }

}
