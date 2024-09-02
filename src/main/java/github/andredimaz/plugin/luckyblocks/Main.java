package github.andredimaz.plugin.luckyblocks;

import github.andredimaz.plugin.luckyblocks.commands.LuckyBlocksCommand;
import github.andredimaz.plugin.luckyblocks.listeners.OnPlace;
import github.andredimaz.plugin.luckyblocks.utils.ASAnimation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin {
    private ASAnimation armorstand;

    private Map<String, FileConfiguration> lbConfigs = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadLuckyBlocks();

        getCommand("luckyblocks").setExecutor(new LuckyBlocksCommand(this));

        Bukkit.getPluginManager().registerEvents(new OnPlace(this), this);
    }

    @Override
    public void onDisable() {

    }

    public void loadLuckyBlocks() {
        File lbFolder = new File(getDataFolder(), "luckyblocks");

        if (!lbFolder.exists()) {
            lbFolder.mkdir();
        }

        for (File file : lbFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                lbConfigs.put(file.getName(), config);
            }
        }
    }

    public FileConfiguration getLbConfigs(String fileName) {
        return lbConfigs.get(fileName);
    }
}
