package lol.hyper.petlives;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandPet implements TabExecutor {

    private final PetLives petLives;

    public CommandPet(PetLives petLives) {
        this.petLives = petLives;
    }

    public ArrayList<Player> playerisCheckingMob = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.GREEN + "PetLives version " + petLives.getDescription().getVersion() + ". Created by hyperdefined.");
            sender.sendMessage(ChatColor.GREEN + "Use /petlives help for command help.");
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]) {
            case "help": {
                sender.sendMessage(ChatColor.GOLD + "-----------------PetLives-----------------");
                sender.sendMessage(ChatColor.GOLD + "/petlives help " + ChatColor.YELLOW + "- Shows this menu.");
                sender.sendMessage(ChatColor.GOLD + "/petlives check <pet UUID> " + ChatColor.YELLOW + "- Check on a dead pet.");
                sender.sendMessage(ChatColor.GOLD + "/petlives deadpets " + ChatColor.YELLOW + "- Shows a list of your dead pets.");
                sender.sendMessage(ChatColor.GOLD + "/petlives revive <pet UUID> " + ChatColor.YELLOW + "- Respawn a pet.");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            }
            case "check": {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "You must say which pet you want to see. See /petlives deadpets for a list.");
                    return true;
                }
                UUID petUUID = UUID.fromString(args[1]);
                List<String> pets = new ArrayList<String>(petLives.petFileHandler.getDeadPets(player.getUniqueId()).keySet());
                if (!pets.contains(petUUID.toString())) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid pet!");
                    return true;
                }
                JSONObject jsonObject = petLives.petFileHandler.getDeadPets(player.getUniqueId());
                JSONObject pet = (JSONObject) jsonObject.get(petUUID.toString());
                String name = (String) pet.get("name");
                if (name == null) {
                    name = (String) pet.get("type");
                    name = PetNameHandler.fixName(name);
                }
                Location deathLocation = petLives.petFileHandler.getDeathLocation(player.getUniqueId(), petUUID);
                sender.sendMessage(ChatColor.GOLD + "-----------------" + name + "-----------------");
                sender.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.YELLOW + pet.get("type").toString());
                sender.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.YELLOW +petUUID.toString());
                sender.sendMessage(ChatColor.GOLD + "X: " + ChatColor.YELLOW + (int) deathLocation.getX() + ChatColor.GOLD +
                                " Y: " + ChatColor.YELLOW + (int) deathLocation.getY() + ChatColor.GOLD
                                + " Z: " + ChatColor.YELLOW + (int) deathLocation.getZ() + ChatColor.GOLD
                                + " (" + deathLocation.getWorld().getName() + ")");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            }
            case "deadpets": {
                ArrayList<String> deadPets = petLives.petFileHandler.getDeadPetsList(player.getUniqueId());
                if (deadPets == null) {
                    sender.sendMessage(ChatColor.RED + "None of your pets have died.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "-----------------Dead Pets-----------------");
                JSONObject jsonObject = petLives.petFileHandler.getDeadPets(player.getUniqueId());
                sender.sendMessage(ChatColor.YELLOW + "Click on a pet to see its information.");
                for (String x : deadPets) {
                    JSONObject pet = (JSONObject) jsonObject.get(x);
                    String name = (String) pet.get("name");
                    if (name == null) {
                        name = (String) pet.get("type");
                        name = PetNameHandler.fixName(name);
                    }
                    TextComponent textComponent = new TextComponent(name);
                    textComponent.setColor(ChatColor.YELLOW);
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                            ChatColor.GOLD + "Name: " + ChatColor.YELLOW + name + "\n" +
                            ChatColor.GOLD + "Type: " + ChatColor.YELLOW + pet.get("type") + "\n" +
                            ChatColor.GOLD + "UUID: " + ChatColor.YELLOW + x)));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/petlives check " + x));
                    sender.spigot().sendMessage(textComponent);
                }
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                break;
            }
            case "revive": {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "You must say which pet you want to revive.");
                    return true;
                }
                if (player.getInventory().getItemInMainHand().getType() != petLives.reviveItem) {
                    sender.sendMessage(ChatColor.RED + "You must be holding a " + petLives.reviveItem.toString() + " to revive a pet.");
                    return true;
                }
                UUID petUUID = UUID.fromString(args[1]);
                List<String> pets = new ArrayList<String>(petLives.petFileHandler.getDeadPets(player.getUniqueId()).keySet());
                if (!pets.contains(petUUID.toString())) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid pet!");
                    return true;
                }
                petLives.petReviver.respawnPet(player, petUUID, player.getLocation());
                player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
                petLives.petFileHandler.removeDeadPet(player.getUniqueId(), petUUID);
                int index = player.getInventory().getHeldItemSlot();
                player.getInventory().setItem(index, new ItemStack(Material.AIR));
                break;
            }
            case "uuid": {
                sender.sendMessage(ChatColor.GREEN + "Right click on the mob to see its UUID.");
                playerisCheckingMob.add(player);
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args[0].equalsIgnoreCase("check")) {
            return petLives.petFileHandler.getDeadPetsList(Bukkit.getPlayerExact(sender.getName()).getUniqueId());
        }
        if (args[0].equalsIgnoreCase("revive")) {
            return petLives.petFileHandler.getDeadPetsList(Bukkit.getPlayerExact(sender.getName()).getUniqueId());
        }
        return Arrays.asList("help", "check", "revive", "deadpets");
    }
}
