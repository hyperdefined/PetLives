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
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
        return Paths.get(petLives.alivePetsFolder + File.separator + player.toString() + ".json")
                .toFile();
    }

    /**
     * Get the player's dead pets file.
     * @param player Player to get file for.
     * @return The player's dead pet file.
     */
    private File getDeadPetsFile(UUID player) {
        return Paths.get(petLives.deadPetsFolder + File.separator + player.toString() + ".json")
                .toFile();
    }

    /**
     * Read data from JSON file.
     * @param file File to read data from.
     * @return JSONObject with JSON data.
     */
    private JSONObject readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        JSONObject object = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            object = new JSONObject(sb.toString());
            br.close();
        } catch (Exception e) {
            petLives.logger.severe("Unable to read file " + file.getAbsolutePath());
            petLives.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Write data to JSON file.
     * @param file File to write data to.
     * @param jsonToWrite Data to write to file. This much be a JSON string.
     */
    private void writeFile(File file, JSONObject jsonToWrite) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(String.valueOf(jsonToWrite));
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
    public int getPetLives(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            return 0;
        }
        return jsonObject.getInt(pet.toString());
    }

    /**
     * Get a string list of dead pets' UUID.
     * @param player Player to get dead pets for.
     * @return The list of pets.
     */
    public ArrayList<String> getDeadPetsList(UUID player) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        if (jsonObject == null) {
            return null;
        }
        return new ArrayList<>(jsonObject.keySet());
    }

    /**
     * Check if the pet is in our storage. This goes through all alive pets files.
     * @param pet Pet to check if it's saved.
     * @return True if it's saved, false if not.
     */
    public boolean isPetInStorage(UUID pet) {
        File[] petFiles = petLives.alivePetsFolder.listFiles();
        if (petFiles != null) {
            for (File currentFile : petFiles) {
                JSONObject currentJSON = readFile(currentFile);
                List<String> pets = new ArrayList<>(currentJSON.keySet());
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
    public void updatePetLives(UUID player, UUID pet, int newLives) {
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            return;
        }
        jsonObject.remove(pet.toString());
        jsonObject.put(pet.toString(), newLives);
        writeFile(getAlivePetsFile(player), jsonObject);
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
        writeFile(getAlivePetsFile(player), jsonObject);
    }

    /**
     * Add a new pet to the player's file.
     * @param player Pet owner.
     * @param pet The pet to add.
     */
    public void addNewPet(UUID player, UUID pet) {
        petLives.logger.info("Adding " + pet + " for owner "
                + Bukkit.getOfflinePlayer(player).getName() + ".");
        JSONObject jsonObject = readFile(getAlivePetsFile(player));
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.put(pet.toString(), 0);
        writeFile(getAlivePetsFile(player), jsonObject);
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
        return jsonObject.keySet().contains(pet.toString());
    }

    /**
     * Remove a pet that has died from the dead pet files.
     * @param player Pet owner.
     * @param pet Pet to remove.
     */
    public void removeDeadPet(UUID player, UUID pet) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        if (jsonObject == null) {
            return;
        }
        jsonObject.remove(pet.toString());
        writeFile(getDeadPetsFile(player), jsonObject);
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
            petDetails.put("name", "");
        } else {
            petDetails.put("name", tameable.getCustomName());
        }
        petDetails.put("age", tameable.getAge());
        petDetails.put("type", tameable.getType().toString());
        petDetails.put("isAdult", tameable.isAdult());
        petDetails.put(
                "maxHealth", tameable.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        if (tameable instanceof Wolf) {
            petDetails.put("collar", ((Wolf) tameable).getCollarColor().toString());
        }
        if (tameable instanceof AbstractHorse) {
            petDetails.put("jumpStrength", ((AbstractHorse) tameable).getJumpStrength());
            petDetails.put(
                    "speed",
                    tameable.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
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
        writeFile(getDeadPetsFile(player), jsonObject);
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
        JSONObject petJSON = jsonObject.getJSONObject(pet.toString());
        JSONObject locationJSON = petJSON.getJSONObject("location");
        double x = locationJSON.getDouble("x");
        double y = locationJSON.getDouble("y");
        double z = locationJSON.getDouble("z");
        float yaw = Float.parseFloat(String.valueOf(locationJSON.getFloat("yaw")));
        float pitch = Float.parseFloat(String.valueOf(locationJSON.getFloat("pitch")));
        World world = Bukkit.getWorld(locationJSON.getString("world"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public JSONObject getDeadPetsJSON(UUID player) {
        return readFile(getDeadPetsFile(player));
    }
}
