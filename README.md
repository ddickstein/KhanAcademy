Dani Dickstein<br />
Khan Academy<br />
Limited Infection Interview<br />
10/30/14
<br /><br />
Language of choice: Scala

## How to Run
  To compile the program, execute `scalac src/*.scala tests/*.scala` from the
  root folder.

  To run the tests, execute `scala khanacademy.tests.TestSuite` from the root
  folder.

  The tests provide some basic sample code for how to use the network infection
  utility.  After creating a new `UserNetwork`, creating users, and defining
  relationships between them, you can prepare the network for a new update
  by invoking `myNetwork.prepareVersion(version)`.

  You can then invoke `myNetwork.updateNetwork(numToUpdate)` or 
  `myNetwork.updateNetworkPercentage(percentage)`, whichever is more
  convenient (the latter invokes the former).  The numbers supplied to these
  functions are given in abolute terms rather than with respect to previous
  calls.  This means that if you say `updateNetworkPercentage(50)` twice, the
  second call will have no effect, because 50% of the network has already been
  updated.  To update the remainder of the network,
  `updateNetworkPercentage(100)` must be called.

  `updateNetwork(numToUpdate)` accepts an optional boolean parameter to
  indicate whether to require an exact match before updating.  If false, we
  update the network by choosing the best possible match without exceeding the
  given number ("price is right" strategy).  When not specified, the option
  defaults to false.

  To conform to the spec, `total_infection` is treated as an alias
  for `updateNetworkPercentage(100)`, and `limited_infection` is an alias for
  `updateNetwork(numToUpdate, requireExactMatch)`.
