package github.andredimaz.plugin.luckyblocks.commands;

import github.andredimaz.plugin.luckyblocks.Main;
import github.andredimaz.plugin.luckyblocks.commands.subcommands.GiveCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LuckyBlocksCommand implements CommandExecutor {

    private final GiveCommand giveCommand;

    public LuckyBlocksCommand(Main plugin) {
        this.giveCommand = new GiveCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§eLista de comandos:");
            return true;
        }

        // Verifique se o comando foi executado por um jogador
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser usado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "give":
                giveCommand.execute(player, args);
                break;
            default:
                sender.sendMessage("§cComando inválido. Use /luckyblocks help para ver a lista de comandos.");
                break;
        }
        return true;
    }
}
