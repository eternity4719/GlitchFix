package me.albert.glitchfix.fixes

import me.albert.corelib.utils.air
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object ItemDrop : Listener {

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

}