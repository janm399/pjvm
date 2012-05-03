package net.cakesolutions.pjvm.session2.importer

import net.cakesolutions.pjvm.session2.domain.User

class UserReader {

  List<User> read(Reader input) {
    def users = new ArrayList<User>()
    //{
    new XmlSlurper().parse(input).children().each { elem ->
      def user = new User()
      user.firstName = elem.@firstName
      user.lastName = elem.@lastName
      user.username = elem.@username

      users.add(user)
    }
    //}

    users
  }

}
