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

import java.nio.file.Paths
import java.util.UUID
import scala.concurrent.ExecutionContextExecutor
import scala.sys.process._
import scala.util.{Failure, Success}

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val logger = Logger("server")

    val route =
      pathPrefix("api") {
        path("upload") {
          fileUpload("zip") {
            case (info, value) =>
              onComplete {
                value.runWith {
                  val path = UUID.randomUUID() + "/zip"
                  FileIO.toPath(Paths.get(path))
                }.map { path =>
                  s"unzip $path".!!

                }
              } {
                case Failure(exception) =>
                  logger.error("Upload error", exception)
                  complete(StatusCodes.InternalServerError, "ERROR " + exception.getMessage)
                case Success(value) =>
                  complete(value)
              }
          }
        }
      }

    Http().newServerAt("0.0.0.0", 8080).bind(route)

    println(s"Server now online")
  }
}