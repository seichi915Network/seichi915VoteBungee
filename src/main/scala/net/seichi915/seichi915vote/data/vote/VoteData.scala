package net.seichi915.seichi915vote.data.vote

case class VoteData(
    serviceName: String,
    username: String,
    address: String,
    timeStamp: String
) {
  def getServiceName: String = serviceName

  def getUsername: String = username

  def getAddress: String = address

  def getTimeStamp: String = timeStamp

  override def toString: String =
    s"From: $getServiceName, Username: $getUsername, Address: $getAddress, TimeStamp: $getTimeStamp"
}
