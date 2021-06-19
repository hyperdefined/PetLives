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

package lol.hyper.petlives;

import lol.hyper.petlives.events.ChunkLoad;
import lol.hyper.petlives.events.EntityDamage;
import lol.hyper.petlives.events.EntityTame;
import lol.hyper.petlives.events.PlayerInteract;
import lol.hyper.petlives.tools.PetFileHandler;
import lol.hyper.petlives.tools.PetReviver;
import lol.hyper.petlives.tools.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class PetLives extends JavaPlugin {

    public final Logger logger = this.getLogger();
    public final File alivePetsFolder =
            Paths.get(this.getDataFolder() + File.separator + "alivepets").toFile();
    public final File deadPetsFolder =
            Paths.get(this.getDataFolder() + File.separator + "deadpets").toFile();
    final int CONFIG_VERSION = 1;
    private final File configFile = new File(this.getDataFolder(), "config.yml");
    public Material livesItem;
    public Material reviveItem;
    public FileConfiguration config;
    public PetFileHandler petFileHandler;
    public ChunkLoad chunkLoad;
    public EntityDamage entityDamage;
    public EntityTame entityTame;
    public PlayerInteract playerInteract;
    public CommandPet commandPet;
    public PetReviver petReviver;

    @Override
    public void onEnable() {
        petFileHandler = new PetFileHandler(this);
        commandPet = new CommandPet(this);
        chunkLoad = new ChunkLoad(this);
        entityDamage = new EntityDamage(this);
        entityTame = new EntityTame(this);
        playerInteract = new PlayerInteract(this);
        petReviver = new PetReviver(this);
        loadConfig();

        this.getCommand("petlives").setExecutor(commandPet);
        Bukkit.getServer().getPluginManager().registerEvents(chunkLoad, this);
        Bukkit.getServer().getPluginManager().registerEvents(entityDamage, this);
        Bukkit.getServer().getPluginManager().registerEvents(entityTame, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerInteract, this);

        Metrics metrics = new Metrics(this, 11226);

        new UpdateChecker(this, 91822).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("You are running the latest version.");
            } else {
                logger.info(
                        "There is a new version available! Please download at https://www.spigotmc.org/resources/petlives.91822/");
            }
        });
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        if (config.getInt("config-version") != CONFIG_VERSION) {
            logger.warning("Your config file is outdated! Please regenerate the config.");
        }

        if (!alivePetsFolder.exists()) {
            try {
                Files.createDirectory(alivePetsFolder.toPath());
            } catch (IOException e) {
                logger.severe("Unable to create folder " + alivePetsFolder.getAbsolutePath());
                e.printStackTrace();
            }
        }

        if (!deadPetsFolder.exists()) {
            try {
                Files.createDirectory(deadPetsFolder.toPath());
            } catch (IOException e) {
                logger.severe("Unable to create folder " + deadPetsFolder.getAbsolutePath());
                e.printStackTrace();
            }
        }

        if (Material.matchMaterial(config.getString("items.lives-item")) == null) {
            logger.warning(config.getString("items.lives-item") + " is NOT a valid material! Using default.");
            livesItem = Material.GOLDEN_APPLE;
        } else {
            livesItem = Material.valueOf(config.getString("items.lives-item"));
        }

        if (Material.matchMaterial(config.getString("items.revive-item")) == null) {
            logger.warning(config.getString("items.revive-item") + " is NOT a valid material! Using default.");
            reviveItem = Material.ENCHANTED_GOLDEN_APPLE;
        } else {
            reviveItem = Material.valueOf(config.getString("items.revive-item"));
        }
    }
}
