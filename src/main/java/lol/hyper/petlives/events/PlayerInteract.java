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
import org.bukkit.ChatColor;
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

    public PlayerInteract(PetLives petLives) {
        this.petLives = petLives;
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        // check if player is checking the uuid from command
        if (petLives.commandPet.playerisCheckingMob.contains(player)) {
            petLives.commandPet.playerisCheckingMob.remove(player);
            player.sendMessage(ChatColor.GREEN + "UUID of this mob is: " + entity.getUniqueId() + ".");
            event.setCancelled(true);
        }
        // check if the mob is owned by player
        if (petLives.petFileHandler.checkIfPlayerOwnsPet(player.getUniqueId(), entity.getUniqueId())) {
            long currentLives = petLives.petFileHandler.getPetLives(player.getUniqueId(), entity.getUniqueId());
            Tameable tameable = (Tameable) entity;
            ItemStack itemHeld = player.getInventory().getItemInMainHand();
            // see if they are checking how many lives pet has left
            if (player.isSneaking() && itemHeld.getType() == Material.AIR) {
                event.setCancelled(true);
                player.sendMessage(
                        ChatColor.GREEN + PetNameHandler.getPetName(tameable) + " has " + currentLives + " lives!");
            }
            if (itemHeld.getType() != Material.AIR) {
                if (itemHeld.getType() == petLives.livesItem) {
                    event.setCancelled(true);
                    if (currentLives + 1 > petLives.config.getInt("max-pet-lives")) {
                        player.sendMessage(ChatColor.RED + "The maximum amount of lives is "
                                + petLives.config.getInt("max-pet-lives") + ".");
                    } else {
                        petLives.petFileHandler.updatePetLives(
                                player.getUniqueId(), entity.getUniqueId(), currentLives + 1);
                        player.sendMessage(ChatColor.GREEN + PetNameHandler.getPetName(tameable) + " now has "
                                + (currentLives + 1) + " lives!");
                        tameable.playEffect(EntityEffect.LOVE_HEARTS);
                        int index = player.getInventory().getHeldItemSlot();
                        player.getInventory().setItem(index, new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }
}
