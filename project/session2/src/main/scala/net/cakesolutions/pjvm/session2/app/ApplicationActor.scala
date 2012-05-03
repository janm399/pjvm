package net.cakesolutions.pjvm.session2.app

import akka.actor.{Props, Actor}
import net.cakesolutions.pjvm.session2.{Started, Stop, Start}

/**
 * @author janmachacek
 */
class ApplicationActor extends Actor {

  protected def receive = {
    case Start() =>
      // create the EntityActor under the name "entity"
      // create the UserActor   under the name "user"
      // reply Started() to the sender
      //{
      context.actorOf(props = Props[EntityActor], name = "entity")
      context.actorOf(props = Props[UserActor], name = "user")

      sender ! Started()
      //}
    case Stop() =>
      // nothing to do, but we must react to the message
  }

}
