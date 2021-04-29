package lol.hyper.petlives.tools;

import org.bukkit.entity.Tameable;

import java.util.Locale;

public class PetNameHandler {

    public static String getPetName(Tameable tameable) {
        String name;
        if (tameable.getCustomName() == null) {
            // make it all lowercase, then capitalize the first letter
            // getType() returns with all caps, so we fix that
            name = tameable.getType().toString();
            name = name.toLowerCase(Locale.ROOT);
            name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        } else {
            name = tameable.getCustomName();
        }
        return name;
    }

    public static String fixName(String nameToFix) {
        String name = nameToFix.toLowerCase(Locale.ROOT);
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        return name;
    }
}