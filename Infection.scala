import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.Queue;

class User(val name: String) {
  override def toString: String = name;
  private val _teachers = HashSet.empty[User];
  private val _students = HashSet.empty[User];
  private var _version = 0;
  def teachers: List[User] = _teachers.toList;
  def students: List[User] = _students.toList;
  def version: Int = _version;
  def addStudent(student: User) {
    if (student != this && !students.contains(student)) {
      _students += student;
      student.addTeacher(this);
    }
  }
  def addTeacher(teacher: User) {
    if (teacher != this && !teachers.contains(teacher)) {
      _teachers += teacher;
      teacher.addStudent(this);
    }
  }
  def updateVersion(newVersion: Int) {
    if (newVersion != version) {
      _version = newVersion;
      teachers.foreach(_.updateVersion(newVersion));
      students.foreach(_.updateVersion(newVersion));
    }
  }
}

class NetworkInfector(network: Set[User], version: Int) {
  private var numInfected = 0;
  private var uninfectedSubnets = {
    var tempNetwork = network;
    val subnets = ListBuffer.empty[Set[User]];
    while (tempNetwork.nonEmpty) {
      val subnet = HashSet.empty[User];
      val userQueue = Queue(tempNetwork.head);
      while (userQueue.nonEmpty) {
        val user = userQueue.dequeue();
        if (!subnet.contains(user)) {
          subnet += user;
          userQueue ++= user.teachers.filterNot(subnet.contains);
          userQueue ++= user.students.filterNot(subnet.contains);  
        }
      }
      subnets += subnet.toSet;
      tempNetwork --= subnet;
    }
    subnets.sortBy(-_.size).toList;
  }
  
  def infectPercentage(percentage: Int) {
    infect(math.min(math.max(percentage, 0), 100) * network.size / 100);
  }

  def infect(numToInfect: Int) {
    if (numInfected < numToInfect) {
      val newInfections = numToInfect - numInfected;
      val subnetsToInfect = chooseSubnetsToInfect(
        uninfectedSubnets
          .map(_.size)
          .zipWithIndex
          .dropWhile(_._1 > newInfections),
        newInfections
      ).map(uninfectedSubnets.apply)
      uninfectedSubnets = uninfectedSubnets.diff(subnetsToInfect);
      subnetsToInfect.foreach(_.head.updateVersion(version));
      numInfected += subnetsToInfect.map(_.size).sum;
    }
  }

  private def chooseSubnetsToInfect(
    subnetSizes: List[(Int, Int)],
    newInfections: Int
  ): List[Int] = {
    def _chooseSubnetsToInfect(
      subnetSizes: List[(Int, Int)],
      infectionsCount: Int
    ): Option[(List[Int], Int)] = {
      // base case 1: reached the end of our subnets
      if (subnetSizes.isEmpty) {
        return Some((Nil, infectionsCount));
      // base case 2: exact fit
      } else if (infectionsCount + subnetSizes.head._1 == newInfections) {
        return Some((List(subnetSizes.head._2), newInfections));
      } else {
        var subnetSequenceOpt1: Option[(List[Int], Int)] = None;
        var subnetSequenceOpt2: Option[(List[Int], Int)] = None;
        if (infectionsCount + subnetSizes.head._1 < newInfections) {
          // get the best sequence we can make while including this subnet
          subnetSequenceOpt1 = _chooseSubnetsToInfect(
            subnetSizes.tail,
            infectionsCount + subnetSizes.head._1
          ).map(tup => (subnetSizes.head._2 +: tup._1, tup._2));
        }
        // if exact fit, we choose this sequence and skip the next check
        if (subnetSequenceOpt1.exists(_._2 == newInfections)) {
          return subnetSequenceOpt1;
        }
        // get the best sequence we can make while excluding this subnet
        subnetSequenceOpt2 = _chooseSubnetsToInfect(
          subnetSizes.tail,
          infectionsCount
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
    return _chooseSubnetsToInfect(subnetSizes, 0).map(_._1).getOrElse(Nil);
  }
}

val users = Range(0, 15).map(x => new User("user" + x));
users(0).addTeacher(users(3));
users(1).addTeacher(users(4));
users(1).addTeacher(users(5));
users(5).addStudent(users(4));
users(5).addStudent(users(7));
users(7).addTeacher(users(8));
users(2).addTeacher(users(3));
users(3).addTeacher(users(2));
users(6).addStudent(users(9));
users(10).addStudent(users(6));
users(10).addStudent(users(14));
users(11).addTeacher(users(12));
val infector = new NetworkInfector(users.toSet, 1);
infector.infect(3);
infector.infect(8);
users.foreach(user => println(user + ":\t" + user.version));
