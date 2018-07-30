package io.wonder.soft.example

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import io.wonder.soft.example.actor.{CountAsk, CustomMyClass, EchoActor, EchoMessage}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

/*
object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("AkkaSimpleExample")
    implicit val executor: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: Materializer = ActorMaterializer()

    implicit val timeout: Timeout = 5.seconds

    // val echoActor = system.actorOf(Props[EchoActor], s"echo-actor-${i.toString}")
    val echoActor = system.actorOf(Props[EchoActor], s"echo-actor")
    var i = 0
    var futureList = List.empty[Future[Int]]
    while (i < 300) {
      i += 1
      // echoActor ! EchoMessage(s"hello! ${i} times")
      // echoActor ! i
      echoActor ! 1

      val future = echoActor ? CountAsk()
      futureList ::= future.asInstanceOf[Future[Int]]
    }

    futureList.foreach {future =>
      val result = Await.result(future, 6.seconds)
      println(s"result is ${result}")
    }

    println("fin.")
  }

//    // define source data
//    val source1 = Source(1 to 10)
//
//    // define map operation
//    val flow1 = Flow[Int].map(elem => elem * elem)
//    val flow2 = Flow[Int].map(elem => elem - 1)
//
//
//    // define sink operation
//    val sink = Sink.foreach[Int](println)
//
//    val runnableGraph = source1 via flow1 via flow2 to sink
//
//    //val source2 = Source(4 to 13)
//    //val runnableGraph = source1 merge source2 via flow2 to sink
//
//    runnableGraph.run()
//  }

}
*/

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ContentTypes._
import akka.stream.{ ActorMaterializer, Materializer }
import akka.util.ByteString
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.ExecutionContextExecutor

trait Service {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout: Timeout = 5.seconds

  val startTimeMillis = System.currentTimeMillis()

  val echoActor: ActorRef

  def config: Config
  def logger: LoggingAdapter

  val routes =
    path("api" / "v1" / "status") {
      (get | post) {

        echoActor ! 1
        val future = echoActor ? CountAsk()
        val result = Await.result(future, 6.seconds)
        logger.info(s"counter: ${result}")

        complete("alive")
      }
    }

    /*
    } ~ path("api" / "v1" / "sample") {
      post {
        entity(as[KeyPlayer]) { myKey: KeyPlayer =>
          delegateActor ! NormalEmployee(myKey.userId)
          val endTimeMills = System.currentTimeMillis()
          val estimate = endTimeMills - startTimeMillis

          complete(s"${myKey.userId}-${myKey.key}-${estimate.toString}\n")
        }
      }
    }
    */
}

object Main extends App with Service {
  override implicit val system: ActorSystem = ActorSystem("akka-http-sample")
  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  override val echoActor: ActorRef =
    system.actorOf(Props[EchoActor], "echo-actor")

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
