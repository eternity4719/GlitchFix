package me.albert.glitchfix.fixes

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent

object PistonOre : Listener {

    private fun isOre(block: Block): Boolean {
        val type = block.type
        // 原版矿石均以 _ORE 结尾，远古残骸是唯一例外；新版本新增矿石可自动覆盖
        return type.name.endsWith("_ORE") || type == Material.ANCIENT_DEBRIS
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onExtend(event: BlockPistonExtendEvent) {
        event.isCancelled = event.blocks.any(::isOre)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onRetract(event: BlockPistonRetractEvent) {
        event.isCancelled = event.blocks.any(::isOre)
    }

}
