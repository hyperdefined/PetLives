package lol.hyper.petlives;

import lol.hyper.petlives.tools.PetNameHandler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;


import java.util.UUID;

public class Events implements Listener {

    private final PetLives petLives;

    public Events(PetLives petLives) {
        this.petLives = petLives;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        // check only pet mobs
        if (entity instanceof Cat || entity instanceof AbstractHorse || entity instanceof Parrot || entity instanceof Wolf) {
            // check to see if it's actually tamed
            if (!((Tameable) entity).isTamed()) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            UUID petUUID = entity.getUniqueId();
            Tameable tameable = (Tameable) entity;
            UUID owner = tameable.getOwner().getUniqueId();
            // check if the pet is going to die
            if (livingEntity.getHealth() - event.getFinalDamage() <= 0) {
                long currentLives = petLives.petFileHandler.getPetLives(owner, petUUID);
                // see if the pet has any lives left
                if (currentLives != 0) {
                    // since the pet has a life left, cancel the damage and heal it back up
                    // also remove one life
                    event.setCancelled(true);
                    livingEntity.setHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    petLives.petFileHandler.updatePetLives(owner, petUUID, currentLives - 1);
                    livingEntity.playEffect(EntityEffect.TOTEM_RESURRECT);
                    Player player = Bukkit.getPlayer(owner);
                    if (player != null) {
                        player.sendMessage(ChatColor.GREEN + PetNameHandler.getPetName(tameable) + " has lost a life! They now have " + (currentLives - 1) + ".");
                    }
                } else {
                    // pet is going to die :(
                    // remove it from the player file
                    petLives.petFileHandler.removePet(owner, petUUID);
                    petLives.petFileHandler.exportPet(owner, tameable);
                }
            }
        }
    }

    @EventHandler
    public void onPetTame(EntityTameEvent event) {
        UUID owner = event.getOwner().getUniqueId();
        UUID petUUID = event.getEntity().getUniqueId();
        petLives.petFileHandler.addNewPet(owner, petUUID);
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (petLives.commandPet.playerisCheckingMob.contains(player)) {
            petLives.commandPet.playerisCheckingMob.remove(player);
            player.sendMessage(ChatColor.GREEN + "UUID of this mob is: " + entity.getUniqueId() + ".");
            event.setCancelled(true);
        }
        if (petLives.petFileHandler.checkIfPlayerOwnsPet(player.getUniqueId(), entity.getUniqueId())) {
            long currentLives = petLives.petFileHandler.getPetLives(player.getUniqueId(), entity.getUniqueId());
            Tameable tameable = (Tameable) entity;
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.GREEN + PetNameHandler.getPetName(tameable) + " has " + currentLives + " lives!");
            }
            if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack.getType() == petLives.livesItem) {
                    event.setCancelled(true);
                    if (currentLives + 1 > petLives.config.getInt("max-pet-lives")) {
                        player.sendMessage(ChatColor.RED + "The maximum amount of lives is " + petLives.config.getInt("max-pet-lives") + ".");
                    } else {
                        petLives.petFileHandler.updatePetLives(player.getUniqueId(), entity.getUniqueId(), currentLives + 1);
                        player.sendMessage(ChatColor.GREEN + PetNameHandler.getPetName(tameable) + " now has " + (currentLives + 1) + " lives!");
                        tameable.playEffect(EntityEffect.LOVE_HEARTS);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity e : chunk.getEntities()) {
            if (e instanceof Tameable) {
                boolean isPetAlreadySaved = petLives.petFileHandler.isPetInStorage(e.getUniqueId());
                if (!isPetAlreadySaved) {
                    Tameable tameable = (Tameable) e;
                    if (tameable.getOwner() != null) {
                        petLives.petFileHandler.addNewPet(tameable.getOwner().getUniqueId(), e.getUniqueId());
                    }
                }
            }
        }
    }
}
