package net.cakesolutions.pjvm.session2.api

import akka.util.Timeout
import akka.dispatch.Await
import akka.pattern.ask
import net.cakesolutions.pjvm.session2.domain.User
import net.cakesolutions.pjvm.session2.app._
import au.id.jazzy.scalaflect.ScalaFlect
import cc.spray.directives.{SprayRoute1, LongNumber}
import cc.spray._
import typeconversion._

case class UserSearch(name: Option[String], age: Int)

trait UserService extends Directives with DefaultMarshallers with CustomMarshallers {
  implicit val timeout = Timeout(10000)

  class CaseClassParameterMatcher[A <: AnyRef : ClassManifest, R1](f1: A => R1) extends Deserializer[Map[String, String], A] {
    override def apply(params: Map[String, String]) = {
      val clazz = classManifest[A].erasure.asInstanceOf[Class[A]]
      val sf = new ScalaFlect[A](clazz)
      val member = sf.reflect(f1)

      println(member)

      Right(clazz.getConstructor(classOf[Option[String]], classOf[Int]).
        newInstance(None, Int.box(5)))
    }
  }

  def cc[A](pm: Deserializer[Map[String, String], A]): SprayRoute1[A] = filter1[A] { ctx =>
    pm(ctx.request.queryParams) match {
      case Right(value) => Pass.withTransform(value) {
        _.cancelRejections {
          _ match {
            case MissingQueryParamRejection("x") => true
            case MalformedQueryParamRejection(_, "x") => true
            case _ => false
          }
        }
      }
      case Left(ContentExpected) => Reject(MissingQueryParamRejection("x"))
      case Left(MalformedContent(errorMsg)) => Reject(MalformedQueryParamRejection(errorMsg, "x"))
      case Left(UnsupportedContentType(_)) => throw new IllegalStateException
    }
  }

  def $[A <: AnyRef : ClassManifest, R1](f1: A => R1) = new CaseClassParameterMatcher(f1)
  def $[A <: AnyRef : ClassManifest, R1, R2](f1: A => R1, f2: A => R2) = new CaseClassParameterMatcher(f1)

  val svc =
    path("user") {
      cc[UserSearch]($(_.name, _.age)) { us =>
        get {
          completeWith("Search for " + us)
        }
      }
    }


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

