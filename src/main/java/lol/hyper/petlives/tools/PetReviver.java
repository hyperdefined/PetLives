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

package lol.hyper.petlives.tools;

import lol.hyper.petlives.PetLives;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.json.JSONObject;

import java.util.UUID;

public class PetReviver {

    private final PetLives petLives;

    public PetReviver(PetLives petLives) {
        this.petLives = petLives;
    }

    public void respawnPet(Player player, UUID pet, Location locationToSpawn) {
        JSONObject jsonObject = petLives.petFileHandler.getDeadPetsJSON(player.getUniqueId());
        if (jsonObject == null) {
            player.sendMessage(ChatColor.RED + "You don't have any dead pets. This is not supposed to happen.");
            return;
        }
        // get some basic info for the pet
        JSONObject deadPet = jsonObject.getJSONObject(pet.toString());
        String type = deadPet.getString("type");
        String name = deadPet.getString("name");
        if (name.isEmpty()) {
            name = null;
        }
        boolean isAdult = deadPet.getBoolean("isAdult");
        AttributeInstance attributeMovementSpeed;
        AttributeInstance attributeMaxHealth;
        long ageFromJSON = deadPet.getLong("age");
        int age = Math.toIntExact(ageFromJSON);

        // we spawn the pet differently based on the type
        // each mob has it's own custom thing we need to save
        Entity entity = null;
        switch (type) {
            case "HORSE":
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.HORSE);
                Horse newHorse = (Horse) entity;
                newHorse.setAge(age);
                if (name != null) {
                    newHorse.setCustomName(name);
                }
                newHorse.setJumpStrength(deadPet.getDouble("jumpStrength"));

                newHorse.setStyle(Horse.Style.valueOf(deadPet.getString("style")));
                newHorse.setColor(Horse.Color.valueOf(deadPet.getString("color")));

                attributeMovementSpeed = newHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue(deadPet.getDouble("speed"));
                attributeMaxHealth = newHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue(deadPet.getDouble("maxHealth"));

                if (isAdult) {
                    newHorse.setAdult();
                } else {
                    newHorse.setBaby();
                }

                newHorse.setOwner(player);
                break;
            case "WOLF": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.WOLF);
                Wolf newWolf = (Wolf) entity;
                newWolf.setAge(age);
                if (name != null) {
                    newWolf.setCustomName(deadPet.getString("name"));
                }
                newWolf.setCollarColor(DyeColor.valueOf(deadPet.getString("collar")));
                if (isAdult) {
                    newWolf.setAdult();
                } else {
                    newWolf.setBaby();
                }

                newWolf.setOwner(player);
                break;
            }
            case "DONKEY": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.DONKEY);
                Donkey newDonkey = (Donkey) entity;
                newDonkey.setAge(age);
                if (name != null) {
                    newDonkey.setCustomName(name);
                }
                newDonkey.setJumpStrength(deadPet.getDouble("jumpStrength"));

                attributeMovementSpeed = newDonkey.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue(deadPet.getDouble("speed"));
                attributeMaxHealth = newDonkey.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue(deadPet.getDouble("maxHealth"));

                if (isAdult) {
                    newDonkey.setAdult();
                } else {
                    newDonkey.setBaby();
                }

                newDonkey.setOwner(player);
                break;
            }
            case "MULE": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.MULE);
                Mule newMule = (Mule) entity;
                newMule.setAge(age);
                if (name != null) {
                    newMule.setCustomName(name);
                }
                newMule.setJumpStrength(deadPet.getDouble("jumpStrength"));

                attributeMovementSpeed = newMule.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue(deadPet.getDouble("speed"));
                attributeMaxHealth = newMule.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue(deadPet.getDouble("maxHealth"));

                if (isAdult) {
                    newMule.setAdult();
                } else {
                    newMule.setBaby();
                }

                newMule.setOwner(player);
                break;
            }
            case "TRADER_LLAMA": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.TRADER_LLAMA);
                TraderLlama newLlama = (TraderLlama) entity;
                newLlama.setAge(age);
                if (name != null) {
                    newLlama.setCustomName(name);
                }
                newLlama.setJumpStrength(deadPet.getDouble("jumpStrength"));

                newLlama.setColor(Llama.Color.valueOf(deadPet.getString("color")));

                attributeMovementSpeed = newLlama.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue(deadPet.getDouble("speed"));
                attributeMaxHealth = newLlama.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue(deadPet.getDouble("maxHealth"));

                if (isAdult) {
                    newLlama.setAdult();
                } else {
                    newLlama.setBaby();
                }

                newLlama.setOwner(player);
                break;
            }
            case "LLAMA": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.LLAMA);
                Llama newLlama = (Llama) entity;
                newLlama.setAge(age);
                if (name != null) {
                    newLlama.setCustomName(name);
                }
                newLlama.setJumpStrength(deadPet.getDouble("jumpStrength"));

                newLlama.setColor(Llama.Color.valueOf(deadPet.getString("color")));

                attributeMovementSpeed = newLlama.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue(deadPet.getDouble("speed"));
                attributeMaxHealth = newLlama.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue(deadPet.getDouble("maxHealth"));

                if (isAdult) {
                    newLlama.setAdult();
                } else {
                    newLlama.setBaby();
                }

                newLlama.setOwner(player);
                break;
            }
            case "PARROT": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.PARROT);
                Parrot newParrot = (Parrot) entity;
                newParrot.setAge(age);
                if (name != null) {
                    newParrot.setCustomName(name);
                }

                newParrot.setVariant(Parrot.Variant.valueOf(deadPet.getString("parrotType")));

                attributeMaxHealth = newParrot.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue(deadPet.getDouble("maxHealth"));

                if (isAdult) {
                    newParrot.setAdult();
                } else {
                    newParrot.setBaby();
                }

                newParrot.setOwner(player);
                break;
            }
            case "CAT": {
                entity = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.CAT);
                Cat newCat = (Cat) entity;
                newCat.setAge(age);
                if (name != null) {
                    newCat.setCustomName(deadPet.getString("name"));
                }
                newCat.setCollarColor(DyeColor.valueOf(deadPet.getString("collar")));
                newCat.setCatType(Cat.Type.valueOf(deadPet.getString("catType")));
                if (isAdult) {
                    newCat.setAdult();
                } else {
                    newCat.setBaby();
                }

                newCat.setOwner(player);
                break;
            }
            default: {
                player.sendMessage(
                        ChatColor.RED
                                + "Invalid pet type was saved. This is very bad. Plugin data was modified manually. Please check your console and report this issue on GitHub.");
                petLives.logger.severe(
                        "Unable to respawn pet because the type is invalid. Please report this issue on GitHub. Raw data from file: "
                                + deadPet);
            }
        }
        if (entity != null) {
            petLives.petFileHandler.addNewPet(player.getUniqueId(), entity.getUniqueId());
        }
    }
}
