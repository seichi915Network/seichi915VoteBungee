package net.seichi915.seichi915vote.listener

import net.seichi915.seichi915vote.Seichi915Vote
import net.seichi915.seichi915vote.util.Implicits._
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{ClickType, InventoryClickEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID

class InventoryClickListener extends Listener {
  @EventHandler
  def onInventoryClick(event: InventoryClickEvent): Unit = {
    if (!event.getInventory.isSeichi915VoteInventory) return
    if (event.getCurrentItem.isNull) return
    if (!event.getWhoClicked.isInstanceOf[Player]) return
    event.setCancelled(true)
    event.getClick match {
      case ClickType.LEFT =>
        val uuidString =
          event.getCurrentItem.getItemMeta.getPersistentDataContainer.get(
            new NamespacedKey(Seichi915Vote.instance, "click_action"),
            PersistentDataType.STRING
          )
        if (uuidString.isNull) return
        val uuid = UUID.fromString(uuidString)
        val clickAction = Seichi915Vote.clickActionMap(uuid)
        clickAction.onClick(event.getWhoClicked.asInstanceOf[Player])
      case ClickType.RIGHT =>
        val uuidString =
          event.getCurrentItem.getItemMeta.getPersistentDataContainer.get(
            new NamespacedKey(Seichi915Vote.instance, "right_click_action"),
            PersistentDataType.STRING
          )
        if (uuidString.isNull) return
        val uuid = UUID.fromString(uuidString)
        val clickAction = Seichi915Vote.rightClickActionMap(uuid)
        clickAction.onClick(event.getWhoClicked.asInstanceOf[Player])
      case _ =>
    }
  }
}
