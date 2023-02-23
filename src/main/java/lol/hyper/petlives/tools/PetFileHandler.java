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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
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
     * Get the player's dead pets file.
     * @param player Player to get file for.
     * @return The player's dead pet file.
     */
    private File getDeadPetsFile(UUID player) {
        return Paths.get(petLives.deadPetsFolder + File.separator + player.toString() + ".json")
                .toFile();
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
     * Read data from JSON file.
     * @param file File to read data from.
     * @return JSONObject with JSON data.
     */
    private @Nullable JSONObject readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        JSONObject object;
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
            return null;
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
            writer.write(jsonToWrite.toString(4));
            writer.close();
        } catch (IOException e) {
            petLives.logger.severe("Unable to write file " + file.getAbsolutePath());
            petLives.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
    }

    /**
     * Get a string list of dead pets' UUID.
     * @param player Player to get dead pets for.
     * @return The list of pets.
     */
    public List<String> getDeadPetsList(UUID player) {
        JSONObject jsonObject = readFile(getDeadPetsFile(player));
        if (jsonObject == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(jsonObject.keySet());
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
     * @param tameable Pet to export.
     */
    public void exportPet(Tameable tameable) {
        UUID owner = tameable.getOwner().getUniqueId();
        JSONObject jsonObject = readFile(getDeadPetsFile(owner));
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        JSONObject petDetails = new JSONObject();
        if (tameable.getCustomName() == null) {
            petDetails.put("name", "");
        } else {
            String finalName;
            // mcmmo hearts work around
            // this will remove the custom name, but mcmmo messes it up with the hearts
            if (tameable.getCustomName().contains("❤")) {
                finalName = PetNameHandler.fixName(tameable.getType().toString());
            } else {
                finalName = tameable.getCustomName();
            }
            petDetails.put("name", finalName);
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
        writeFile(getDeadPetsFile(owner), jsonObject);
    }

    /**
     * Get a pet's death location from file.
     * @param player Pet owner.
     * @param pet Pet to get location.
     * @return Location of death.
     */
    public @Nullable Location getDeathLocation(UUID player, UUID pet) {
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

    /**
     * Get how many lives a pet has.
     * @param entity The entity to check.
     * @return How many lives it has.
     */
    public int getLives(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        Integer lives = 0;
        if (container.has(petLives.petLivesKey, PersistentDataType.INTEGER)) {
            lives = container.get(petLives.petLivesKey, PersistentDataType.INTEGER);
        }
        if (lives == null) {
            return 0;
        } else {
            return lives;
        }
    }

    /**
     * Update a pet's lives.
     * @param entity The entity to update.
     */
    public void updateLives(Entity entity, int newLives) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(petLives.petLivesKey, PersistentDataType.INTEGER)) {
            container.set(petLives.petLivesKey, PersistentDataType.INTEGER, newLives);
        }
    }

    /**
     * Adds 0 lives to a pet.
     * @param entity The entity to add the tag to.
     */
    public void addLivesTag(Tameable entity, UUID owner) {
        // see if the pet has the old JSON storage first
        JSONObject jsonObject = readFile(getAlivePetsFile(owner));
        int lives = 0;

        if (jsonObject != null) {
            if (jsonObject.has(entity.getUniqueId().toString())) {
                lives = jsonObject.getInt(entity.getUniqueId().toString());
                jsonObject.remove(entity.getUniqueId().toString());
                if (jsonObject.isEmpty()) {
                    try {
                        Files.delete(getAlivePetsFile(owner).toPath());
                        petLives.logger.info("Removing old pet file for " + entity.getUniqueId());
                    } catch (IOException e) {
                        petLives.logger.warning("Unable to delete file!");
                        e.printStackTrace();
                    }
                }
            }
        }

        // set the lives the new way
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(petLives.petLivesKey, PersistentDataType.INTEGER)) {
            container.set(petLives.petLivesKey, PersistentDataType.INTEGER, lives);
        }
    }
}
