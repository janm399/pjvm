package net.cakesolutions.pjvm.session2

import akka.actor.{Props, ActorSystem}
import api.UserService
import app._
import infrastructure.SpringContextActor
import akka.dispatch.Await
import cc.spray.can.server.HttpServer
import cc.spray.io.pipelines.MessageHandlerDispatch
import cc.spray.io.IoWorker
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import cc.spray.{HttpService, SprayCanRootService}

case class Start()
case class Started()

case class Stop()

class Boot(system: ActorSystem) {
  implicit val timeout = Timeout(30000)

  val spring = system.actorOf(
    props = Props[SpringContextActor],
    name = "spring")
  val application = system.actorOf(
    props = Props[ApplicationActor],
    name = "application"
  )

  // Send the spring and application actors the Start() message.
  // Await until ready for at most timeout.

  //{
  Await.ready(spring ? Start(), timeout.duration)
  Await.ready(application ? Start(), timeout.duration)
  //}

  println("ready")
}

class SprayCanBoot(system: ActorSystem) {

  val userModule = new UserService {
    implicit def actorSystem = system
  }

  val rootService = system.actorOf(
    props = Props(new SprayCanRootService(
      system.actorOf(Props(new HttpService(userModule.userService)))
    )),
    name = "root-service"
  )

  // every spray-can HttpServer (and HttpClient) needs an IoWorker for low-level network IO
  // (but several servers and/or clients can share one)
  val ioWorker = new IoWorker(system).start()

  // create and start the spray-can HttpServer, telling it that we want requests to be
  // handled by the root service actor
  val sprayCanServer = system.actorOf(
    Props(new HttpServer(ioWorker, MessageHandlerDispatch.SingletonHandler(rootService))),
    name = "http-server"
  )

  // a running HttpServer can be bound, unbound and rebound
  // initially to need to tell it where to bind to
  sprayCanServer ! HttpServer.Bind("localhost", 8080)

  // finally we drop the main thread but hook the shutdown of
  // our IoWorker into the shutdown of the applications ActorSystem
  system.registerOnTermination {
    ioWorker.stop()
  }


}

object Main extends App {
  // -javaagent:/Users/janmachacek/.m2/repository/org/springframework/spring-instrument/3.1.1.RELEASE/spring-instrument-3.1.1.RELEASE.jar -Xmx512m -XX:MaxPermSize=256m
  val system = ActorSystem("PJT")

  // boot the main app
  new Boot(system)

  // boot the HTTP interface
  new SprayCanBoot(system)

  sys.addShutdownHook {
    system.shutdown()
  }

}
