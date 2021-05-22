package net.seichi915.seichi915vote.inventory

import org.bukkit.inventory.{Inventory, InventoryHolder}

object Seichi915VoteInventoryHolder {
  val seichi915VoteInventoryHolder: Seichi915VoteInventoryHolder =
    new Seichi915VoteInventoryHolder {
      override def getInventory: Inventory = null
    }
}

trait Seichi915VoteInventoryHolder extends InventoryHolder
