package io.github.lucaargolo.kibe.blocks.trashcan

import io.github.lucaargolo.kibe.blocks.TRASH_CAN
import io.github.lucaargolo.kibe.blocks.getContainerInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TrashCanScreenHandler(syncId: Int, playerInventory: PlayerInventory, val entity: TrashCanEntity, private val blockContext: ScreenHandlerContext): ScreenHandler(getContainerInfo(TRASH_CAN)?.handlerType, syncId) {

    var inventory: Inventory = object: Inventory {
        override fun size(): Int {
            return entity.size()
        }

        override fun isEmpty(): Boolean {
            return entity.isEmpty
        }

        override fun getStack(slot: Int): ItemStack {
            return entity.getStack(slot)
        }

        override fun removeStack(slot: Int): ItemStack {
            val stack: ItemStack = entity.removeStack(slot)
            onContentChanged(this)
            return stack
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            val stack: ItemStack = entity.removeStack(slot, amount)
            onContentChanged(this)
            return stack
        }

        override fun setStack(slot: Int, stack: ItemStack?) {
            entity.setStack(slot, stack)
            onContentChanged(this)
        }

        override fun markDirty() {
            entity.markDirty()
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return entity.canPlayerUse(player)
        }

        override fun clear() {
            entity.clear()
        }

        override fun onClose(player: PlayerEntity?) {
            clear()
        }
    }

    init {
        checkSize(inventory, 1)
        inventory.onOpen(playerInventory.player)
        val i: Int = (3 - 4) * 18

        addSlot(Slot(inventory, 0, -36727,  -36727))
        addSlot(Slot(inventory, 1, 8 + 4*18,  36))

        (0..2).forEach {n ->
            (0..8).forEach { m ->
                addSlot(
                    Slot(
                        playerInventory,
                        m + n * 9 + 9,
                        8 + m * 18,
                        103 + n * 18 + i
                    )
                )
            }
        }

        (0..8).forEach { n ->
            addSlot(Slot(playerInventory, n, 8 + n * 18, 161 + i))
        }

    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        inventory.onClose(player)
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {
        if (!cursorStack.isEmpty && slotIndex == 1 && actionType == SlotActionType.PICKUP && (button == 0 || button == 1)) {
            inventory.removeStack(1)
        }
        super.onSlotClick(slotIndex, button, actionType, player)
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return blockContext.get({ world: World, blockPos: BlockPos ->
            if (world.getBlockState(
                    blockPos
                ).block != TRASH_CAN
            ) false else player.squaredDistanceTo(
                blockPos.x + .5,
                blockPos.y + .5,
                blockPos.z + .5
            ) < 64.0
        }, true)
    }

    override fun transferSlot(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[invSlot]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (invSlot < 1) {
                if (!insertItem(itemStack2, 1, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(itemStack2, 0, 1, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return itemStack
    }

}