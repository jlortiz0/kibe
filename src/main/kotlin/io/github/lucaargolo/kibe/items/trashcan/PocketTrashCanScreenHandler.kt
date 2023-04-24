package io.github.lucaargolo.kibe.items.trashcan

import io.github.lucaargolo.kibe.items.POCKET_TRASH_CAN
import io.github.lucaargolo.kibe.items.getContainerInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

@Suppress("UNUSED_PARAMETER")
class PocketTrashCanScreenHandler(syncId: Int, playerInventory: PlayerInventory, hand: Hand, val world: World, tag: NbtCompound?): ScreenHandler(getContainerInfo(POCKET_TRASH_CAN)?.handlerType, syncId) {

    val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)

    private var synchronizedInventory: Inventory = object: Inventory {

        override fun markDirty() {
            return
        }

        override fun clear() {
            inventory.clear()
        }

        override fun setStack(slot: Int, stack: ItemStack?) {
            inventory[0] = stack
        }

        override fun isEmpty() = inventory.all { it.isEmpty }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            return if (slot == 1) {
                Inventories.splitStack(inventory, 0, amount)
            } else {
                ItemStack.EMPTY
            }
        }

        override fun removeStack(slot: Int): ItemStack {
            return if (slot == 1) {
                Inventories.removeStack(inventory, 0)
            } else {
                ItemStack.EMPTY
            }
        }

        override fun getStack(slot: Int): ItemStack {
            return if (slot == 1) {
                inventory[0]
            } else {
                ItemStack.EMPTY
            }
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return true
        }

        override fun size() = 2

        override fun onClose(player: PlayerEntity?) {
            clear()
        }
    }

    init {
        checkSize(synchronizedInventory, 2)
        synchronizedInventory.onOpen(playerInventory.player)
        val i: Int = (3 - 4) * 18

        addSlot(Slot(synchronizedInventory, 0, -36727,  -36727))
        addSlot(Slot(synchronizedInventory, 1, 8 + 4*18,  36))

        (0..2).forEach {n ->
            (0..8).forEach { m ->
                addSlot(Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i))
            }
        }

        (0..8).forEach { n ->
            addSlot(Slot(playerInventory, n, 8 + n * 18, 161 + i))
        }

    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
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

    override fun close(player: PlayerEntity?) {
        super.close(player)
        synchronizedInventory.onClose(player)
    }

}