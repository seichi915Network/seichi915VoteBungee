package net.seichi915.seichi915vote.listener

import com.vexsoftware.votifier.model.VotifierEvent
import net.seichi915.seichi915vote.data.vote.VoteData
import net.seichi915.seichi915vote.event.VoteEvent
import org.bukkit.Bukkit
import org.bukkit.event.{EventHandler, Listener}

class VotifierListener extends Listener {
  @EventHandler
  def onVotifier(event: VotifierEvent): Unit = {
    val voteData =
      VoteData(
        event.getVote.getServiceName,
        event.getVote.getUsername,
        event.getVote.getAddress,
        event.getVote.getTimeStamp
      )
    val voteEvent = new VoteEvent(voteData)
    Bukkit.getPluginManager.callEvent(voteEvent)
  }
}
