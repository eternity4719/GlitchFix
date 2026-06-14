package me.albert.glitchfix.fixes

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import me.albert.corelib.utils.isNull
import me.albert.corelib.utils.sendMsg
import me.albert.corelib.utils.set
import me.albert.glitchfix.anvilKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerEditBookEvent
import java.util.regex.Pattern

object AntiEmoji : Listener, PacketListenerAbstract(PacketListenerPriority.HIGHEST) {

    private val EMOJI_PATTERN = Pattern.compile("[^\\u0000-\\uFFFF]")

    @JvmStatic
    fun containsEmoji(source: String): Boolean {
        return EMOJI_PATTERN.matcher(source).find()
    }

    fun String?.hasEmoji(): Boolean {
        return this != null && containsEmoji(this)
    }


    @EventHandler
    fun onBook(event: PlayerEditBookEvent) {
        val meta = event.newBookMeta

        if (meta.hasTitle() && containsEmoji(meta.title!!)) {
            event.isCancelled = true
            event.player.sendMsg("§c你的书本标题包含非法字符!")
            return
        }

        if (containsEmoji(meta.pages.toString())) {
            event.isCancelled = true
            event.player.sendMsg("§c你的书本内容包含非法字符!")
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onRename(e: InventoryClickEvent) {
        if (e.inventory.type == InventoryType.ANVIL) {
            val oldItem = e.inventory.getItem(0)
            val newItem = e.currentItem

            if (newItem.isNull || oldItem.isNull) return
            newItem!!
            oldItem!!
            val oldMeta = oldItem.itemMeta
            if (oldMeta?.displayName?.contains("§") == true) {
                newItem.editMeta {
                    it.setDisplayName(oldMeta.displayName)
                }
                return
            }
            newItem.editMeta {
                it[anvilKey] = e.whoClicked.name
            }
        }
    }


}