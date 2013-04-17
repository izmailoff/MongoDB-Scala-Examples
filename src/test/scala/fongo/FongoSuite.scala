package fongo

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter
import org.junit.runner.RunWith
import com.foursquare.fongo.Fongo
import rogue.User
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.MongoIdentifier
import com.foursquare.rogue.LiftRogue._
import org.scalatest.BeforeAndAfterAll
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Defines single Fongo DB instance, so that all tests
 * would use the same DB.
 * It creates Fongo DB and registers it as a default
 * Mongo DB, so that code in tested classes will use
 * this connection transparently. This is achieved by
 * referencing the same DB and collection names, but
 * creating connection once either in test framework
 * with Fongo, or in production with Mongo.
 */
object FongoTest extends MongoIdentifier {
  override def jndiName = "rogue_mongo"

  private val dbName = "newdb"
  private val fongo = new Fongo("server1")

  def getDB = fongo.getDB(dbName)

  def connectToMongo = {
    MongoDB.defineDb(FongoTest, fongo.getMongo, dbName)
  }

  def disconnectFromMongo = {
    fongo.getMongo.close
    MongoDB.close
  }
}

@RunWith(classOf[JUnitRunner])
class FongoSuite extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  /* alternatively to 'beforeAll' we can define DB connection like this:
   * FongoTest.connectToMongo
   * val collection = FongoTest.getDB.getCollection("newcollection")
   * in constructor body to avoid using var.
   * We can then drop collection in 'before' or 'after' method.
   */

  val collectionName = "users"
  var collection = FongoTest.getDB.getCollection(collectionName)

  override def beforeAll() = {
    FongoTest.connectToMongo
  }

  override def afterAll() = {
    FongoTest.disconnectFromMongo
  }

  before {
    collection = FongoTest.getDB.getCollection(collectionName)
  }

  after {
    collection.drop()
  }

  test("Insert adds document to collection") {
    assert(0 === collection.count)
    val user = User.createRecord.username("testuser").password("testpass").points(111).email("test@test.com")
    user.save(true)
    assert(1 === collection.count)
    assert(user.asDBObject === collection.findOne)
    // converting to DBObject because User does not implement equals properly and is not a case class,
    // so assert would fail otherwise:
    assert(user.asDBObject === User.find(user.asDBObject).get.asDBObject)
  }

  test("Update updates a field") {
    User.createRecord.username("testuser").password("testpass").points(111).email("test@test.com").save
    assert(1 === User.where(_.username eqs "testuser").count)
    // find and update all users who match the condition:
    User.where(_.username eqs "testuser").modify(_.username setTo "admin").updateMulti() // or updateOne
    assert(0 === User.where(_.username eqs "testuser").count)
    assert(1 === User.where(_.username eqs "admin").count)
  }

}