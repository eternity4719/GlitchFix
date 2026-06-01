package me.albert.glitchfix.fixes

import io.papermc.paper.event.player.AsyncChatEvent
import me.albert.corelib.utils.hasPD
import me.albert.corelib.utils.isNull
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack


object McMMO : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        val player: Player = event.getPlayer()
        val hand = player.inventory.itemInMainHand
        if (hand.isNull) {
            return
        }
        if (this.isInAbility(hand)) {
            event.isCancelled = true
            player.sendMessage("""§7[§b系统§7] §c你在技能释放期间不能进行聊天!""")
        }
    }


    fun isInAbility(item: ItemStack): Boolean {
        return item.itemMeta?.hasPD("super_ability_boosted") == true
    }

}