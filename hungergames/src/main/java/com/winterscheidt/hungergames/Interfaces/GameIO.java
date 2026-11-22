package com.winterscheidt.hungergames.Interfaces;

import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Game.GameAction;
import com.winterscheidt.hungergames.Classes.Game.Location;

public interface GameIO {
    void broadcast(String message);
    void broadcast(String message, Location location);
    void displayMenu(Entity entity, List<GameAction> actions);
}
