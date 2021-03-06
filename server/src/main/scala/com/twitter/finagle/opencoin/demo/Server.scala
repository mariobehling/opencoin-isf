package com.twitter.finagle.opencoin.demo

import java.net.InetSocketAddress
import com.twitter.finagle.Codec
import com.twitter.finagle.CodecFactory
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.util.Future
import org.jboss.netty.channel.ChannelPipelineFactory
import org.jboss.netty.channel.Channels
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder
import org.jboss.netty.handler.codec.frame.Delimiters
import org.jboss.netty.handler.codec.string.StringDecoder
import org.jboss.netty.handler.codec.string.StringEncoder
import org.jboss.netty.util.CharsetUtil
import java.net.NetworkInterface
import scala.collection.JavaConversions._
import java.lang.Exception
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.SessionFactory
import org.squeryl.Session
import org.squeryl.adapters.MySQLAdapter
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http.{ HttpRequest, HttpResponse, DefaultHttpResponse, QueryStringDecoder }
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http.HttpResponseStatus.OK
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.finagle.http.Http
import org.jboss.netty.handler.codec.http._
import com.codahale.jerkson.Json._

object isNumber {
  def isNumeric(input: String): Boolean = input.forall(_.isDigit)
}

case class Coin(var Type: String, var Value: Int) {
  def this() = this("", 0)
}

object Open_Coin extends Schema {
  val coins = table[Coin]
}

object Server {
  def get_ip(): String = {
    var i = 0
    var j = 0
    var ip = new Array[String](10)
    NetworkInterface.getNetworkInterfaces.foreach { it =>
      it.getInetAddresses.foreach { inet =>
        inet.getAddress()
        if (i % 2 != 0) {
          ip(j) = inet.toString().substring(1, inet.toString().length())
          j = j + 1
        }
        i = i + 1
      }
    }
    ip(0)
  }
  def connection(databaseConnection: String, databaseUsername: String, databasePassword: String): Boolean = {
    Class.forName("com.mysql.jdbc.Driver")
    try {
      Session.create(java.sql.DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword), new MySQLAdapter)
      SessionFactory.concreteFactory = Some(() => Session.create(java.sql.DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword), new MySQLAdapter))
      true
    } catch {
      case e: java.sql.SQLException => { false }
    }
  }
  def Response(method: String, data: String): String = {
    if (method == "GET") {
      var map: Map[String, String] = parse[Map[String, String]](data) //PARSE JERKSON
      var _coin = Coin("", 0)
      /**** GET COIN FROM DATABASE****/
      _coin = get_coin(map("type").toString()) // GET COIN WITH COIN'S TYPE INPUTTED
      var res = ""
      if (_coin.Type != "") // COIN'S TYPE IS FOUND
      {
        //Use generate() method in the Jerkson libraries to transform a Coin object into a JSON string.
        generate(_coin)
      } else // COIN'S TYPE IS NOT FOUND
      {
        map("type") + " is not found!" // RESPONSE ERROR
      }
    } else if (method == "POST") {
      var map: Map[String, Any] = parse[Map[String, Any]](data) //PARSE JERKSON
      var _coin = Coin("", 0)
      _coin = get_coin(map("type").toString()) // GET COIN WITH COIN'S TYPE INPUTTED
      var new_value: Int = map("value").toString().toInt + _coin.Value
      /**** UPDATE COIN****/
      transaction {
        update(Open_Coin.coins)(c =>
          where(c.Type === map("type").toString())
            set (c.Value := new_value))
      }
      _coin = get_coin(map("type").toString()) // GET NEW COIN WITH COIN'S
      var res = ""
      if (_coin.Type != "") // COIN'S TYPE IS FOUND
      {
        //Use generate() method in the Jerkson libraries to transform a Coin object into a JSON string.
        generate(_coin)
      } else // COIN'S TYPE IS NOT FOUND
      {
        map("type") + " is not found!"
      }
    } else {
      method + " doesn't support"
    }
  }
  def get_coin(Type: String): Coin = {
    var ccoin = new Coin("", 0)
    transaction {
      val coin = from(Open_Coin.coins)(c => where(c.Type === Type.toString()) select (c))
      for (c <- coin) {
        ccoin = c
      }
    }
    ccoin
  }
  def main(args: Array[String]) {
    /********** SHOW IP ON THE SCREEN ************/
    var ip_address = get_ip()
    /********** INPUT DATABASE USERNAME, DATABASE PASSWORD ************/
    println("Server IP address: " + ip_address)
    print("Database username: ")
    var databaseUsername = readLine
    print("Database password: ")
    var databasePassword = readLine
    val databaseConnection = "jdbc:mysql://localhost:3306/Open_Coin"
    /********* CONNECT MYSQL *********************/
    connection(databaseConnection, databaseUsername, databasePassword)

    /***********RECEIVE REQUEST FROM CLIENT AND RESPONSE TO CLIENT*************/
    if (!connection(databaseConnection, databaseUsername, databasePassword)) {
      println("Can not connect to Database")
      return
    } else {
      val service: Service[HttpRequest, HttpResponse] = new Service[HttpRequest, HttpResponse] {
        def apply(request: HttpRequest) =
          {
            var method: String = request.getMethod().toString()
            val decoder = new QueryStringDecoder(request.getUri)
            val path = decoder.getPath.split('/').toList.drop(1)
            var str = path(0)
            println("Client send: " + str)
            var res = Response(method, str)
            var response = new DefaultHttpResponse(HTTP_1_1, OK)
            response.setContent(copiedBuffer(res, UTF_8))
            Future.value(response) // SEND RESPONSE TO CLIENT
          }
      }

      // BIND THE SERVICE
      val server: Server = ServerBuilder()
        .codec(Http())
        .bindTo(new InetSocketAddress(ip_address, 8080))
        .name("echoserver")
        .build(service)
    }
  }
}