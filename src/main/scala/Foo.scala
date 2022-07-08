import java.lang.reflect.Method
import scala.util.parsing.json.JSONObject
import scala.util.{Random, Try}

class Foo {
  var bar: String = "bar"

  val notif_num = Random.nextInt(4532)

  def count = 5

  def getBar: String = bar
}

object Foo {
  val testFoo = new Foo

  val fieldsForReport: Seq[(String, String)] =
    Seq("getBar" -> "поле Bar", "count" -> "метод count", "notif_num" -> "Номер уведомления")


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
