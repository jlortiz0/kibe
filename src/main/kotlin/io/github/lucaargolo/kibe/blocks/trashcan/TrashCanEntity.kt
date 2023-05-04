package io.github.lucaargolo.kibe.blocks.trashcan

import io.github.lucaargolo.kibe.blocks.getEntityType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.LockableContainerBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class TrashCanEntity(trashCan: TrashCan, pos: BlockPos, state: BlockState): LockableContainerBlockEntity(getEntityType(trashCan), pos, state),
    SidedInventory {

    val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)

    override fun createScreenHandler(i: Int, playerInventory: PlayerInventory?): ScreenHandler? {
        return null
    }

    override fun size() = 3


    override fun isEmpty() = inventory.all { it.isEmpty }

    override fun getStack(slot: Int): ItemStack {
        return if (slot == 1) {
            inventory[0]
        } else {
            ItemStack.EMPTY
        }
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return if (slot == 1) {
            Inventories.splitStack(this.inventory, 0, amount)
        } else {
            ItemStack.EMPTY
        }
    }

    override fun removeStack(slot: Int): ItemStack {
        return if (slot == 1) {
            Inventories.removeStack(this.inventory, 0)
        } else {
            ItemStack.EMPTY
        }
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        if (slot < 2)
            inventory[0] = stack
    }

    override fun clear() {
        inventory.clear()
    }

    override fun getContainerName(): Text = TranslatableText("screen.kibe.trash_can")

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        return if (world!!.getBlockEntity(pos) != this) {
            false
        } else {
            player!!.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) <= 64.0
        }
    }

    override fun onClose(player: PlayerEntity?) {
        clear()
    }

    override fun getAvailableSlots(side: Direction?): IntArray {
        return IntArray(1) { 2 }
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return dir == null && slot == 1;
    }

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return slot != 1 || dir == null;
    }
}