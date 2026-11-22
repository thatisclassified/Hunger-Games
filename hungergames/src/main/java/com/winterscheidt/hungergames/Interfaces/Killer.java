package com.winterscheidt.hungergames.Interfaces;

import com.winterscheidt.hungergames.Classes.Entities.Entity;

public interface Killer {
    // These methods are called BY the victim, passing itself
    // to the killer
    public String getKillMessage(Entity entity);
    public String getKillDescription(Entity entity);
}
