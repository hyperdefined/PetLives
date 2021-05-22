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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetFileHandler {

    private final PetLives petLives;

    public PetFileHandler(PetLives petLives) {
        this.petLives = petLives;
    }

    /**
     * Get the player's alive pets file.
     * @param player Player to get file for.
     * @return The player's alive pet file.
     */
    private File getAlivePetsFile(UUID player) {
        return Paths.get(petLives.alivePetsFolder + File.separator + player.toString() + ".json").toFile();
    }

    /**
     * Get the player's dead pets file.
     * @param player Player to get file for.
     * @return The player's dead pet file.
     */
    private File getDeadPetsFile(UUID player) {
        return Paths.get(petLives.deadPetsFolder + File.separator + player.toString() + ".json").toFile();
    }

    /**
     * Read a JSON file.
     * @param file JSON file to be read.
     * @return JSONObject from the file.
     */
    private JSONObject readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            FileReader reader = new FileReader(file);
            obj = parser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            petLives.logger.severe("Unable to read file " + file.getAbsolutePath());
            petLives.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
        return (JSONObject) obj;
    }

    /**
     * Write data to JSON file.
     * @param file File to write data to.
     * @param jsonToWrite Data to write to file. This much be a JSON string.
     */
    private void writeFile(File file, String jsonToWrite) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonToWrite);
            writer.close();
        } catch (IOException e) {
            petLives.logger.severe("Unable to write file " + file.getAbsolutePath());
            petLives.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
    }

    /**
     * Get a player's pet's lives.
     * @param player Pet owner.
     * @param pet Pet to get lives for.
     * @return The lives the pet has.
     */
    public long getPetLives(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            return 0;
        }
        return (long) jsonObject.get(pet.toString());
    }

    /**
     * Get a string list of dead pets' UUID.
     * @param player Player to get dead pets for.
     * @return The list of pets.
     */
    public ArrayList < String > getDeadPetsList(UUID player) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        ArrayList < String > deadPets = new ArrayList < > ();
        if (jsonObject == null) {
            return null;
        }
        for (Object o: jsonObject.keySet()) {
            String key = (String) o;
            deadPets.add(key);
        }
        return deadPets;
    }

    /**
     * Check if the pet is in our storage. This goes through all alive pets files.
     * @param pet Pet to check if it's saved.
     * @return True if it's saved, false if not.
     */
    public boolean isPetInStorage(UUID pet) {
        File[] petFiles = petLives.alivePetsFolder.listFiles();
        if (petFiles != null) {
            for (File currentFile: petFiles) {
                JSONObject currentJSON = readFile(currentFile);
                List < String > pets = new ArrayList < String > (currentJSON.keySet());
                if (pets.contains(pet.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Update a pet's lives with a new number.
     * @param player Owner of the pet.
     * @param pet Pet to edit lives for.
     * @param newLives The new total of lives to set.
     */
    public void updatePetLives(UUID player, UUID pet, long newLives) {
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            return;
        }
        jsonObject.remove(pet.toString());
        jsonObject.put(pet.toString(), newLives);
        writeFile(getAlivePetsFile(player), jsonObject.toJSONString());
    }

    /**
     * Remove a pet from the alive file.
     * @param player Pet owner.
     * @param pet The pet to remove.
     */
    public void removePet(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            return;
        }
        jsonObject.remove(pet.toString());
        writeFile(getAlivePetsFile(player), jsonObject.toJSONString());
    }

    /**
     * Add a new pet to the player's file.
     * @param player Pet owner.
     * @param pet The pet to add.
     */
    public void addNewPet(UUID player, UUID pet) {
        petLives.logger.info("Adding " + pet + " for owner " + Bukkit.getOfflinePlayer(player).getName() + ".");
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.put(pet.toString(), 0);
        writeFile(getAlivePetsFile(player), jsonObject.toJSONString());
    }

    /**
     * See if a mob is owned by a player.
     * @param player Owner to check.
     * @param pet Mob to check.
     * @return True if player owns it, false if not.
     */
    public boolean checkIfPlayerOwnsPet(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            return false;
        }
        return jsonObject.containsKey(pet.toString());
    }

    /**
     * Remove a pet that has died from the dead pet files.
     * @param player Pet owner.
     * @param pet Pet to remove.
     */
    public void removeDeadPet(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        jsonObject.remove(pet.toString());
        writeFile(getDeadPetsFile(player), jsonObject.toJSONString());
    }

    /**
     * Export a pet into the dead pets file.
     * @param player Pet owner.
     * @param tameable Pet to export.
     */
    public void exportPet(UUID player, Tameable tameable) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        JSONObject petDetails = new JSONObject();
        if (tameable.getCustomName() == null) {
            petDetails.put("name", null);
        } else {
            petDetails.put("name", tameable.getCustomName());
        }
        petDetails.put("age", tameable.getAge());
        petDetails.put("type", tameable.getType().toString());
        petDetails.put("isAdult", tameable.isAdult());
        petDetails.put("maxHealth", tameable.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        if (tameable instanceof Wolf) {
            petDetails.put("collar", ((Wolf) tameable).getCollarColor().toString());
        }
        if (tameable instanceof AbstractHorse) {
            petDetails.put("jumpStrength", ((AbstractHorse) tameable).getJumpStrength());
            petDetails.put("speed", tameable.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
            if (tameable instanceof Horse) {
                petDetails.put("color", ((Horse) tameable).getColor().toString());
                petDetails.put("style", ((Horse) tameable).getStyle().toString());
            }
            if (tameable instanceof Llama) {
                petDetails.put("color", ((Llama) tameable).getColor().toString());
            }
        }
        if (tameable instanceof Cat) {
            petDetails.put("catType", ((Cat) tameable).getCatType().toString());
            petDetails.put("collar", ((Cat) tameable).getCollarColor().toString());
        }

        if (tameable instanceof Parrot) {
            petDetails.put("parrotType", ((Parrot) tameable).getVariant().toString());
        }
        JSONObject location = new JSONObject();
        Location deathLocation = tameable.getLocation();
        location.put("x", deathLocation.getX());
        location.put("y", deathLocation.getY());
        location.put("z", deathLocation.getZ());
        location.put("pitch", deathLocation.getPitch());
        location.put("yaw", deathLocation.getYaw());
        location.put("world", deathLocation.getWorld().getName());

        petDetails.put("location", location);
        jsonObject.put(tameable.getUniqueId().toString(), petDetails);
        writeFile(getDeadPetsFile(player), jsonObject.toJSONString());
    }

    /**
     * Get a pet's death location from file.
     * @param player Pet owner.
     * @param pet Pet to get location.
     * @return Location of death.
     */
    public Location getDeathLocation(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        if (jsonObject == null) {
            return null;
        }
        JSONObject petJSON = (JSONObject) jsonObject.get(pet.toString());
        JSONObject locationJSON = (JSONObject) petJSON.get("location");
        double x = (double) locationJSON.get("x");
        double y = (double) locationJSON.get("y");
        double z = (double) locationJSON.get("z");
        float yaw = Float.parseFloat(locationJSON.get("yaw").toString());
        float pitch = Float.parseFloat(locationJSON.get("pitch").toString());
        World world = Bukkit.getWorld((String) locationJSON.get("world"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public JSONObject getDeadPetsJSON(UUID player) {
        return readFile(getDeadPetsFile(player));
    }
}