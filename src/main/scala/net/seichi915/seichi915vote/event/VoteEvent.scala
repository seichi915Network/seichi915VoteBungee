package net.seichi915.seichi915vote.event

import net.seichi915.seichi915vote.data.vote.VoteData
import org.bukkit.event.{Event, HandlerList}

object VoteEvent {
  private val handlerList = new HandlerList

  def getHandlerList: HandlerList = handlerList
}

class VoteEvent(voteData: VoteData) extends Event {
  def getVoteData: VoteData = voteData

  override def getHandlers: HandlerList = VoteEvent.getHandlerList
}
