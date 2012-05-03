package net.cakesolutions.pjvm.session2.app

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{Props, ActorSystem}
import org.hibernate.{Session, SessionFactory}
import net.cakesolutions.pjvm.session2.domain.User

class UserActorTest extends TestKit(ActorSystem()) with Specification with Mockito {
  val sessionFactory = mock[SessionFactory]
  val session = mock[Session]
  val userActor = TestActorRef(props = Props {
    val actor = new UserActor
    actor.sessionFactory = sessionFactory

    actor
  })

  "generates specified number of users" in {
    val count = 5

    sessionFactory.getCurrentSession returns session
    userActor ! Generate(count)

    there was count.times(session).saveOrUpdate(any[User])
  }

}
