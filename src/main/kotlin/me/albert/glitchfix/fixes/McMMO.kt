package me.albert.glitchfix.fixes

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent
import io.papermc.paper.event.player.AsyncChatEvent
import me.albert.corelib.utils.hasPD
import me.albert.corelib.utils.isNull
import me.albert.corelib.utils.sendMsg
import me.albert.glitchfix.fixes.ItemDrop.isPrecious
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack


object McMMO : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onSalvage(event: McMMOPlayerSalvageCheckEvent) {
        val player: Player = event.getPlayer()
        if (event.salvageItem.isPrecious() && !player.isSneaking) {
            event.isCancelled = true
            player.sendMsg("§b检测到当前物品过于珍贵,请按下Shift重新进行分解！")
        }
    }

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