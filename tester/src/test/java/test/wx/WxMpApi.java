package test.wx;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;

public interface WxMpApi{
    class WxMpException extends RuntimeException{
        public final int type;
        public WxMpException(Throwable t){
            super("",t);
            type = 0;
        }
    }
    interface WxMpConfig{
        WxMpConfigStorage get();
    }
    interface WxToken{
        WxOAuth2AccessToken get();
        String lang();
    }
    interface WxUserInfo{
        WxOAuth2UserInfo get();
    }
    void onConfigUpdate(WxMpConfig newer);
    String buildAuthorizationUrl(String _302Url,String scope,String state);
    WxUserInfo getUserInfo(WxToken wxToken);
}
