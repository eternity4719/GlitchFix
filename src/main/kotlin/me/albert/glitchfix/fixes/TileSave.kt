package me.albert.glitchfix.fixes

import me.albert.corelib.utils.get
import me.albert.corelib.utils.hasPD
import me.albert.corelib.utils.set
import org.bukkit.block.TileState
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack

object TileSave : Listener {
    const val SAVE_KEY = "tile_item_data"

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.block
        val state = block.state
        if (state !is TileState) {
            return
        }
        val item = event.itemInHand
        if (!item.hasItemMeta()) {
            return
        }
        val itemData = item.serializeAsBytes()
        state[SAVE_KEY] = itemData
        state.update(true, false)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(event: BlockDropItemEvent) {
        val state = event.blockState
        if (state !is TileState) {
            return
        }
        if (!state.hasPD(SAVE_KEY)) {
            return
        }
        val itemToDrop = ItemStack.deserializeBytes(state[SAVE_KEY]!!)
        itemToDrop.amount = 1
        val items = event.items
        if (items.size != 1) {
            return
        }
        val item = items.first()
        if (item.itemStack.type != itemToDrop.type) {
            return
        }
        item.itemStack = itemToDrop
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun protectWater(event: BlockFromToEvent) {
        val block = event.toBlock
        val state = block.state
        if (state !is TileState) {
            return
        }
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun protectPiston(event: BlockPistonExtendEvent) {
        event.isCancelled = event.blocks.any { it.state is TileState }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun protectPistonRe(event: BlockPistonRetractEvent) {
        event.isCancelled = event.blocks.any { it.state is TileState }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun protectExplode(event: BlockExplodeEvent) {
        event.blockList().removeAll { it.state is TileState }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun protectExplodeEntity(event: EntityExplodeEvent) {
        event.blockList().removeAll { it.state is TileState }
    }

}