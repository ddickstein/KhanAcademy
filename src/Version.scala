package khanacademy.src;

case class Version(x: Int, y: Int, z: Int) extends Ordered[Version] {
  require(x >= 0, "Bad version: " + this);
  require(y >= 0, "Bad version: " + this);
  require(z >= 0, "Bad version: " + this);
  override def compare(that: Version): Int = {
    if (this.x == that.x) {
      if (this.y == that.y) {
        if (this.z == that.z) {
          return 0;
        } else {
          return this.z - that.z;
        }
      } else {
        return this.y - that.y;
      }
    } else {
      return this.x - that.x;
    }
  }
  override def toString: String = if (z == 0) {
    x + "." + y;
  } else {
    x + "." + y + "." + z;
  }
}
