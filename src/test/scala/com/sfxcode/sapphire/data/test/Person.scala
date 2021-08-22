package com.sfxcode.sapphire.data.test

import java.text.SimpleDateFormat
import java.util.Date

import com.sfxcode.sapphire.data.DataAdapter
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

import scala.io.Source

case class Person(
    id: Long,
    guid: String,
    isActive: Boolean,
    balance: Double,
    picture: String,
    age: Int,
    name: String,
    gender: String,
    email: String,
    phone: String,
    address: String,
    about: String,
    tags: List[String],
    friends: List[Friend],
    greeting: String,
    favoriteFruit: String
)

case class Friend(id: Long, name: String) {
  def getValue(s: String): String = s
}

object PersonDatabase {

  private val jsonString: String = fromJson("/test_data.json")
  val personen: List[Person]     = decode[List[Person]](jsonString).getOrElse(List())

  val friends: List[Friend] = personen.head.friends

  def fromJson(name: String): String = {
    val is = getClass.getResourceAsStream(name)
    Source.fromInputStream(is, "UTF-8").getLines().mkString
  }

  def testPerson(id: Int): DataAdapter[Person] = DataAdapter(personen(id))

  def testFriend(id: Int): DataAdapter[Friend] = DataAdapter(friends(id))

  def testPersonen: List[DataAdapter[Person]] = personen.map(item => DataAdapter[Person](item))

  def testFriends: List[DataAdapter[Friend]] = friends.map(item => DataAdapter[Friend](item))
}
