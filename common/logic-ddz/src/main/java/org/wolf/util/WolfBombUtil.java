package org.wolf.util;

import org.wolf.WolfKilling;
import org.wolf.role.impl.Wolf;


public class WolfBombUtil {

    public static void handle(WolfKilling ctx, String bomb){
        if(!ctx.aliveWolf().contains(bomb))return;
        ctx.get(bomb).castTo(Wolf.class).lived=false;
        ctx.changeState(ctx.newWolfPhaser());
    }
}
