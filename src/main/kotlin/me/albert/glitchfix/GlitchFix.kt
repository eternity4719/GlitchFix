package me.albert.glitchfix

import com.github.retrooper.packetevents.PacketEvents
import com.tcoded.folialib.FoliaLib
import com.tcoded.folialib.impl.PlatformScheduler
import me.albert.corelib.utils.registerEvents
import me.albert.glitchfix.fixes.AntiEmoji
import me.albert.glitchfix.fixes.CommonFix
import me.albert.glitchfix.fixes.ItemDrop
import org.bukkit.plugin.java.JavaPlugin


lateinit var instance: GlitchFix

lateinit var foliaLib: FoliaLib

val scheduler: PlatformScheduler get() = foliaLib.scheduler

val config get() = instance.config

val debug get() = config.getBoolean("debug")

val logger get() = instance.logger

class GlitchFix : JavaPlugin() {


    override fun onEnable() {
        instance = this
        foliaLib = FoliaLib(this)
        registerEvents(CommonFix)
        registerEvents(ItemDrop)
        registerEvents(AntiEmoji)
        PacketEvents.getAPI().eventManager.registerListener(
            AntiEmoji
        )
        logger.info("GlitchFix 已加载 ✅")
        saveDefaultConfig()
    }

    override fun onDisable() {
        PacketEvents.getAPI().eventManager.unregisterListener(AntiEmoji)
    }


}
