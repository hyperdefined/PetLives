/*
 * This file is part of PetLives.
 *
 * PetLives is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PetLives is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PetLives.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.petlives.events;

import lol.hyper.petlives.PetLives;
import org.bukkit.Bukkit;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class EntityTame implements Listener {

    private final PetLives petLives;

    public EntityTame(PetLives petLives) {
        this.petLives = petLives;
    }

    @EventHandler
    public void onPetTame(EntityTameEvent event) {
        Tameable tameable = (Tameable) event.getEntity();
        Bukkit.getScheduler().runTaskLater(petLives, () -> petLives.petFileHandler.addLivesTag(tameable, tameable.getOwner().getUniqueId()), 5);
    }
}
