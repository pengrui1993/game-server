package org.games.gate.core;

import org.games.gate.core.iface.AuthApiPassBus;
import org.games.gate.core.iface.ConfigApiPassBus;
import org.games.gate.core.iface.LogicsApiPassBus;
import org.games.gate.core.iface.UsersApiPassBus;

public interface Bus extends
        AuthApiPassBus
        , ConfigApiPassBus
        , LogicsApiPassBus
        , UsersApiPassBus
{
}
