package org.games.logic.wolf.util;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.role.Role;
import org.games.logic.wolf.role.Roles;

import java.util.*;

public class NotifyUtil {
    static class Item{
        Major from,to;
        Set<Roles> role;
        static Item n(Major from, Major to, Roles... roles){
            Item i = new Item();
            i.from = from;
            i.to = to;
            i.role = Set.of(roles);
            return i;
        }
    }
    static Map<Major, Map<Major,Set<Roles>>> map = new HashMap<>();
    static{
        final Roles[] all = { Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER};
        List<Item> n = List.of(//TODO
                Item.n(Major.PREPARING, Major.WOLF, all)
                ,Item.n(Major.WITCH, Major.PREDICTOR, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.PREDICTOR, Major.RACE, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.RACE, Major.WOLF, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.RACE, Major.CALC_DIED, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.CALC_DIED, Major.DIED_INFO, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.CALC_DIED, Major.TALKING, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.CALC_DIED, Major.OVER, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.DIED_INFO, Major.ORDERING, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.DIED_INFO, Major.LAST_WORDS, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.LAST_WORDS, Major.HUNTER, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.HUNTER, Major.LAST_WORDS, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.ORDERING, Major.TALKING, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.TALKING, Major.VOTING, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.TALKING, Major.WOLF, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
                ,Item.n(Major.VOTING, Major.CALC_DIED, Roles.WOLF, Roles.PREDICTOR,Roles.WITCH,Roles.HUNTER,Roles.PROTECTOR,Roles.FARMER)
        );
        n.forEach(i-> map.computeIfAbsent(i.from, k -> new HashMap<>()).computeIfAbsent(i.to, k -> Set.copyOf(i.role)));
        n.forEach(i-> map.forEach((key, value) -> value.forEach((k, v)-> value.replace(k,Collections.unmodifiableSet(v)))));
    }
    public static boolean debug = true;

    public static void main(String[] args) {
        Set<Roles> roles = map.get(Major.ORDERING).get(Major.TALKING);
        System.out.println(roles.getClass());
    }
    public static boolean notify(Major from, Major to, Role role){
        if(debug)return true;
        return Optional.ofNullable(map.get(from))
                .map(a->a.get(to))
                .orElse(Collections.emptySet())
                .contains(role.role());
    }
}
