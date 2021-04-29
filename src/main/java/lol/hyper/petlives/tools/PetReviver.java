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
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PetReviver {

    private final PetLives petLives;

    public PetReviver(PetLives petLives) {
        this.petLives = petLives;
    }

    public void respawnPet(Player player, UUID pet, Location locationToSpawn) {
        JSONObject jsonObject = petLives.petFileHandler.getDeadPetsJSON(player.getUniqueId());
        if (jsonObject == null) {
            return;
        }
        JSONObject deadPet = (JSONObject) jsonObject.get(pet.toString());
        String type = (String) deadPet.get("type");
        String name = (String) deadPet.get("name");
        boolean isAdult = (boolean) deadPet.get("isAdult");
        AttributeInstance attributeMovementSpeed;
        AttributeInstance attributeMaxHealth;

        long ageFromJSON = (long) deadPet.get("age");
        int age = Math.toIntExact(ageFromJSON);

        switch (type) {
            case "HORSE":
                Entity horse = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.HORSE);
                Horse newHorse = (Horse) horse;
                newHorse.setAge(age);
                if (name != null) {
                    newHorse.setCustomName(name);
                }
                newHorse.setJumpStrength((Double) deadPet.get("jumpStrength"));

                newHorse.setStyle(Horse.Style.valueOf((String) deadPet.get("style")));
                newHorse.setColor(Horse.Color.valueOf((String) deadPet.get("color")));

                attributeMovementSpeed = newHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue((Double) deadPet.get("speed"));
                attributeMaxHealth = newHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue((Double) deadPet.get("maxHealth"));

                if (isAdult) {
                    newHorse.setAdult();
                } else {
                    newHorse.setBaby();
                }

                newHorse.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), horse.getUniqueId());
                break;
            case "WOLF":
            {
                Entity wolf = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.WOLF);
                Wolf newWolf = (Wolf) wolf;
                newWolf.setAge(age);
                if (name != null) {
                    newWolf.setCustomName((String) deadPet.get("name"));
                }
                newWolf.setCollarColor(DyeColor.valueOf((String) deadPet.get("collar")));
                if (isAdult) {
                    newWolf.setAdult();
                } else {
                    newWolf.setBaby();
                }

                newWolf.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), wolf.getUniqueId());
                break;
            }
            case "DONKEY":
            {
                Entity donkey = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.DONKEY);
                Donkey newDonkey = (Donkey) donkey;
                newDonkey.setAge(age);
                if (name != null) {
                    newDonkey.setCustomName(name);
                }
                newDonkey.setJumpStrength((Double) deadPet.get("jumpStrength"));

                attributeMovementSpeed = newDonkey.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue((Double) deadPet.get("speed"));
                attributeMaxHealth = newDonkey.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue((Double) deadPet.get("maxHealth"));

                if (isAdult) {
                    newDonkey.setAdult();
                } else {
                    newDonkey.setBaby();
                }

                newDonkey.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), donkey.getUniqueId());
                break;
            }
            case "MULE":
            {
                Entity mule = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.MULE);
                Mule newMule = (Mule) mule;
                newMule.setAge(age);
                if (name != null) {
                    newMule.setCustomName(name);
                }
                newMule.setJumpStrength((Double) deadPet.get("jumpStrength"));

                attributeMovementSpeed = newMule.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue((Double) deadPet.get("speed"));
                attributeMaxHealth = newMule.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue((Double) deadPet.get("maxHealth"));

                if (isAdult) {
                    newMule.setAdult();
                } else {
                    newMule.setBaby();
                }

                newMule.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), mule.getUniqueId());
                break;
            }
            case "LLAMA":
            {
                Entity llama = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.LLAMA);
                Llama newLlama = (Llama) llama;
                newLlama.setAge(age);
                if (name != null) {
                    newLlama.setCustomName(name);
                }
                newLlama.setJumpStrength((Double) deadPet.get("jumpStrength"));

                newLlama.setColor(Llama.Color.valueOf((String) deadPet.get("color")));

                attributeMovementSpeed = newLlama.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attributeMovementSpeed.setBaseValue((Double) deadPet.get("speed"));
                attributeMaxHealth = newLlama.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue((Double) deadPet.get("maxHealth"));

                if (isAdult) {
                    newLlama.setAdult();
                } else {
                    newLlama.setBaby();
                }

                newLlama.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), llama.getUniqueId());
                break;
            }
            case "PARROT":
            {
                Entity parrot = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.PARROT);
                Parrot newParrot = (Parrot) parrot;
                newParrot.setAge(age);
                if (name != null) {
                    newParrot.setCustomName(name);
                }

                newParrot.setVariant(Parrot.Variant.valueOf((String) deadPet.get("parrotType")));

                attributeMaxHealth = newParrot.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attributeMaxHealth.setBaseValue((Double) deadPet.get("maxHealth"));

                if (isAdult) {
                    newParrot.setAdult();
                } else {
                    newParrot.setBaby();
                }

                newParrot.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), parrot.getUniqueId());
                break;
            }
            case "CAT":
            {
                Entity cat = locationToSpawn.getWorld().spawnEntity(locationToSpawn, EntityType.CAT);
                Cat newCat = (Cat) cat;
                newCat.setAge(age);
                if (name != null) {
                    newCat.setCustomName((String) deadPet.get("name"));
                }
                newCat.setCollarColor(DyeColor.valueOf((String) deadPet.get("collar")));
                newCat.setCatType(Cat.Type.valueOf((String) deadPet.get("catType")));
                if (isAdult) {
                    newCat.setAdult();
                } else {
                    newCat.setBaby();
                }

                newCat.setOwner(player);
                petLives.petFileHandler.addNewPet(player.getUniqueId(), newCat.getUniqueId());
                break;
            }
        }
    }
}