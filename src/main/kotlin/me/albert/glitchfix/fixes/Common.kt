package me.albert.glitchfix.fixes

import me.albert.corelib.utils.getMeta
import me.albert.corelib.utils.setMetadata
import me.albert.glitchfix.scheduler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.PluginDisableEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CommonFix : Listener {

    val joins = ConcurrentHashMap<UUID, Long>()

    // ===============================
    //           Events
    // ===============================
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (player.allowFlight && event.to.world != event.from.world) {
            scheduler.runAtEntity(player) {
                player.allowFlight = true
            }
        }
    }

    @EventHandler
    fun onPlayerMove(evt: PlayerMoveEvent) {
        val toLocation = evt.to
        if (toLocation.y > 500.0) {
            val player = evt.player
            player.sendMessage("${ChatColor.GREEN}你超过飞行高度. 限制高度 [${ChatColor.RED}500${ChatColor.GREEN}]")
            evt.to.y = 495.0
        }
    }

    @EventHandler
    fun onPluginDisable(e: PluginDisableEvent) {
        Bukkit.getOnlinePlayers().forEach { it.closeInventory() }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if (event.isCancelled && player.isGliding) {
            player.isGliding = false
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (player.isDead) player.spigot().respawn()
        player.vehicle?.eject()
    }

    @Synchronized
    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val uuid = event.player.uniqueId
        joins[uuid]?.let { last ->
            if (System.currentTimeMillis() - last < 1000) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§c登录频繁，请稍后再试！")
                return
            }
        }
        joins[uuid] = System.currentTimeMillis()
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        joins.remove(event.player.uniqueId)
        event.player.vehicle?.eject()
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onShulkerBreak(e: BlockBreakEvent) {
        val shulker = e.block.state as? ShulkerBox ?: return
        for (item in shulker.inventory) {
            if (item != null && item.type != Material.AIR &&
                item.amount > item.type.maxStackSize
            ) {
                e.isCancelled = true
                e.player.sendMessage("§c此潜影盒内含有超过最大堆叠数量的物品！请移除后再摧毁！")
                return
            }
        }
    }

    @EventHandler
    fun onStringShear(e: BlockFromToEvent) {
        if (e.toBlock.type == Material.TRIPWIRE) {
            e.isCancelled = true
            e.toBlock.type = Material.AIR
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onHit(event: ProjectileHitEvent) {
        val entity = event.entity
        val current = entity.getMeta("antiglitch_hit_check")?.asInt() ?: 0
        if (current >= 10) {
            entity.remove()
            return
        }
        entity.setMetadata("antiglitch_hit_check", current + 1)
    }

    @EventHandler(ignoreCancelled = true)
    fun onRename(event: PlayerInteractEntityEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (item.type == Material.NAME_TAG) {
            val name = event.rightClicked.customName ?: return
            if ('§' in name && !player.hasPermission("essentials.admin")) {
                event.isCancelled = true
            }
        }
    }
}