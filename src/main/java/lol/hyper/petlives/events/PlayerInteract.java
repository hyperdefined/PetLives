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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    private final PetLives petLives;
    private final BukkitAudiences audiences;

    public PlayerInteract(PetLives petLives) {
        this.petLives = petLives;
        this.audiences = petLives.getAdventure();
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        // check if player is checking the uuid from command
        if (petLives.commandPet.playerisCheckingMob.contains(player)) {
            petLives.commandPet.playerisCheckingMob.remove(player);
            ClickEvent copyUUID = ClickEvent.copyToClipboard(entity.getUniqueId().toString());
            Component component = Component.text("UUID of this mob is: " + entity.getUniqueId() + ". Click to copy.").color(NamedTextColor.GREEN).clickEvent(copyUUID);
            audiences.sender(player).sendMessage(component);
            event.setCancelled(true);
        }
        // check if the mob is owned by player
        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            if (tameable.isTamed() && tameable.getOwner() != null) {
                int lives = petLives.petFileHandler.getLives(entity);
                ItemStack itemHeld = player.getInventory().getItemInMainHand();
                // see if they are checking how many lives pet has left
                if (player.isSneaking() && itemHeld.getType() == Material.AIR) {
                    event.setCancelled(true);
                    audiences.sender(player).sendMessage(Component.text(PetNameHandler.getPetName(tameable) + " has " + lives + " lives!").color(NamedTextColor.GREEN));
                }
                if (itemHeld.getType() != Material.AIR) {
                    if (itemHeld.getType() == petLives.livesItem) {
                        event.setCancelled(true);
                        if (lives + 1 > petLives.config.getInt("max-pet-lives")) {
                            audiences.sender(player).sendMessage(Component.text("The maximum amount of lives is "
                                    + petLives.config.getInt("max-pet-lives") + ".").color(NamedTextColor.RED));
                        } else {
                            petLives.petFileHandler.updateLives(entity, lives + 1);
                            audiences.sender(player).sendMessage(Component.text(PetNameHandler.getPetName(tameable) + " now has "
                                    + (lives + 1) + " lives!").color(NamedTextColor.GREEN));
                            tameable.playEffect(EntityEffect.LOVE_HEARTS);
                            int index = player.getInventory().getHeldItemSlot();
                            itemHeld.setAmount(itemHeld.getAmount() - 1);
                            player.getInventory().setItem(index, itemHeld);
                        }
                    }
                }
            }
        }
    }
}
