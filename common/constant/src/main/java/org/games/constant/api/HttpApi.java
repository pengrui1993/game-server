package org.games.constant.api;

import org.games.constant.Const;
import org.games.constant.RW;
import org.games.constant.SystemRoleType;

/**
 * component exported http api
 */
public enum HttpApi {
    AUTH_HELLO(Const.AUTH_HTTP_HELLO,SystemRoleType.AUTH,RW.READ)
    ,AUTH_LOGIN(Const.AUTH_HTTP_LOGIN,SystemRoleType.AUTH,RW.WRITE,SystemRoleType.GATE)


    ,BUS_HELLO(Const.BUS_HTTP_HELLO,SystemRoleType.BUS,RW.READ)
    ,BUS_CALL(Const.BUS_HTTP_HELLO,SystemRoleType.BUS,RW.READ
            ,SystemRoleType.ALL_NODE
    )


    ,CONFIG_HELLO(Const.CONFIG_HTTP_HELLO,SystemRoleType.CONFIG,RW.READ
            ,SystemRoleType.ALL_NODE
    )


    ,GATE_HELLO(Const.GATE_HTTP_HELLO,SystemRoleType.CONFIG,RW.READ)


    ,LOGICS_HELLO(Const.LOGICS_HTTP_HELLO,SystemRoleType.LOGICS,RW.READ)
    ,USERS_HELLO(Const.LOGICS_HTTP_HELLO,SystemRoleType.LOGICS,RW.READ)

    ;
    //node's exported url
    public final String url;
    //node's role
    public final SystemRoleType role;
    //call the url if absolutely no change the state then RW.read else then RW.write
    public final RW rw;
    //should be call by those
    public final SystemRoleType[] forRoles;
    HttpApi(String url, SystemRoleType role, RW rw,SystemRoleType... forRoles){
        this.url = url;
        this.role = role;
        this.rw=rw;
        this.forRoles =forRoles;
    }
}
