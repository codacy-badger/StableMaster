package net.nperkins.stablemaster.commands.subcommands;

import net.nperkins.stablemaster.StableMaster;
import net.nperkins.stablemaster.commands.CommandInfo;
import net.nperkins.stablemaster.commands.SubCommand;
import net.nperkins.stablemaster.data.Stable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Release extends SubCommand {

    public Release() {
        setName("release");
    }

    public void handle(CommandInfo commandInfo) {
        final Player player = (Player) commandInfo.getSender();

        StableMaster.commandQueue.put(player, this);
        StableMaster.langMessage(player, "punch-animal");
    }

    public void handleInteract(Stable stable, Player player, Tameable animal) {
        final Animals a = (Animals) animal;
        final ConfigurationSection config = StableMaster.getPlugin().getConfig().getConfigurationSection("command.info");

        // If animal is a horse, drop its saddle and remove it from the stable.
        if (animal instanceof AbstractHorse) {
            final AbstractHorse horse = (AbstractHorse) animal;
            final Inventory inv = horse.getInventory();

            // Saddle must drop, else it cannot be tamed later
            ItemStack[] contents = inv.getContents();
            if (contents[0] != null) {
                horse.getWorld().dropItemNaturally(horse.getLocation(), contents[0]);
                contents[0] = null;
                inv.setContents(contents);
            }

            stable.removeHorse(horse);
        }

        if (config.getBoolean("clear-custom-name")) {
            a.setCustomNameVisible(false);
            a.setCustomName("");
        }

        // Finally remove ownership of the animal
        animal.setTamed(false);

        StableMaster.langFormat(player, "command.release.released", a.getType());
    }
}
