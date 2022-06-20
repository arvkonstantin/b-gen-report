import java.lang.reflect.Method
import scala.util.Try
import scala.util.parsing.json.JSONObject

class Foo {
  var bar: String = "bar"

  def count = 5

  def getBar: String = bar
}

object Foo {
  val testFoo = new Foo

  val fieldsForReport: Seq[(String, String)] = Seq("getBar" -> "поле Bar", "count" -> "метод count")


  def getResultMap(foo: Foo): Map[String, String] = {
    Foo.fieldsForReport.map {
      case (name, _) =>
        val mtd: Method = testFoo.getClass.getMethod(name)
        (name, s"${
          Try {
            mtd.invoke(testFoo).toString
          }.getOrElse("")
        }")
    }.toMap
  }

  //noinspection ScalaDeprecation
  println(JSONObject(Foo.getResultMap(Foo.testFoo)).toString())
}
