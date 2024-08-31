package github.andredimaz.plugin.luckyblocks.commands.subcommands;

import github.andredimaz.plugin.luckyblocks.Main;
import github.andredimaz.plugin.luckyblocks.utils.LuckyBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {

    private final Main plugin;

    public GiveCommand(Main plugin) {
        this.plugin = plugin;
    }

    public void execute(Player sender, String[] args) {
        if (!sender.hasPermission("luckyblocks.admin")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando.");
            return;
        }

        if (args.length < 4) {
            sender.sendMessage("§cUso correto: /luckyblocks give <jogador> <tipo> <quantia>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJogador não encontrado.");
            return;
        }

        String type = args[2];
        int quantity;

        try {
            quantity = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cQuantidade inválida. Por favor, insira um número.");
            return;
        }

        FileConfiguration config = plugin.getLbConfigs(type + ".yml");

        if (config == null) {
            sender.sendMessage("§cTipo de Lucky Block não encontrado: " + type);
            return;
        }

        // Cria o valor NBT a partir do tipo
        String nbtValue = type + "-luckyblock";

        // Cria o item LuckyBlock com a quantidade embutida no nome e lore
        ItemStack luckyBlockItem = LuckyBuilder.create(config, nbtValue, quantity);

        if (luckyBlockItem == null) {
            sender.sendMessage("§cErro ao criar o Lucky Block do tipo: " + type);
            return;
        }

        // Configura o item para ter quantidade 1, mas com a quantidade real embutida no NBT e na lore
        luckyBlockItem.setAmount(1);

        target.getInventory().addItem(luckyBlockItem);

        sender.sendMessage("§aVocê deu " + quantity + " luckyblock(s) do tipo " + type + " para " + target.getName() + ".");
        target.sendMessage("§aVocê recebeu " + quantity + " luckyblock(s) do tipo " + type + ".");
    }
}
