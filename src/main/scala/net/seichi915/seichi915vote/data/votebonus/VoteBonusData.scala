package net.seichi915.seichi915vote.data.votebonus

import org.bukkit.inventory.ItemStack

case class VoteBonusData(name: String, itemStack: ItemStack, count: Int) {
  def getName: String = name

  def getItemStack: ItemStack = itemStack

  def getCount: Int = count
}
