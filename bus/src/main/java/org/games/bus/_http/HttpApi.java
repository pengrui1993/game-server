package org.games.bus._http;

import org.games.constant.Const;
import org.games.constant.SystemRoleType;

interface C{
    boolean READ = false;
    boolean WRITE = true;
}
public enum HttpApi {
    AUTH_HELLO(Const.AUTH_HTTP_HELLO,SystemRoleType.AUTH,C.READ)
    ;
    public final String url;
    public final SystemRoleType role;
    public final boolean rw;
    HttpApi(String url,SystemRoleType role,boolean rw){
        this.url = url;
        this.role = role;
        this.rw=rw;
    }
}
