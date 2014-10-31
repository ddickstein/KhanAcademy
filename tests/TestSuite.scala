package khanacademy.tests;

import khanacademy.src.{User, UserNetwork, Version}

object TestSuite extends App {
  
  List(
    test(testCase1),
    test(testCase2),
    test(testCase3),
    test(testCase4),
    test(testCase5),
    test(testCase6),
    test(testCase7),
    test(testCase8),
    test(testCase9),
    test(testCase10)
  ).zipWithIndex.foreach({ case (test, index) => if (test) {
      pass(index + 1);
    } else {
      fail(index + 1);
    }
  });

  private def pass(test: Int) {
    println(Console.GREEN + "Test " + test + ": passed" + Console.RESET);
  }

  private def fail(test: Int) {
    println(Console.RED + "Test " + test + ": failed" + Console.RESET);
  }

  private def test(testCase: => Unit): Boolean = try {
    testCase;
    return true;
  } catch {
    case e: AssertionError => return false;
  }

  private def testCase1 { // test empty network case
    val network = new UserNetwork();
    assert(network.prepareVersion(Version(0, 0, 1)));
    network.updateNetwork(1);
    assert(network.getNetworkSize == 0);
    assert(network.getNumUpdated == 0);
  }

  private def testCase2 { // test single user case
    val network = new UserNetwork();
    val user = new User("user1", network);
    assert(user.version == Version(0, 0, 0));
    assert(network.prepareVersion(Version(0, 0, 1)));
    assert(!network.readyForNewVersion)
    network.updateNetwork(1);
    assert(user.version == Version(0, 0, 1));
    assert(network.readyForNewVersion);
  }

  private def testCase3 { // test three unrelated users case
    val network = new UserNetwork();
    val user1 = new User("user1", network);
    val user2 = new User("user2", network);
    val user3 = new User("user3", network);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetworkPercentage(33);
    assert(network.getNumUpdated == 0);
    network.updateNetworkPercentage(34);
    assert(network.getNumUpdated == 1);
    network.updateNetworkPercentage(67);
    assert(network.getNumUpdated == 2);
    network.updateNetworkPercentage(100);
    assert(network.getNumUpdated == 0);
    assert(network.getVersion == Version(0, 0, 1));
  }

  private def testCase4 { // test three related users case
    val network = new UserNetwork();
    val user1 = new User("user1", network);
    val user2 = new User("user2", network);
    val user3 = new User("user3", network);
    user1.addTeacher(user2);
    user2.addStudent(user3);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetworkPercentage(99);
    assert(network.getNumUpdated == 0);
    assert(user1.version == Version(0, 0, 0));
    assert(user2.version == Version(0, 0, 0));
    assert(user3.version == Version(0, 0, 0));
    // ensure they all update at the same time
    network.updateNetworkPercentage(100);
    assert(network.getNumUpdated == 0);
    assert(network.getVersion == Version(0, 0, 1));
    assert(user1.version == Version(0, 0, 1));
    assert(user2.version == Version(0, 0, 1));
    assert(user3.version == Version(0, 0, 1));
  }

