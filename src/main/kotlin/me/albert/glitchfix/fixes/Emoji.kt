package me.albert.glitchfix.fixes

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.inventory.ItemStack
import java.util.regex.Pattern

object AntiEmoji : Listener {

    private val EMOJI_PATTERN = Pattern.compile("[^\\u0000-\\uFFFF]")

    @JvmStatic
    fun containsEmoji(source: String): Boolean {
        return EMOJI_PATTERN.matcher(source).find()
    }


    init {
        PacketEvents.getAPI().eventManager.registerListener(
            object : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {
                override fun onPacketReceive(event: PacketReceiveEvent) {
                    val player = event.getPlayer<Player>()

                    when (event.packetType) {
                        // 1. 监听聊天信息
                        PacketType.Play.Client.CHAT_MESSAGE -> {
                            val wrapper = WrapperPlayClientChatMessage(event)
                            if (containsEmoji(wrapper.message)) {
                                event.isCancelled = true
                                player.sendMessage("§c你的聊天信息包含非法字符,发送失败")
                            }
                        }

                        // 2. 监听动态指令（可选：防止通过 /me, /msg 发送非法字符）
                        PacketType.Play.Client.CHAT_COMMAND -> {
                            val wrapper = WrapperPlayClientChatCommand(event)
                            if (containsEmoji(wrapper.command)) {
                                event.isCancelled = true
                                player.sendMessage("§c你的指令包含非法字符,发送失败")
                            }
                        }

                        PacketType.Play.Client.EDIT_BOOK -> {
                            val wrapper = WrapperPlayClientEditBook(event)

                        }

                        // 3. 监听告示牌更新
                        PacketType.Play.Client.UPDATE_SIGN -> {
                            val wrapper = WrapperPlayClientUpdateSign(event)
                            // 将所有行拼接成一个字符串进行检测
                            val allLines = wrapper.textLines.joinToString("\n")
                            if (containsEmoji(allLines)) {
                                event.isCancelled = true
                                player.sendMessage("§c你的告示牌信息包含非法字符,更新失败")
                            }
                        }
                    }
                }
            }
        )


    }


    @EventHandler
    fun onBook(event: PlayerEditBookEvent) {
        val meta = event.newBookMeta

        if (meta.hasTitle() && containsEmoji(meta.title!!)) {
            event.isCancelled = true
            event.player.sendMessage("§c你的书本标题包含非法字符!")
            clearBook(event.player)
            return
        }

        if (containsEmoji(meta.pages.toString())) {
            event.isCancelled = true
            event.player.sendMessage("§c你的书本内容包含非法字符!")
            clearBook(event.player)
        }
    }

    @EventHandler
    fun onRename(e: InventoryClickEvent) {
        if (e.inventory.type == InventoryType.ANVIL) {
            val oldItem = e.inventory.getItem(0)
            val newItem = e.currentItem

            if (isNullItem(newItem) || isNullItem(oldItem)) return

            val oldMeta = oldItem!!.itemMeta
            val meta = newItem!!.itemMeta ?: return

            if (meta.hasDisplayName() && containsEmoji(meta.displayName)) {
                meta.setDisplayName(oldMeta?.displayName)
                newItem.itemMeta = meta
            }
        }
    }

    private fun isNullItem(item: ItemStack?): Boolean {
        return item == null || item.type == Material.AIR
    }

    private fun clearBook(player: Player) {
        val inventory = player.inventory
        val mainHand = inventory.itemInMainHand
        val offHand = inventory.itemInOffHand

        if (!isNullItem(mainHand) && (mainHand.type == Material.WRITTEN_BOOK || mainHand.type == Material.WRITABLE_BOOK)) {
            inventory.setItemInMainHand(ItemStack(Material.AIR))
        }
        if (!isNullItem(offHand) && (offHand.type == Material.WRITTEN_BOOK || offHand.type == Material.WRITABLE_BOOK)) {
            inventory.setItemInOffHand(ItemStack(Material.AIR))
        }
    }
}