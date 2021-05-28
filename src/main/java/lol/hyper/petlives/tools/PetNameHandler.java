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

import org.bukkit.entity.Tameable;

import java.util.Locale;

public class PetNameHandler {

    /**
     * Get the name of the pet.
     * @param tameable The mob to get the name for.
     * @return The mob's name. If the pet does not have a custom name, it will simply use the mob type instead.
     */
    public static String getPetName(Tameable tameable) {
        String name;
        if (tameable.getCustomName() == null) {
            name = fixName(tameable.getType().toString());
        } else {
            name = tameable.getCustomName();
        }
        return name;
    }

    /**
     * Fix the mob's type string. getType() returns in all caps so we fix it.
     * @param nameToFix The mob's type.
     * @return The fixed name.
     */
    public static String fixName(String nameToFix) {
        String name = nameToFix.toLowerCase(Locale.ROOT);
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        return name;
    }
}
