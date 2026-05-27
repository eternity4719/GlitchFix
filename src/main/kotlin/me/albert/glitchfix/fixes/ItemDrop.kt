package me.albert.glitchfix.fixes

import me.albert.glitchfix.scheduler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack

object ItemDrop : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) {
        val player = e.player
        if (player.isSneaking) {
            return
        }
        val item = e.itemDrop.itemStack.clone()

        if (item.isPrecious()) {
            e.isCancelled = true
            player.sendMessage("§7[§b系统§7] §a检测到当前物品过于贵重,请按Shift+Q进行丢弃!")
        }
        if (player.inventory.firstEmpty() == -1) {
            scheduler.runAtEntity(player) {
                if (!e.isCancelled) {
                    return@runAtEntity
                }
                if (player.itemOnCursor.isEmpty){
                    player.setItemOnCursor(item)
                }
            }
        }
    }

    private fun ItemStack.isPrecious(): Boolean {
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

        // 检查是否有任何附魔等级大于 5
        return enchants.values.any { it > 5 }
    }
}