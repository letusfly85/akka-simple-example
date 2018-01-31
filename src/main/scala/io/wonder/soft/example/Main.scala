package io.wonder.soft.example

import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.{Flow, Sink, Source}
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
    while (i < 3) {
      i += 1
      Thread.sleep(1000L)

      echoActor ! Echo(s"hello! ${i} times")

      Thread.sleep(1000L)

      echoActor ! s"plain string ${i} times"
    }

    // define source data
    val source1 = Source(1 to 10)

    // define map operation
    val flow1 = Flow[Int].map(elem => elem * elem)
    val flow2 = Flow[Int].map(elem => elem - 1)


    // define sink operation
    val sink = Sink.foreach[Int](println)

    val runnableGraph = source1 via flow1 via flow2 to sink

    //val source2 = Source(4 to 13)
    //val runnableGraph = source1 merge source2 via flow2 to sink

    runnableGraph.run()
  }

}
