package github.andredimaz.plugin.luckyblocks.utils;

import github.andredimaz.plugin.core.utils.basics.ColorUtils;
import github.andredimaz.plugin.core.utils.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class LuckyBuilder {

    public static ItemStack create(FileConfiguration config, String nbtValue, int amount) {
        String materialConfig = config.getString("item.material");

        String itemName = ColorUtils.colorize(config.getString("item.name", "Lucky Block").replace("{amount}", String.valueOf(amount)));

        List<String> itemLore = ColorUtils.colorize(config.getStringList("item.lore").stream()
                .map(line -> line.replace("{amount}", String.valueOf(amount)))
                .collect(Collectors.toList()));

        ItemBuilder itemBuilder;

        if (materialConfig.startsWith("http://textures.minecraft.net/texture/")) {
            String textureCode = materialConfig.replace("http://textures.minecraft.net/texture/", "");
            itemBuilder = new ItemBuilder(textureCode);

        } else {
            String[] parts = materialConfig.split(":");
            Material material;
            int data = 0;

            try {
                if (parts.length == 2) {
                    material = Material.getMaterial(parts[0].toUpperCase());
                    data = Integer.parseInt(parts[1]);
                } else {
                    material = Material.getMaterial(parts[0].toUpperCase());
                }

                if (material == null) {
                    throw new IllegalArgumentException("Material inválido: " + parts[0]);
                }

                itemBuilder = new ItemBuilder(material, data);

            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao criar o item com o material: " + materialConfig, e);
            }
        }

        itemBuilder.setDisplayName(itemName);
        if (!itemLore.isEmpty()) {
            itemBuilder.setLore(itemLore);
        }

        if (nbtValue != null && !nbtValue.isEmpty()) {
            itemBuilder.addNBT("luckyblock", nbtValue);
            itemBuilder.addNBT("luckyblock_amount", amount); // Armazena o valor da quantidade no NBT

        }

        itemBuilder.hideAttributes();
        return itemBuilder.build();
    }
}
