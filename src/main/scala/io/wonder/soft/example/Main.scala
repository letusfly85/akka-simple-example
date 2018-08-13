package io.wonder.soft.example

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.event.{ Logging, LoggingAdapter }
import akka.stream.{ ActorMaterializer, Materializer }

import scala.concurrent.ExecutionContextExecutor

import com.typesafe.config.{ Config, ConfigFactory }
import io.wonder.soft.example.actor._

trait Service {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout: Timeout = 5.seconds

  val startTimeMillis = System.currentTimeMillis()

  val parentActor: ActorRef

  def config: Config
  def logger: LoggingAdapter

  val routes =
    path("api" / "v1" / "status") {
      (get | post) {
        val future = parentActor ? (Props[EchoActor], "my-child-actor")
        val childActor = Await.result(future, 5.seconds).asInstanceOf[ActorRef]

        childActor ! 'crash

//        echoActor ! 1
//        val future = echoActor ? CountAsk()
//        val result = Await.result(future, 6.seconds)
//        logger.info(s"counter: ${result}")

        complete("alive")
      }
    }
}

object Main extends App with Service {
  override implicit val system: ActorSystem = ActorSystem("akka-http-sample")
  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  override val parentActor: ActorRef = system.actorOf(Props[ParentEchoActor], name = "echo-parent-actor")

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
