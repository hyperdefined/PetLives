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
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoad implements Listener {

    private final PetLives petLives;

    public ChunkLoad(PetLives petLives) {
        this.petLives = petLives;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Bukkit.getScheduler().runTaskLater(petLives, () -> {
            for (Entity entity : chunk.getEntities()) {
                // only check tameable mobs
                if (entity instanceof Tameable) {
                    Tameable tameable = (Tameable) entity;
                    if (tameable.isTamed()) {
                        petLives.petFileHandler.addLivesTag(tameable, tameable.getOwner().getUniqueId());
                    }
                }
            }
        }, 20);
    }
}
