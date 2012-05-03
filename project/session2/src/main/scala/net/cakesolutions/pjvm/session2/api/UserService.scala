package net.cakesolutions.pjvm.session2.api

import cc.spray.Directives
import cc.spray.typeconversion.DefaultMarshallers
import akka.util.Timeout
import akka.dispatch.Await
import akka.pattern.ask
import net.cakesolutions.pjvm.session2.domain.User
import cc.spray.directives.LongNumber
import net.cakesolutions.pjvm.session2.app._

trait UserService extends Directives with DefaultMarshallers with CustomMarshallers {
  implicit val timeout = Timeout(10000)

  val userService = {
    get {
      path("user/list") {
        completeWith {
          val actor = actorSystem.actorFor("/user/application/entity")
          Await.result((actor ? ListEntities(classOf[User])).mapTo[List[User]], timeout.duration)
        }
      } ~
      path("user" / LongNumber) {
        id =>
          completeWith {
            val actor = actorSystem.actorFor("/user/application/entity")
            // ask the actor for the reply to the GetEntity(classOf[User], id) message
            //{
            Await.result((actor ? GetEntity(classOf[User], id)).mapTo[Option[User]], timeout.duration)
            //}
            //{}            "Implement me!"
          }
      } ~
      path("user/count") {
        completeWith {
          val actor = actorSystem.actorFor("/user/application/entity")
          // ask the actor for the reply to the CountEntities(classOf[User]) message
          //{
          Await.result((actor ? CountEntities(classOf[User])).mapTo[Long], timeout.duration)
          //}
          //{}            "Implement me!"
        }
      }
    } ~
    post {
      path("user/generate") {
        completeWith {
          val actor = actorSystem.actorFor("/user/application/user")
          // tell the actor to react to the Generate(10) message
          //{
          actor ! Generate(10)
          //}

          "Generating"
        }
      } ~
      path("user/import") {
        completeWith {
          val actor = actorSystem.actorFor("/user/application/user")
          // tell the actor to react to the Import("file-path") message
          //{
          actor ! Import("/Users/janmachacek/Training/pvjm/project/session2/src/users.xml")
          //}

          "Importing"
        }
      }
    }
  }
}

