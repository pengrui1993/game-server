package org.games.logic.wolf.util;

import org.games.logic.wolf.WolfKilling;
import org.games.logic.wolf.role.impl.Wolf;


public class WolfBombUtil {

    public static boolean handle(WolfKilling ctx, String bomb){
        if(!ctx.aliveWolf().contains(bomb))return false;
        if(!ctx.get(bomb).castTo(Wolf.class).goDied())
            ctx.cur().out.println("warning:already died about:"+bomb);
        ctx.changeState(ctx.newWolfPhaser());
        ctx.deadInfo.addDiedInfoByWolfBomb(bomb,ctx.day());
        return true;
    }
}
