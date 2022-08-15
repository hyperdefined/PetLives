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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;

public class CommandPet implements TabExecutor {

    public final ArrayList<Player> playerisCheckingMob = new ArrayList<>();
    private final PetLives petLives;
    private final BukkitAudiences audiences;

    public CommandPet(PetLives petLives) {
        this.petLives = petLives;
        this.audiences = petLives.getAdventure();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || sender instanceof ConsoleCommandSender) {
            audiences.sender(sender).sendMessage(Component.text("PetLives version " + petLives.getDescription().getVersion() + ". Created by hyperdefined.").color(NamedTextColor.GREEN));
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]) {
            case "help": {
                audiences.sender(sender).sendMessage(Component.text("-----------------PetLives-----------------").color(NamedTextColor.GOLD));
                audiences.sender(sender).sendMessage(Component.text("/petlives help ").color(NamedTextColor.GOLD).append(Component.text("- Shows this menu.").color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("/petlives check <pet UUID> ").color(NamedTextColor.GOLD).append(Component.text("- Check on a dead pet.").color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("/petlives deadpets ").color(NamedTextColor.GOLD).append(Component.text("- Shows a list of your dead pets.").color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("/petlives revive <pet UUID> ").color(NamedTextColor.GOLD).append(Component.text("- Respawn a pet.").color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("/petlives uuid ").color(NamedTextColor.GOLD).append(Component.text("- Check on a mob's UUID.").color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("/petlives setlives <uuid> <lives> ").color(NamedTextColor.GOLD).append(Component.text("- Check on a mob's UUID.").color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("--------------------------------------------").color(NamedTextColor.GOLD));
                break;
            }
            case "check": {
                if (!sender.hasPermission("petlives.command.check")) {
                    audiences.sender(sender).sendMessage(Component.text("You do not have permission to use this!").color(NamedTextColor.RED));
                    return true;
                }
                if (args.length == 1) {
                    audiences.sender(sender).sendMessage(Component.text("You must say which pet you want to see. See /petlives deadpets for a list.").color(NamedTextColor.RED));
                    return true;
                }
                UUID petUUID;
                try {
                    petUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException exception) {
                    audiences.sender(sender).sendMessage(Component.text(args[1] + " is not a valid UUID.").color(NamedTextColor.RED));
                    return true;
                }
                List<String> pets = new ArrayList<>(petLives.petFileHandler
                        .getDeadPetsJSON(player.getUniqueId())
                        .keySet());
                if (!pets.contains(petUUID.toString())) {
                    audiences.sender(sender).sendMessage(Component.text("That is not a valid pet!").color(NamedTextColor.RED));
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
                audiences.sender(sender).sendMessage(Component.text("-----------------" + name + "-----------------").color(NamedTextColor.GOLD));
                audiences.sender(sender).sendMessage(Component.text("Type: ").color(NamedTextColor.GOLD).append(Component.text(pet.get("type").toString()).color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("UUID: ").color(NamedTextColor.GOLD).append(Component.text(petUUID.toString()).color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("X: ").color(NamedTextColor.GOLD).append(Component.text((int) deathLocation.getX()).color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("Y: ").color(NamedTextColor.GOLD).append(Component.text((int) deathLocation.getY()).color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("Z: ").color(NamedTextColor.GOLD).append(Component.text((int) deathLocation.getZ()).color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("World: ").color(NamedTextColor.GOLD).append(Component.text(deathLocation.getWorld().getName()).color(NamedTextColor.YELLOW)));
                audiences.sender(sender).sendMessage(Component.text("--------------------------------------------").color(NamedTextColor.GOLD));
                break;
            }
            case "deadpets": {
                if (!sender.hasPermission("petlives.command.deadpets")) {
                    audiences.sender(sender).sendMessage(Component.text("You do not have permission to use this!").color(NamedTextColor.RED));
                    return true;
                }
                ArrayList<String> deadPets = petLives.petFileHandler.getDeadPetsList(player.getUniqueId());
                if (deadPets == null || deadPets.size() == 0) {
                    audiences.sender(sender).sendMessage(Component.text("You currently have no dead pets saved.").color(NamedTextColor.RED));
                    return true;
                }
                audiences.sender(sender).sendMessage(Component.text("-----------------Dead Pets-----------------").color(NamedTextColor.GOLD));
                JSONObject jsonObject = petLives.petFileHandler.getDeadPetsJSON(player.getUniqueId());
                audiences.sender(sender).sendMessage(Component.text("Click on a pet to see its information.").color(NamedTextColor.YELLOW));
                for (String x : deadPets) {
                    JSONObject pet = jsonObject.getJSONObject(x);
                    String name = pet.getString("name");
                    if (name == null || name.isEmpty()) {
                        name = pet.getString("type");
                        name = PetNameHandler.fixName(name);
                    }
                    Component hoverText = Component.text("Name: ").color(NamedTextColor.GOLD).append(Component.text(name).color(NamedTextColor.YELLOW)).append(Component.newline())
                            .append(Component.text("Type: ").color(NamedTextColor.GOLD).append(Component.text(pet.getString("type")).color(NamedTextColor.YELLOW).append(Component.newline())
                                    .append(Component.text("UUID: ").color(NamedTextColor.GOLD).append(Component.text(x).color(NamedTextColor.YELLOW)))));
                    ClickEvent click = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/petlives check " + x);
                    Component component = Component.text(name).color(NamedTextColor.YELLOW).hoverEvent(HoverEvent.showText(hoverText)).clickEvent(click);
                    audiences.sender(sender).sendMessage(component);
                }
                audiences.sender(sender).sendMessage(Component.text("--------------------------------------------").color(NamedTextColor.GOLD));
                break;
            }
            case "revive": {
                if (!sender.hasPermission("petlives.command.revive")) {
                    audiences.sender(sender).sendMessage(Component.text("You do not have permission to use this!").color(NamedTextColor.RED));
                    return true;
                }
                if (!petLives.config.getBoolean("allow-revives")) {
                    audiences.sender(sender).sendMessage(Component.text("You cannot revive pets.").color(NamedTextColor.RED));
                    return true;
                }
                if (args.length == 1) {
                    audiences.sender(sender).sendMessage(Component.text("You must say which pet you want to revive.").color(NamedTextColor.RED));
                    return true;
                }
                int index = player.getInventory().getHeldItemSlot();
                ItemStack heldItem = player.getInventory().getItem(index);
                if (heldItem == null || heldItem.getType() != petLives.reviveItem) {
                    audiences.sender(sender).sendMessage(Component.text("You must be holding a " + petLives.reviveItem.toString()
                            + " to revive a pet.").color(NamedTextColor.RED));
                    return true;
                }
                UUID petUUID;
                try {
                    petUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException exception) {
                    audiences.sender(sender).sendMessage(Component.text(args[1] + " is not a valid UUID.").color(NamedTextColor.RED));
                    return true;
                }
                List<String> pets = new ArrayList<>(petLives.petFileHandler
                        .getDeadPetsJSON(player.getUniqueId())
                        .keySet());
                if (!pets.contains(petUUID.toString())) {
                    audiences.sender(sender).sendMessage(Component.text("That is not a valid pet!").color(NamedTextColor.RED));
                    return true;
                }
                petLives.petReviver.respawnPet(player, petUUID, player.getLocation());
                player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
                petLives.petFileHandler.removeDeadPet(player.getUniqueId(), petUUID);
                heldItem.setAmount(heldItem.getAmount() - 1);
                player.getInventory().setItem(index, heldItem);
                audiences.sender(sender).sendMessage(Component.text("Your pet is alive once again!").color(NamedTextColor.GREEN));
                break;
            }
            case "uuid": {
                audiences.sender(sender).sendMessage(Component.text("Right click on the mob to see its UUID.").color(NamedTextColor.GREEN));
                playerisCheckingMob.add(player);
                break;
            }
            case "setlives": {
                if (!sender.hasPermission("petlives.command.setlives")) {
                    audiences.sender(sender).sendMessage(Component.text("You do not have permission to use this!").color(NamedTextColor.RED));
                    return true;
                }
                if (args.length != 3) {
                    audiences.sender(sender).sendMessage(Component.text("Please use /petlives setlives <uuid> <lives> instead.").color(NamedTextColor.RED));
                    return true;
                }
                UUID petUUID;
                try {
                    petUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException exception) {
                    audiences.sender(sender).sendMessage(Component.text(args[1] + " is not a valid UUID.").color(NamedTextColor.RED));
                    return true;
                }
                Entity petEntity = Bukkit.getEntity(petUUID);
                if (petEntity == null) {
                    audiences.sender(sender).sendMessage(Component.text("That entity could not be found by that UUID.").color(NamedTextColor.RED));
                    return true;
                }
                try {
                    Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    audiences.sender(sender).sendMessage(Component.text(args[2] + " is not a valid number!").color(NamedTextColor.RED));
                    return true;
                }
                int newLives = Integer.parseInt(args[2]);
                petLives.petFileHandler.updateLives(petEntity, newLives);
                audiences.sender(sender).sendMessage(Component.text("This pet now has " + petLives.petFileHandler.getLives(petEntity) + " lives.").color(NamedTextColor.GREEN));
                break;
            }
            default: {
                audiences.sender(sender).sendMessage(Component.text("Unknown option. Please see /petlives help for all valid options.").color(NamedTextColor.RED));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("revive")) {
            return petLives.petFileHandler.getDeadPetsList(
                    Bukkit.getPlayerExact(sender.getName()).getUniqueId());
        }
        if (args.length == 1) {
            return Arrays.asList("help", "check", "revive", "deadpets", "uuid", "setlives");
        }
        return Collections.emptyList();
    }
}
