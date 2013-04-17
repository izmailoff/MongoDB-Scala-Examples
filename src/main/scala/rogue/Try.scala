package rogue

import com.foursquare.rogue.LiftRogue._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

object Try extends App {
  // connect to DB. See impl in User.scala
  RogueMongo.connectToMongo
  
  // clean up before we start, so that we can see fresh data in db when done.
  User.drop //or bulkDelete_!!!
  Post.drop
  
  // Create first object with all fields
  val newObj = User.createRecord.username("alex").password("mypass").email("a@b.com").points(123).save

  // Create second object without email. Email will be set to empty string
  val newObj2 = User.createRecord.username("mich").password("12345").points(999).save

  // Create third object with embedded single address, and a list of other addresses.
  // List() of addresses in Scala is converted to JSON array.
  val newObj3 = User.createRecord.username("fancy").password("pass").points(1).email("e@n.com").
    homeAddress(Address.createRecord.street("1 Mountain Road").city("Rocky")).
    otherAddresses(List(Address.createRecord.street("2 Valley Ave").city("Plain"),
      Address.createRecord.street("3 Sandy Str").city("Desert"))).save
  // obviously addresses can be created as separate objects and passed to user create record call

  // Create 4th object with FK.
  val newObj4 = User.createRecord.username("me").password("secret").save
  val userPost1 = Post.createRecord.title("test title 1").body("contents of the post 1").userId(newObj4.id).save
  val userPost2 = Post.createRecord.title("test title 2").body("contents of the post 2").userId(newObj4.id).save
      
  // make sure indexes are created
  val collection = RogueMongo.connection.getCollection("users")
  collection.ensureIndex(MongoDBObject("username" -> 1))
  
  // query interface
  // number of docs
  println("Number of docs in collection: " + User.count)

  // distinct users
  println("distinct in all fields:")
  User.findAll.distinct // not sure how to do distinct by subset of fields yet

  // all docs
  println("all docs:")
  User.findAll foreach (println _)

  // find exact document
  println("Searching for: " + newObj)
  User.find(newObj.asDBObject) foreach (println _)

  // find by column/attribute
  val q = MongoDBObject("points" -> 999)
  println("Find one doc by attribute: " + User.find(q).get)

  // find and limit fields
  println("List only email field:")
  val emptyCondition = MongoDBObject.empty
  val fields = MongoDBObject("email" -> 1)
  for (x <- User.find(emptyCondition)) println(x.email) // there should be better way

  // querying with DSL
  // limit displayed field (all rows are displayed even without field)
  println("Show only docs containing email:")
  User.where(_.email exists true) foreach (println _)

  // comparison operators and exact match
  val complexCondition = User.where(_.points between (100, 200)).and(_.email eqs "a@b.com").toString
  println("Complex condition: " + complexCondition)
  User.where(_.points between (100, 200)).and(_.email eqs "a@b.com") foreach (println _)
  
  // fetch documents by FK.
  Post.where(_.userId eqs newObj4.id) foreach (post => println(post.title + ": " + post.body))

  RogueMongo.disconnectFromMongo
}