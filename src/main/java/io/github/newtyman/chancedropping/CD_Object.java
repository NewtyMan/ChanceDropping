package io.github.newtyman.chancedropping;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CD_Object {
    ChanceDropping plugin;

    Material block;
    ItemStack item;
    int chance;

    boolean ERROR_STATE;
    String ERROR_MESSAGE;

    public CD_Object(Player player, String[] args) {
        plugin = ChanceDropping.plugin;
        ERROR_STATE = false;

        // First check to ensure that first parameter is correct one and that we have any arguments at all
        if (args.length == 0 || (!args[0].equals("add") && !args[0].equals("remove") && !args[0].equals("list"))) {
            ERROR_STATE = true;
            ERROR_MESSAGE = plugin.getConfig().getString("error-invalid-syntax");
            return;
        }

        // We transform material args to uppercase in order to avoid issues with material conversion
        for (int i=1; i<args.length; i++) args[i] = args[i].toUpperCase();

        // Second universal check across all actions which defines the block we want to work with
        try {
            block = Material.valueOf(args[1]);
            ERROR_STATE = block.equals(Material.AIR);
            if (ERROR_STATE) { ERROR_MESSAGE = plugin.getConfig().getString("error-cannot-be-air"); return; }
        } catch (Exception e) {
            ERROR_STATE = true;
            ERROR_MESSAGE = plugin.getConfig().getString("error-invalid-block") + " 3";
            return;
        }

        if (args[0].equals("remove")) {
            try {
                chance = Integer.parseInt(args[2]) - 1;
                if (chance < 0) {
                    ERROR_STATE = true;
                    ERROR_MESSAGE = plugin.getConfig().getString("error-out-of-bound-index");
                }
            } catch (Exception e) {
                ERROR_STATE = true;
                ERROR_MESSAGE = plugin.getConfig().getString("error-invalid-index");
            }
            return;
        }

        if (args[0].equals("list")) return;

        // Third parameter that only appears in "add" function
        try {
            // We use the deprecated "getItemInHand" in order to retain compatibility with 1.8 version
            if (args[2].equalsIgnoreCase("hand")) item = player.getInventory().getItemInHand().clone();
            else item = new ItemStack(Material.valueOf(args[2]));
            ERROR_STATE = item.getType().equals(Material.AIR);
            if (ERROR_STATE) { ERROR_MESSAGE = plugin.getConfig().getString("error-cannot-be-air"); return; }
        } catch (Exception e) {
            ERROR_STATE = true;
            ERROR_MESSAGE = plugin.getConfig().getString("error-invalid-block") + " 2";
            return;
        }

        // Fourth parameter that only appears in "add" function
        try { chance = Integer.parseInt(args[3]); }
        catch (Exception e) {
            ERROR_STATE = true;
            ERROR_MESSAGE = plugin.getConfig().getString("error-invalid-chance");
        }
    }

    public CD_Object(Material _block, ItemStack _item, int _chance) {
        item = _item;
        block = _block;
        chance = _chance;
    }
}