  private def testCase5 { // subnets [2, 4, 6] starting with 2-person injection
    val network = new UserNetwork();
    val user1  = new User("user1", network);
    val user2  = new User("user2", network);
    val user3  = new User("user3", network);
    val user4  = new User("user4", network);
    val user5  = new User("user5", network);
    val user6  = new User("user6", network);
    val user7  = new User("user7", network);
    val user8  = new User("user8", network);
    val user9  = new User("user9", network);
    val user10 = new User("user10", network);
    val user11 = new User("user11", network);
    val user12 = new User("user12", network);
    // subnet 2
    user1.addTeacher(user2);
    // subnet 4
    user3.addStudent(user4);
    user3.addStudent(user5);
    user6.addTeacher(user5);
    // subnet 6
    user7.addTeacher(user9);
    user8.addTeacher(user10);
    user10.addStudent(user12);
    user12.addTeacher(user11);
    user11.addTeacher(user7);
    val users = List(user1, user2, user3, user4, user5, user6, user7, user8,
      user9, user10, user11, user12);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetwork(3);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, false,
      false, false, false, false, false, false, false, false, false));
    network.updateNetwork(6);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, false, false, false, false, false, false));
    network.updateNetwork(10);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, false, false, false, false, false, false));
    network.updateNetwork(12);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, true, true, true, true));
  }


  private def testCase6 { // subnets [2, 4, 6] starting with 6-person injection
    val network = new UserNetwork();
    val user1  = new User("user1", network);
    val user2  = new User("user2", network);
    val user3  = new User("user3", network);
    val user4  = new User("user4", network);
    val user5  = new User("user5", network);
    val user6  = new User("user6", network);
    val user7  = new User("user7", network);
    val user8  = new User("user8", network);
    val user9  = new User("user9", network);
    val user10 = new User("user10", network);
    val user11 = new User("user11", network);
    val user12 = new User("user12", network);
    // subnet 2
    user1.addTeacher(user2);
    // subnet 4
    user3.addStudent(user4);
    user3.addStudent(user5);
    user6.addTeacher(user5);
    // subnet 6
    user7.addTeacher(user9);
    user8.addTeacher(user10);
    user10.addStudent(user12);
    user12.addTeacher(user11);
    user11.addTeacher(user7);
    val users = List(user1, user2, user3, user4, user5, user6, user7, user8,
      user9, user10, user11, user12);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetwork(6);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false,
      false, false, false, false, true, true, true, true, true, true));
    network.updateNetwork(11);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false, true,
      true, true, true, true, true, true, true, true, true));
    network.updateNetwork(12);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, true, true, true, true));
  }

  private def testCase7 { // require exact match
    val network = new UserNetwork();
    val user1  = new User("user1", network);
    val user2  = new User("user2", network);
    val user3  = new User("user3", network);
    val user4  = new User("user4", network);
    val user5  = new User("user5", network);
    val user6  = new User("user6", network);
    val user7  = new User("user7", network);
    val user8  = new User("user8", network);
    val user9  = new User("user9", network);
    val user10 = new User("user10", network);
    val user11 = new User("user11", network);
    val user12 = new User("user12", network);
    // subnet 2
    user1.addTeacher(user2);
    // subnet 4
    user3.addStudent(user4);
    user3.addStudent(user5);
    user6.addTeacher(user5);
    // subnet 6
    user7.addTeacher(user9);
    user8.addTeacher(user10);
    user10.addStudent(user12);
    user12.addTeacher(user11);
    user11.addTeacher(user7);
    val users = List(user1, user2, user3, user4, user5, user6, user7, user8,
      user9, user10, user11, user12);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetwork(6, true);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false,
      false, false, false, false, true, true, true, true, true, true));
    network.updateNetwork(11, true);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false,
      false, false, false, false, true, true, true, true, true, true));
    network.updateNetwork(10, true);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false, true,
      true, true, true, true, true, true, true, true, true));
    network.updateNetwork(12, true);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, true, true, true, true));
  }

  private def testCase8 { // add users to network in the middle of an update
    val network = new UserNetwork();
    val user1  = new User("user1", network);
    val user2  = new User("user2", network);
    val user3  = new User("user3", network);
    val user4  = new User("user4", network);
    val user5  = new User("user5", network);
    val user6  = new User("user6", network);
    val user7  = new User("user7", network);
    val user8  = new User("user8", network);
    val user9  = new User("user9", network);
    val user10 = new User("user10", network);
    val user11 = new User("user11", network);
    val user12 = new User("user12", network);
    // subnet 2
    user1.addTeacher(user2);
    // subnet 4
    user3.addStudent(user4);
    user3.addStudent(user5);
    user6.addTeacher(user5);
    // subnet 6
    user7.addTeacher(user9);
    user8.addTeacher(user10);
    user10.addStudent(user12);
    user12.addTeacher(user11);
    user11.addTeacher(user7);
    val users = List(user1, user2, user3, user4, user5, user6, user7, user8,
      user9, user10, user11, user12);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetwork(6);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false,
      false, false, false, false, true, true, true, true, true, true));
    network.updateNetwork(11);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false, true,
      true, true, true, true, true, true, true, true, true));
    val user13 = new User("user13", network);
    val user14 = new User("user14", network);
    val user15 = new User("user15", network);
    user13.addTeacher(user14);
    user13.addStudent(user15);
    val newUsers = List(user13, user14, user15);
    network.updateNetwork(13);
    assert(users.map(_.version == Version(0, 0, 1)) == List(false, false, true,
      true, true, true, true, true, true, true, true, true));
    assert(newUsers.map(_.version == Version(0, 0, 1)) == List(true,
      true, true));
    network.updateNetwork(15);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, true, true, true, true));
  }

  private def testCase9 { // link 2 untouched subnets during update
    val network = new UserNetwork();
    val user1  = new User("user1", network);
    val user2  = new User("user2", network);
    val user3  = new User("user3", network);
    val user4  = new User("user4", network);
    val user5  = new User("user5", network);
    val user6  = new User("user6", network);
    val user7  = new User("user7", network);
    val user8  = new User("user8", network);
    val user9  = new User("user9", network);
    val user10 = new User("user10", network);
    val user11 = new User("user11", network);
    val user12 = new User("user12", network);
    // create network of [3, 3, 2, 2, 1, 1]
    user1.addTeacher(user2);
    user1.addStudent(user3);
    user4.addTeacher(user5);
    user4.addStudent(user6);
    user7.addTeacher(user8);
    user9.addStudent(user10);
    val users = List(user1, user2, user3, user4, user5, user6, user7, user8,
      user9, user10, user11, user12);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetworkPercentage(50);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, false, false, false, false, false, false));
    user7.addTeacher(user11);
    user12.addTeacher(user10);
    network.updateNetwork(11);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, false, false, true, false));
    network.updateNetwork(12);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, true, true, true, true));
  }

  private def testCase10 { // link untouched and updated subnets during update
    val network = new UserNetwork();
    val user1  = new User("user1", network);
    val user2  = new User("user2", network);
    val user3  = new User("user3", network);
    val user4  = new User("user4", network);
    val user5  = new User("user5", network);
    val user6  = new User("user6", network);
    val user7  = new User("user7", network);
    val user8  = new User("user8", network);
    val user9  = new User("user9", network);
    val user10 = new User("user10", network);
    val user11 = new User("user11", network);
    val user12 = new User("user12", network);
    // create network of [3, 3, 2, 2, 1, 1]
    user1.addTeacher(user2);
    user1.addStudent(user3);
    user4.addTeacher(user5);
    user4.addStudent(user6);
    user7.addTeacher(user8);
    user9.addStudent(user10);
    val users = List(user1, user2, user3, user4, user5, user6, user7, user8,
      user9, user10, user11, user12);
    network.prepareVersion(Version(0, 0, 1));
    network.updateNetworkPercentage(50);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, false, false, false, false, false, false));
    user7.addTeacher(user5);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, false, false, false, false));
    assert(network.getNumUpdated == 8);
    user12.addTeacher(user10);
    user11.addStudent(user2);
    user9.addStudent(user8);
    assert(users.map(_.version == Version(0, 0, 1)) == List(true, true, true,
      true, true, true, true, true, true, true, true, true));
    assert(network.readyForNewVersion);
    assert(network.getNumUpdated == 0);
  }
}
