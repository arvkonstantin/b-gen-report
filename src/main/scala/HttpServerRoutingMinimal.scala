/*
 * Copyright (C) 2020-2021 Lightbend Inc. <https://www.lightbend.com>
 */

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.FileIO

import java.io.File
import java.nio.file.Paths
import scala.concurrent.ExecutionContextExecutor
import scala.sys.process._
import scala.util.parsing.json.JSONObject
import scala.util.{Failure, Success}

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val logger = Logger("server")

    println(s"relaxed kek/main.pug pdf.pdf --build-once --no-sandbox --locals \"${ //noinspection ScalaDeprecation
      JSONObject(Foo.getResultMap(Foo.testFoo)).toString().replace(" ", "")
    }\"")

    val route =
      pathPrefix("api") {
        path("upload") {
          fileUpload("zip") {
            case (info, value) =>
              onComplete {
                value.runWith {
                  FileIO.toPath(Paths.get(info.fileName))
                }.map { _ =>
                  s"unzip ${Paths.get(info.fileName)} -d ${info.fileName.replace(".zip", "")}".!!
                  //noinspection LongLine
                  println(s"relaxed ${info.fileName.replace(".zip", "")}/main.pug pdf.pdf --build-once --no-sandbox --locals '${ //noinspection ScalaDeprecation
                    JSONObject(Foo.getResultMap(Foo.testFoo)).toString().replace(" ", "")
                  }'".!!)
                  info.fileName
                }
              } {
                case Failure(exception) =>
                  logger.error("Upload error", exception)
                  exception.printStackTrace()
                  complete(StatusCodes.InternalServerError, s"ERROR ${exception.getMessage}")
                case Success(value) =>
                  complete(HttpEntity.fromFile(
                    MediaTypes.`application/pdf`, new File(s"/root/b-gen-report/pdf.pdf")
                  ))
              }
          }
        } ~
          path("fieldsInfo") {
            complete(StatusCodes.OK, Foo.fieldsForReport.map {
              case (name, desc) =>
                s"$name -> $desc"
            }.mkString(", "))
          }
      }

    Http().newServerAt("0.0.0.0", 8080).bind(route)

    println(s"Server now online")
  }
}