package test.wx;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;

import java.util.Objects;
import java.util.Optional;

public class WxMpApiImpl implements WxMpApi{
    private WxMpService wxMpService;
    private WxMpConfig wxMpConfig;
    public WxMpApiImpl(WxMpService wxMpService){
        this.wxMpService = Objects.requireNonNull(wxMpService);
    }
    @Override
    public void onConfigUpdate(WxMpConfig newer) {
        this.wxMpConfig = Objects.requireNonNull(newer);
        Objects.requireNonNull(wxMpService).setWxMpConfigStorage(wxMpConfig.get());
    }
    @Override
    public String buildAuthorizationUrl(String _302Url, String scope, String state) {
        return wxMpService.getOAuth2Service().buildAuthorizationUrl(_302Url,scope,state);
    }

    @Override
    public WxUserInfo getUserInfo(WxToken wxToken) {
        return new WxUserInfo() {
            private WxOAuth2UserInfo info;
            @Override
            public WxOAuth2UserInfo get() {
                return Optional.ofNullable(info).orElseGet(()->{
                    try {
                        return info = wxMpService.getOAuth2Service()
                                .getUserInfo(Objects.requireNonNull(wxToken.get()), wxToken.lang());
                    } catch (WxErrorException e) {
                        throw new WxMpApi.WxMpException(e);
                    }
                });
            }
        };
    }
}
