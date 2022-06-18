import scala.util.Try

class Foo {
  var bar: String = "bar"

  def count = 5

  def getBar: String = bar
}

object Foo extends App {
  val testFoo = new Foo

  val fieldsForReport = Seq("getBar" -> "поле Bar", "count" -> "метод count")

  val mtd = testFoo.getClass.getMethod("getBar")
  Try {
    mtd.invoke(testFoo).toString
  }.foreach(println)
}
