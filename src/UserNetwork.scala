package khanacademy.src;

class UserNetwork {
  private var version = Version(0, 0, 0);
  private var newVersionOpt = Option.empty[Version];
  private var numUpdated = 0;
  private var networkSize = 0;
  private var updatedSubnets = Vector.empty[Set[User]];
  private var untouchedSubnets = Vector.empty[Set[User]];

  def getNumUpdated: Int = numUpdated;
  def getNetworkSize: Int = networkSize;
  def getPercentUpdated: Int = numUpdated * 100 / networkSize;
  def getVersion: Version = version;
  def readyForNewVersion: Boolean = newVersionOpt.isEmpty;

  // Signify that we would like to start rolling out a new version
  def prepareVersion(newVersion: Version): Boolean = {
    if (readyForNewVersion && newVersion > version) {
      newVersionOpt = Some(newVersion);
      return true;
    } else {
      return false;
    }
  }
  
  def updateNetworkPercentage(percentage: Int) {
    updateNetwork(math.min(math.max(percentage, 0), 100) * networkSize / 100);
  }

  def updateNetwork(numToUpdate: Int, requireExactMatch: Boolean = false) {
    if (numUpdated < numToUpdate) {
      val newUpdates = numToUpdate - numUpdated;
      val subnetsToUpdate = chooseSubnetsToUpdate(
        untouchedSubnets
          .map(_.size)
          .zipWithIndex
          .sortBy(-_._1)
          .dropWhile(_._1 > newUpdates)
          .toList,
        newUpdates
      ).map(untouchedSubnets.apply)
      if (requireExactMatch) {
        if (subnetsToUpdate.map(_.size).sum != newUpdates) {
          return; // not an exact match
        }
      }
      untouchedSubnets = untouchedSubnets.diff(subnetsToUpdate)
      updatedSubnets = subnetsToUpdate ++: updatedSubnets
      for {
        subnet <- subnetsToUpdate
        user <- subnet.headOption
        newVersion <- newVersionOpt
      } {
        user.updateVersion(newVersion);
      }
      numUpdated += subnetsToUpdate.map(_.size).sum;
      if (numUpdated == networkSize) {
        newVersionOpt.foreach(newVersion => {
          numUpdated = 0;
          version = newVersion;
          newVersionOpt = None;
          untouchedSubnets = updatedSubnets;
          updatedSubnets = Vector.empty[Set[User]];
        });
      }
    }

    // aliases made available to conform to spec
    def total_infection = updateNetworkPercentage(100);
    def limited_infection(
      numToUpdate: Int,
      requireExactMatch: Boolean = false
    ) = updateNetwork(numToUpdate, requireExactMatch);
  }

  private[src] def add(user: User) {
    untouchedSubnets = Set(user) +: untouchedSubnets;
    networkSize += 1;
    user.updateVersion(version);
  }
  
  private[src] def connect(user1: User, user2: User) {
    val (updatedSubnetsWithUsers, updatedSubnetsWithoutUsers) = {
      updatedSubnets.partition(subnet =>
        subnet.contains(user1) || subnet.contains(user2));
    }
    val (untouchedSubnetsWithUsers, untouchedSubnetsWithoutUsers) = {
      untouchedSubnets.partition(subnet =>
        subnet.contains(user1) || subnet.contains(user2));
    }
    val updatedUserSubnet = updatedSubnetsWithUsers.toSet.flatten;
    val untouchedUserSubnet = untouchedSubnetsWithUsers.toSet.flatten;
    val userSubnetUnion = updatedUserSubnet | untouchedUserSubnet;
    if (updatedUserSubnet.nonEmpty) {
      for {
        subnet <- untouchedSubnetsWithUsers
        user <- subnet.headOption
        newVersion <- newVersionOpt
      } {
        user.updateVersion(newVersion);
      }
      updatedSubnets = userSubnetUnion +: updatedSubnetsWithoutUsers;
      untouchedSubnets = untouchedSubnetsWithoutUsers;
      numUpdated += untouchedUserSubnet.size;
      if (numUpdated == networkSize) {
        newVersionOpt.foreach(newVersion => {
          numUpdated = 0;
          version = newVersion;
          newVersionOpt = None;
          untouchedSubnets = updatedSubnets;
          updatedSubnets = Vector.empty[Set[User]];
        });
      }
    } else {
      updatedSubnets = updatedSubnetsWithoutUsers;
      untouchedSubnets = userSubnetUnion +: untouchedSubnetsWithoutUsers;
    }
  }

  private def chooseSubnetsToUpdate(
    subnetSizes: List[(Int, Int)],
    newUpdates: Int
  ): List[Int] = {
    def _chooseSubnetsToUpdate(
      subnetSizes: List[(Int, Int)],
      updatesCount: Int
    ): Option[(List[Int], Int)] = {
      // base case 1: reached the end of our subnets
      if (subnetSizes.isEmpty) {
        return Some((Nil, updatesCount));
      // base case 2: exact fit
      } else if (updatesCount + subnetSizes.head._1 == newUpdates) {
        return Some((List(subnetSizes.head._2), newUpdates));
      } else {
        var subnetSequenceOpt1 = Option.empty[(List[Int], Int)];
        var subnetSequenceOpt2 = Option.empty[(List[Int], Int)];
        if (updatesCount + subnetSizes.head._1 < newUpdates) {
          // get the best sequence we can make while including this subnet
          subnetSequenceOpt1 = _chooseSubnetsToUpdate(
            subnetSizes.tail,
            updatesCount + subnetSizes.head._1
          ).map(tup => (subnetSizes.head._2 +: tup._1, tup._2));
        }
        // if exact fit, we choose this sequence and skip the next check
        if (subnetSequenceOpt1.exists(_._2 == newUpdates)) {
          return subnetSequenceOpt1;
        }
        // get the best sequence we can make while excluding this subnet
        subnetSequenceOpt2 = _chooseSubnetsToUpdate(
          subnetSizes.tail,
          updatesCount
        );
        // choose the better of the two sequences we got
        (subnetSequenceOpt1, subnetSequenceOpt2) match {
          case (None, opt2) => return opt2;
          case (opt1, None) => return opt1;
          case (opt1 @ Some((_, count1)), opt2 @ Some((_, count2))) => {
            return if (count1 > count2) opt1 else opt2;
          }
        }
      }
    }
    return _chooseSubnetsToUpdate(subnetSizes, 0).map(_._1).getOrElse(Nil);
  }
}
