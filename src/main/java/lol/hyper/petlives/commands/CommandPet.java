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

package lol.hyper.petlives.commands;

import lol.hyper.petlives.PetLives;
import lol.hyper.petlives.tools.PetNameHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;

public class CommandPet implements TabExecutor {

    public final ArrayList<Player> playerisCheckingMob = new ArrayList<>();
    private final PetLives petLives;

    public CommandPet(PetLives petLives) {
        this.petLives = petLives;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "PetLives version "
                    + petLives.getDescription().getVersion() + ". Created by hyperdefined.");
            sender.sendMessage(ChatColor.GREEN + "Use /petlives help for command help.");
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]) {
            case "help": {
                sender.sendMessage(ChatColor.GOLD + "-----------------PetLives-----------------");
                sender.sendMessage(ChatColor.GOLD + "/petlives help " + ChatColor.YELLOW + "- Shows this menu.");
                sender.sendMessage(
                        ChatColor.GOLD + "/petlives check <pet UUID> " + ChatColor.YELLOW + "- Check on a dead pet.");
                sender.sendMessage(ChatColor.GOLD + "/petlives deadpets " + ChatColor.YELLOW
                        + "- Shows a list of your dead pets.");
                sender.sendMessage(
                        ChatColor.GOLD + "/petlives revive <pet UUID> " + ChatColor.YELLOW + "- Respawn a pet.");
                sender.sendMessage(ChatColor.GOLD + "/petlives uuid " + ChatColor.YELLOW + "- Check on a mob's UUID.");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            }
            case "check": {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED
                            + "You must say which pet you want to see. See /petlives deadpets for a list.");
                    return true;
                }
                UUID petUUID;
                try {
                    petUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a valid UUID.");
                    return true;
                }
                List<String> pets = new ArrayList<String>(petLives.petFileHandler
                        .getDeadPetsJSON(player.getUniqueId())
                        .keySet());
                if (!pets.contains(petUUID.toString())) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid pet!");
                    return true;
                }
                JSONObject jsonObject = petLives.petFileHandler.getDeadPetsJSON(player.getUniqueId());
                JSONObject pet = jsonObject.getJSONObject(petUUID.toString());
                String name = pet.getString("name");
                if (name.isEmpty()) {
                    name = (String) pet.get("type");
                    name = PetNameHandler.fixName(name);
                }
                Location deathLocation = petLives.petFileHandler.getDeathLocation(player.getUniqueId(), petUUID);
                sender.sendMessage(ChatColor.GOLD + "-----------------" + name + "-----------------");
                sender.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.YELLOW
                        + pet.get("type").toString());
                sender.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.YELLOW + petUUID);
                sender.sendMessage(
                        ChatColor.GOLD + "X: " + ChatColor.YELLOW + (int) deathLocation.getX() + ChatColor.GOLD + " Y: "
                                + ChatColor.YELLOW + (int) deathLocation.getY() + ChatColor.GOLD + " Z: "
                                + ChatColor.YELLOW + (int) deathLocation.getZ() + ChatColor.GOLD + " ("
                                + deathLocation.getWorld().getName() + ")");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            }
            case "deadpets": {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
                    return true;
                }
                ArrayList<String> deadPets = petLives.petFileHandler.getDeadPetsList(player.getUniqueId());
                if (deadPets == null || deadPets.size() == 0) {
                    sender.sendMessage(ChatColor.RED + "You currently have no dead pets saved.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "-----------------Dead Pets-----------------");
                JSONObject jsonObject = petLives.petFileHandler.getDeadPetsJSON(player.getUniqueId());
                sender.sendMessage(ChatColor.YELLOW + "Click on a pet to see its information.");
                for (String x : deadPets) {
                    JSONObject pet = jsonObject.getJSONObject(x);
                    String name = pet.getString("name");
                    if (name == null || name.isEmpty()) {
                        name = pet.getString("type");
                        name = PetNameHandler.fixName(name);
                    }
                    TextComponent textComponent = new TextComponent(name);
                    textComponent.setColor(ChatColor.YELLOW);
                    textComponent.setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new Text(ChatColor.GOLD + "Name: " + ChatColor.YELLOW + name + "\n" + ChatColor.GOLD
                                    + "Type: " + ChatColor.YELLOW + pet.getString("type") + "\n" + ChatColor.GOLD
                                    + "UUID: " + ChatColor.YELLOW + x)));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/petlives check " + x));
                    sender.spigot().sendMessage(textComponent);
                }
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            }
            case "revive": {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
                    return true;
                }
                if (!petLives.config.getBoolean("allow-revives")) {
                    sender.sendMessage(ChatColor.RED + "You cannot revive pets.");
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "You must say which pet you want to revive.");
                    return true;
                }
                int index = player.getInventory().getHeldItemSlot();
                ItemStack heldItem = player.getInventory().getItem(index);
                if (heldItem == null || heldItem.getType() != petLives.reviveItem) {
                    sender.sendMessage(ChatColor.RED + "You must be holding a " + petLives.reviveItem.toString()
                            + " to revive a pet.");
                    return true;
                }
                UUID petUUID;
                try {
                    petUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a valid UUID.");
                    return true;
                }
                List<String> pets = new ArrayList<String>(petLives.petFileHandler
                        .getDeadPetsJSON(player.getUniqueId())
                        .keySet());
                if (!pets.contains(petUUID.toString())) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid pet!");
                    return true;
                }
                petLives.petReviver.respawnPet(player, petUUID, player.getLocation());
                player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
                petLives.petFileHandler.removeDeadPet(player.getUniqueId(), petUUID);
                heldItem.setAmount(heldItem.getAmount() - 1);
                player.getInventory().setItem(index, heldItem);
                player.sendMessage(ChatColor.GREEN + "Your pet is alive once again!");
                break;
            }
            case "uuid": {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Right click on the mob to see its UUID.");
                playerisCheckingMob.add(player);
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "Unknown option. Please see /petlives help for all valid options.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args[0].equalsIgnoreCase("check")) {
            return petLives.petFileHandler.getDeadPetsList(
                    Bukkit.getPlayerExact(sender.getName()).getUniqueId());
        }
        if (args[0].equalsIgnoreCase("revive")) {
            return petLives.petFileHandler.getDeadPetsList(
                    Bukkit.getPlayerExact(sender.getName()).getUniqueId());
        }
        if (args.length == 1) {
            return Arrays.asList("help", "check", "revive", "deadpets", "uuid");
        }
        return Collections.emptyList();
    }
}
