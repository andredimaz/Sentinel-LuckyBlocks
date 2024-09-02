package github.andredimaz.plugin.luckyblocks.listeners;

import github.andredimaz.plugin.core.utils.objects.ItemBuilder;
import github.andredimaz.plugin.luckyblocks.Main;
import github.andredimaz.plugin.luckyblocks.utils.ASAnimation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class OnPlace implements Listener {

    private final Main plugin;
    private final ASAnimation armorstand;

    public OnPlace(Main plugin) {
        this.plugin = plugin;
        this.armorstand = new ASAnimation(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (item != null && item.hasItemMeta()) {
            ItemBuilder itemBuilder = new ItemBuilder(item);

            String luckyBlockNBT = itemBuilder.getNBT("luckyblock");
            if (luckyBlockNBT != null) {
                String amountNBT = itemBuilder.getNBT("luckyblock_amount");
                int amount = (amountNBT != null) ? Integer.parseInt(amountNBT) : 1;

                if (amount > 1) {
                    amount--;
                    itemBuilder.addNBT("luckyblock_amount", amount);

                    armorstand.placeAnim(event.getPlayer(), event.getBlockPlaced().getLocation().add(0.5, -1.0, 0.5), item, 5.0, 0.5, 5, 0.015);


                    if (itemBuilder.getLore() != null) {
                        itemBuilder.setDisplayName(item.getItemMeta().getDisplayName().replace(String.valueOf(amount + 1), String.valueOf(amount)));
                    }

                    event.getPlayer().setItemInHand(itemBuilder.build());
                } else {
                    event.getPlayer().setItemInHand(null);
                }

                // Cancel the event to prevent the block from being placed
                event.setCancelled(true);
            }
        }
    }
}