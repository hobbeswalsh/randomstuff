package com.wordnik.irc.plugins

import javax.mail._
import javax.mail.internet._
import java.util.Properties

class FeaturePlugin extends GenericPlugin {

  val properties = System.getProperties
  properties.put("mail.smtp.host", "localhost")
  val session = Session.getDefaultInstance(properties)
  val message = new MimeMessage(session)

  message.setFrom(new InternetAddress("scabo@wordnik.com"))
  message.setSubject("Feature request")
  message.setRecipients(Message.RecipientType.TO, "robin@wordnik.com")

  def sendMail(msg: String) {
    message.setText(msg)
    Transport.send(message)
  }

  override def process(args:List[String]): List[String] = {
    val feature = args.mkString(" ")
    sendMail(feature)
    List("Mail sent to my creator.")
  }

}

