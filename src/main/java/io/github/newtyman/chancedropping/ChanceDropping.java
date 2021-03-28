package io.github.newtyman.chancedropping;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ChanceDropping extends JavaPlugin implements Listener {
    public static ChanceDropping plugin;
    HashMap<String, List<CD_Object>> playerData = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

        ConfigurationSection section = getConfig().getConfigurationSection("data");
        if (section == null) return;
        section.getKeys(false).forEach(key -> {
            List<CD_Object> objectList = new ArrayList<>();
            ConfigurationSection block = section.getConfigurationSection(key);
            assert block != null;
            block.getKeys(false).forEach(index ->
                    objectList.add(new CD_Object(Material.valueOf(key), block.getItemStack(index + ".item"), block.getInt(index + ".chance"))));
            playerData.put(key, objectList);
        });
    }

    @Override
    public void onDisable() {
        ConfigurationSection section = getConfig().createSection("data");
        playerData.forEach((key, list) -> {
            AtomicInteger counter = new AtomicInteger(0);
            list.forEach(item -> {
                section.set(key + "." + counter.get() + ".item", item.item);
                section.set(key + "." + counter.getAndIncrement() + ".chance", item.chance);
            });
        });
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!label.equalsIgnoreCase("chancedrop")) return false;

        Player player = ((Player) sender).getPlayer();
        assert player != null;

        CD_Object object = new CD_Object(player, args);
        if (object.ERROR_STATE) {
            sendMessage(player, object.ERROR_MESSAGE);
            return false;
        }

        List<CD_Object> blockList = playerData.get(args[1]);
        if (blockList == null) blockList = new ArrayList<>();

        if (args[0].equals("add")) {
            blockList.add(object);
            sendMessage(player, getConfig().getString("success-added-item"));
        }

        if (args[0].equals("list")) {
            sendMessage(player, " ");
            sendMessage(player, "&6===== " + normalize(args[1]) + " =====");
            for (int i=0; i<blockList.size(); i++) {
                CD_Object o = blockList.get(i);
                sendMessage(player, buildMessage(i+1, o.item.getType().name(), o.chance));
            }
        }

        if (args[0].equals("remove")) {
            int index = Integer.parseInt(args[2]);
            if (blockList.size() < index) sendMessage(player, getConfig().getString("error-out-of-bound-index"));
            else {
                blockList.remove(index-1);
                sendMessage(player, getConfig().getString("success-removed-item"));
            }
        }

        playerData.put(args[1], blockList);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!alias.equalsIgnoreCase("chancedrop")) return null;
        if (args.length == 0) return null;
        List<String> tab = new ArrayList<>();
        boolean add = args[0].equals("add"), remove = args[0].equals("remove"), list = args[0].equals("list");

        if (args.length == 1) tab = new ArrayList<String>(){{ add("add"); add("remove"); add("list"); }};
        if ((args.length == 2 || args.length == 3) && add) for (Material material : Material.values()) tab.add(material.name());
        if (args.length == 2 && (list || remove)) tab.addAll(playerData.keySet());
        if (args.length == 3 && add) tab.add(0,  "hand");
        if (args.length == 3 && remove) for (int i=0; i<playerData.getOrDefault(args[1], new ArrayList<>()).size(); i++) tab.add((i+1) + "");
        if (args.length == 4 && add) tab.add("chance_number");

        tab.removeIf(x -> !x.startsWith(args[args.length-1]));
        return tab;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!playerData.containsKey(event.getBlock().getType().name())) return;

        List<CD_Object> blockList = playerData.get(event.getBlock().getType().name());
        blockList.forEach(object -> {
            if (Math.random()*100 > object.chance) return;
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), object.item);
        });
    }

    private String buildMessage(int index, String item, int chance) {
        String message = getConfig().getString("list-format-text");
        assert message != null;
        return message.replace("{index}", index + "")
                .replace("{item}", item)
                .replace("{chance}", chance + "");
    }

    private String normalize(String text) {
        return text.replace("_", " ");
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
