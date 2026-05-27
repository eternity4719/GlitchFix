package me.albert.glitchfix.fixes

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign
import me.albert.corelib.utils.isNull
import me.albert.corelib.utils.sendMsg
import me.albert.corelib.utils.set
import me.albert.glitchfix.anvilKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
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

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.getPlayer<Any>() as? Player ?: return

        when (event.packetType) {
            // 1. 监听聊天信息
            PacketType.Play.Client.CHAT_MESSAGE -> {
                val wrapper = WrapperPlayClientChatMessage(event)
                if (containsEmoji(wrapper.message)) {
                    event.isCancelled = true
                    player.sendMsg("§c你的聊天信息包含非法字符,发送失败")
                }
            }

            // 2. 监听动态指令（可选：防止通过 /me, /msg 发送非法字符）
            PacketType.Play.Client.CHAT_COMMAND -> {
                val wrapper = WrapperPlayClientChatCommand(event)
                if (containsEmoji(wrapper.command)) {
                    event.isCancelled = true
                    player.sendMsg("§c你的指令包含非法字符,发送失败")
                }
            }

            // 3. 监听告示牌更新
            PacketType.Play.Client.UPDATE_SIGN -> {
                val wrapper = WrapperPlayClientUpdateSign(event)
                // 将所有行拼接成一个字符串进行检测
                val allLines = wrapper.textLines.joinToString("\n")
                if (containsEmoji(allLines)) {
                    event.isCancelled = true
                    player.sendMsg("§c你的告示牌信息包含非法字符,更新失败")
                }
            }
        }
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

    @EventHandler
    fun onRename(e: InventoryClickEvent) {
        if (e.inventory.type == InventoryType.ANVIL) {
            val oldItem = e.inventory.getItem(0)
            val newItem = e.currentItem

            if (newItem.isNull || newItem.isNull) return

            val oldMeta = oldItem?.itemMeta
            val meta = newItem?.itemMeta ?: return
            newItem.editMeta {
                it[anvilKey] = e.whoClicked.name
            }
            if (meta.displayName.hasEmoji() || oldMeta?.displayName?.contains("§") == true) {
                meta.setDisplayName(oldMeta?.displayName)
                newItem.itemMeta = meta
            }
        }
    }


}