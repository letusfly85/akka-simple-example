package io.wonder.soft.example

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import io.wonder.soft.example.actor.{Echo, EchoActor}

import scala.concurrent.ExecutionContextExecutor

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem  = ActorSystem("AkkaSimpleExample")
    implicit val executor: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: Materializer = ActorMaterializer()

    val echoActor = system.actorOf(Props[EchoActor], "echo-actor")

    var i = 0
    while (true) {
      i += 1
      Thread.sleep(1000L)

      echoActor ! Echo(s"hello! ${i} times")
    }
  }

}
