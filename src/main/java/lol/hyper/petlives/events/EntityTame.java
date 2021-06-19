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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.UUID;

public class EntityTame implements Listener {

    private final PetLives petLives;

    public EntityTame(PetLives petLives) {
        this.petLives = petLives;
    }

    @EventHandler
    public void onPetTame(EntityTameEvent event) {
        // add new pet to player's file when they tame it
        UUID owner = event.getOwner().getUniqueId();
        UUID petUUID = event.getEntity().getUniqueId();
        petLives.petFileHandler.addNewPet(owner, petUUID);
    }
}
