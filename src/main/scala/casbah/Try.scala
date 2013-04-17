package casbah

//import com.mongodb.casbah._
//import com.mongodb.casbah.Implicits._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBList
//import com.mongodb.casbah.commons.MongoDBObject

object Try extends App {
  // get DB server connection
  val mongoConn = MongoConnection()

  // create a new DB and Collection if not present or use existing one
  val collection = mongoConn("newdb")("users")
  val collectionPosts = mongoConn("newdb")("posts")
  
  // delete the collection
  collection.dropCollection
  collectionPosts.dropCollection
  
  // add 1st Document
  val newObj = MongoDBObject("username" -> "alex", "password" -> "mypass", "points" -> 123, "email" -> "a@b.com")
  collection += newObj

  // add 2nd Document using builder
  val builder = MongoDBObject.newBuilder
  builder += "username" -> "mich"
  builder += "password" -> "12345"
  builder += "points" -> 999
  val newObj2 = builder.result
  collection += newObj2

  // 3rd Document with nested documents - same structure as in rogue.Try.
  val newObj3 = MongoDBObject("username" -> "fancy", "password" -> "pass", "points" -> 1, "email" -> "e@n.com",
    "homeAddress" -> MongoDBObject("street" -> "1 Mountain Road", "city" -> "Rocky"),
    "otherAddresses" -> MongoDBList(MongoDBObject("street" -> "2 Valley Ave", "city" -> "Plain"),
      MongoDBObject("street" -> "3 Sandy Str", "city" -> "Desert")))
  collection += newObj3
  // Again, nested objects can be extracted into variables for clarity

  // 4th Document with FKs.
  val newObj4 = MongoDBObject("username" -> "me", "password" -> "secret")
  collection += newObj4
  val userPost1 = MongoDBObject("title" -> "test title 1", "body" -> "contents of the post 1", "userId" -> newObj4._id.get)
  val userPost2 = MongoDBObject("title" -> "test title 2", "body" -> "contents of the post 2", "userId" -> newObj4._id.get)
  collectionPosts += userPost1
  collectionPosts += userPost2

  // create an index
  collection.ensureIndex("username") // there is also createIndex
  
  // query interface
  // number of docs
  println("Number of docs in collection: " + collection.size) //FIXME: count does not work for some reason (implicits?)

  // distinct users
  println("distinct by username:")
  collection.distinct("username") foreach (println _)

  // all docs
  println("all docs:")
  collection.find foreach (println _)

  // find exact document
  println("Searching for: " + newObj)
  collection.find(newObj) foreach (println _)

  // find by column/attribute
  val q = MongoDBObject("points" -> 999)
  println("Find one doc by attribute: " + collection.findOne(q))

  // find and limit fields
  println("List only email field:")
  val emptyCondition = MongoDBObject.empty
  val fields = MongoDBObject("email" -> 1)
  for (x <- collection.find(emptyCondition, fields)) println(x)

  // querying with DSL
  // limit displayed field (all rows are displayed even without field)
  println("Show only docs containing email:")
  val emailQuery = "email" $exists true
  collection.find(emailQuery) foreach (println _)

  // comparison operators and exact match
  val complexCondition = ("points" $gte 100 $lte 200) ++ ("email" -> "a@b.com")
  println("Complex condition: " + complexCondition)
  collection.find(complexCondition) foreach (println _)

  // find by FK.
  collectionPosts.find(MongoDBObject("userId" -> newObj4._id.get)) foreach (post => println(post("title") + ": " + post("body")))
}
