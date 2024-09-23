package io.github.pikapikagfy.mcBossEngine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MCBossEngine extends JavaPlugin {
    private ConfigReader configReader;
    private BossConfig bossConfig;
    private SkillConfig skillConfig;
    private BossBuilder bossBuilder;

    @Override
    public void onEnable() {
        // 加载配置文件
        this.saveDefaultConfig();  // 确保默认配置加载
        configReader = new ConfigReader(this);  // 传入插件实例

        bossConfig = configReader.loadBossConfig();
        skillConfig = configReader.loadSkillConfig();

        // 初始化 BossBuilder
        bossBuilder = new BossBuilder(bossConfig);

        getLogger().info("BossEngine plugin enabled!");
    }
    @Override
    public void onDisable() {
        getLogger().info("BossEngine plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // 检查玩家是否有权限执行该命令
                if (!player.hasPermission("bossengine.spawn")) {
                    player.sendMessage("你没有权限执行此命令！");
                    return true;
                }

                if (args.length > 0) {
                    String bossName = args[0];  // 获取传递的 Boss 名字
                    bossBuilder.buildBoss(bossName, player.getLocation());  // 在玩家位置生成 Boss
                    player.sendMessage("Boss " + bossName + " 已生成！");
                    return true;
                } else {
                    player.sendMessage("请提供要生成的 Boss 名字！");
                    return false;
                }
            } else {
                sender.sendMessage("只有玩家可以执行此命令！");
                return true;
            }
        }
        return false;
    }
}