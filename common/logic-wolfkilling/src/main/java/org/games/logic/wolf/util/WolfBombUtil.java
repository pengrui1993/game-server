package org.games.logic.wolf.util;

import org.games.logic.wolf.WolfKilling;
import org.games.logic.wolf.role.impl.Wolf;


public class WolfBombUtil {

    public static boolean handle(WolfKilling ctx, String bomb){
        if(!ctx.aliveWolf().contains(bomb))return false;
        ctx.get(bomb).castTo(Wolf.class).lived=false;
        ctx.changeState(ctx.newWolfPhaser());
        return true;
    }
}
