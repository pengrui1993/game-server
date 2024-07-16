package org.wolf.role;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public interface Role {
    Roles role();
    boolean alive();
    default <T extends Role> T castTo(Class<T> clazz){
        if(clazz.isAssignableFrom(this.getClass()))return clazz.cast(this);
        return null;
    }
    default <T extends Role> void ifIsThen(Class<T> clazz, Consumer<T> c){
        if(Objects.isNull(clazz))return;
        if(Objects.isNull(c))return;
        Optional.ofNullable(castTo(clazz)).ifPresent(c);
    }
}
