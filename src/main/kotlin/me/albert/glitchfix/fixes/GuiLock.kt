package me.albert.glitchfix.fixes

import io.papermc.paper.event.player.AsyncChatEvent
import me.albert.corelib.utils.sendMsg
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object GuiLock : Listener {

    /**
     * 判断玩家是否打开了某个界面。
     * 没有打开任何容器时，topInventory 的类型是 CRAFTING（玩家默认视图），
     * 所以这里把 CRAFTING 视为"未打开界面"。
     */
    private fun Player.hasOpenInterface(): Boolean {
        return openInventory.topInventory.type != InventoryType.CRAFTING
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.player.hasOpenInterface()) {
            event.isCancelled = true
            event.player.sendActionBar("§c打开界面时无法进行交互!")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onInteractEntity(event: PlayerInteractEntityEvent) {
        if (event.player.hasOpenInterface()) {
            event.isCancelled = true
            event.player.sendActionBar("§c打开界面时无法进行交互!")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        if (player.hasOpenInterface()) {
            event.isCancelled = true
            player.sendMsg("§c打开界面时无法发送消息!")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (player.hasOpenInterface()) {
            event.isCancelled = true
            player.sendMsg("§c打开界面时无法执行指令!")
        }
    }
}