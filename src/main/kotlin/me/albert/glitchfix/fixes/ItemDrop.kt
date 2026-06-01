package me.albert.glitchfix.fixes

import me.albert.corelib.utils.air
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

object ItemDrop : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) {
        val player = e.player
        if (player.isSneaking) {
            return
        }
        val item = e.itemDrop.itemStack.clone()

        if (player.inventory.firstEmpty() == -1) {
            return
        }

        if (item.isPrecious()) {
            e.isCancelled = true
            player.sendMessage("§7[§b系统§7] §a检测到当前物品过于贵重,请按Shift+Q进行丢弃!")
        }
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player
        val cursor = player.itemOnCursor.clone()
        if (cursor.isEmpty) {
            return
        }
        player.setItemOnCursor(air)
        val inv = player.inventory
        if (inv.firstEmpty() == -1) {
            player.dropItem(cursor)
            return
        }
        inv.addItem(cursor)
    }

    fun ItemStack.isPrecious(): Boolean {
        // 使用 ?. 避免 ItemMeta 为空时崩溃，若为空则直接返回 false
        val item = this
        val meta = item.itemMeta ?: return false

        // 检查 Lore、无法破坏属性以及是否包含 "BOX" (如 Shulker Box)
        if (meta.hasLore() || meta.isUnbreakable || item.type.name.contains("BOX")) {
            return true
        }

        val enchants = item.enchantments
        if (enchants.size > 5) {
            return true
        }

        // 检查是否有任何附魔等级大于 3
        return enchants.values.any { it >= 3 }
    }
}