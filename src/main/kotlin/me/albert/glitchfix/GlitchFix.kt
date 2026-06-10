package me.albert.glitchfix

import com.github.retrooper.packetevents.PacketEvents
import me.albert.corelib.utils.*
import me.albert.glitchfix.fixes.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


lateinit var instance: GlitchFix


val config get() = instance.config

val debug get() = config.getBoolean("debug")

val logger get() = instance.logger

const val anvilKey = "anvil_name_key"

class GlitchFix : JavaPlugin() {


    override fun onEnable() {
        instance = this
        registerEvents(CommonFix)
        registerEvents(ItemDrop)
        registerEvents(AntiEmoji)
        registerEvents(GuiLock)
        registerEvents(McMMO)
        PacketEvents.getAPI().eventManager.registerListener(
            AntiEmoji
        )
        logger.info("${name} 已加载 ✅")
        saveDefaultConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (args.size) {
            1 -> {
                val sub = args[0].lowercase()
                when (sub) {
                    "anvil" -> {
                        if (sender !is Player) {
                            sender.sendMsg("§a玩家才能用")
                            return true
                        }

                        val item = sender.inventory.itemInMainHand
                        if (item.isNull) {
                            sender.sendMsg("§a拿东西啊")
                            return true
                        }
                        if (!item.hasItemMeta() || !item.itemMeta.hasPD(anvilKey)) {
                            sender.sendMsg("§a没有查询到命名者")
                            return true
                        }
                        val namer: String = item.itemMeta[anvilKey] ?: "未知"
                        sender.sendMsg("§a命名者为: ${namer}")
                        return true
                    }

                    "reload" -> {
                        reloadConfig()
                        sender.sendMsg("配置文件已经重新加载")
                        return true
                    }
                }
            }
        }
        sender.sendMsg("§a/glitchfix anvil - 查看铁砧命名者")
        sender.sendMsg("§a/glitchfix reload")
        return true
    }

    override fun onDisable() {
        PacketEvents.getAPI().eventManager.unregisterListener(AntiEmoji)
    }


}
