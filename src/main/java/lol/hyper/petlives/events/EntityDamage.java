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
import lol.hyper.petlives.tools.PetNameHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class EntityDamage implements Listener {

    private final PetLives petLives;
    private final BukkitAudiences audiences;

    public EntityDamage(PetLives petLives) {
        this.petLives = petLives;
        this.audiences = petLives.getAdventure();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        // check only pet mobs
        if (entity instanceof Tameable) {
            // check to see if it's actually tamed
            if (!((Tameable) entity).isTamed()) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            Tameable tameable = (Tameable) entity;
            UUID owner = tameable.getOwner().getUniqueId();
            // check if the pet is going to die
            if (livingEntity.getHealth() - event.getFinalDamage() <= 0) {
                int currentLives = petLives.petFileHandler.getLives(entity);
                // see if the pet has any lives left
                if (currentLives != 0) {
                    // since the pet has a life left, cancel the damage and heal it back up
                    // also remove one life
                    event.setCancelled(true);
                    livingEntity.setHealth(livingEntity
                            .getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .getValue());
                    petLives.petFileHandler.updateLives(entity, currentLives - 1);
                    livingEntity.playEffect(EntityEffect.TOTEM_RESURRECT);
                    Player player = Bukkit.getPlayer(owner);
                    if (player != null) {
                        audiences.sender(player).sendMessage(Component.text(PetNameHandler.getPetName(tameable)
                                + " has lost a life! They now have " + (currentLives - 1) + ".").color(NamedTextColor.RED));
                    }
                } else {
                    // pet is going to die :(
                    petLives.petFileHandler.exportPet(tameable);
                }
            }
        }
    }
}
