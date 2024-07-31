package org.mrtexasfreedom.statsannouncer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

public class StatsAnnouncer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("StatsAnnouncer is starting up!");

        // Schedule the announcement task
        new BukkitRunnable() {
            @Override
            public void run() {
                announceRandomStat();
            }
        }.runTaskTimer(this, 0L, 20L * 60 * 15); // Run every 15 minutes
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("StatsAnnouncer is shutting down!");
    }


    private Player getRandomOnlinePlayer() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            return null;
        }
        return onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("randomstat")) {
            announceRandomStat();
            return true;
        }
        return false;
    }


    private void announceRandomStat() {
        Player randomPlayer = getRandomOnlinePlayer();
        if (randomPlayer == null) {
            getLogger().info("No players online to announce stats.");
            return;
        }

        String statistic = getRandomStatistic(randomPlayer);
        if (statistic != null) {
            Bukkit.broadcastMessage(String.format("Random stat: %s - %s", randomPlayer.getName(), statistic));
        } else {
            getLogger().warning("Failed to get statistics for " + randomPlayer.getName());
        }
    }


    private String getRandomStatistic(Player player) {
        try {
            File statsFile = new File(player.getWorld().getWorldFolder(), "stats/" + player.getUniqueId() + ".json");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(statsFile);
            // Example: Get a random dropped item
            //String randomDroppedItem = getRandomElement(rootNode, "minecraft:dropped");
            //System.out.println("Random Dropped Item: " + randomDroppedItem);

            // Example: Get a random killed entity
            String randomKilledEntity = getRandomElement(rootNode, "minecraft:killed");
           // System.out.println("Random Killed Entity: " + randomKilledEntity);


           // return String.format("%s: %s", randomStat, statValue.asText());
            return String.format("Minecraft:Killed: %s", randomKilledEntity);

        } catch (Exception e) {
            getLogger().warning("Error reading stats for " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    public static String getRandomElement(JsonNode rootNode, String key) {
        JsonNode statsNode = rootNode.path("stats").path(key);

        if (statsNode.isMissingNode() || !statsNode.isObject()) {
            return null;
        }

        Iterator<String> fieldNames = statsNode.fieldNames();
        List<String> elements = new ArrayList<>();

        while (fieldNames.hasNext()) {
            elements.add(fieldNames.next());
        }

        if (elements.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return elements.get(random.nextInt(elements.size()));
    }




}