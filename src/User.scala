package khanacademy.src;

import scala.collection.mutable.HashSet;

class User(val name: String, val network: UserNetwork) {
  override def toString: String = name;
  private val _teachers = HashSet.empty[User];
  private val _students = HashSet.empty[User];
  private var _version = Version(0, 0, 0);
  network.add(this);
  def teachers: List[User] = _teachers.toList;
  def students: List[User] = _students.toList;
  def version: Version = _version;
  
  def addStudent(student: User) {
    if (student != this && student.network == this.network
    && !students.contains(student)) {
      _students += student;
      student.addTeacher(this);
      network.connect(this, student);
    }
  }
  
  def addTeacher(teacher: User) {
    if (teacher != this && teacher.network == this.network
    && !teachers.contains(teacher)) {
      _teachers += teacher;
      teacher.addStudent(this);
    }
  }
  
  private[src] def updateVersion(newVersion: Version) {
    if (newVersion > version) {
      _version = newVersion;
      teachers.foreach(_.updateVersion(newVersion));
      students.foreach(_.updateVersion(newVersion));
    }
  }
}
