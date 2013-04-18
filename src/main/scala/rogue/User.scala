package rogue

import net.liftweb.mongodb.record.MongoRecord
import com.foursquare.rogue.index.{Asc, Desc, IndexedRecord, IndexModifier}
import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record.MongoMetaRecord
import net.liftweb.record.field.StringField
import net.liftweb.record.field.LongField
import net.liftweb.mongodb.MongoIdentifier
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.mongodb.record.BsonMetaRecord
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.mongodb.record.field.BsonRecordListField
import com.foursquare.rogue.HasMongoForeignObjectId
import net.liftweb.mongodb.record.field.ObjectIdField
import net.liftweb.mongodb.record.MongoId

object RogueMongo extends MongoIdentifier {
  override def jndiName = "test"

  private var mongo: Option[Mongo] = None

  def connectToMongo = {
    val MongoPort = 27017
    mongo = Some(new Mongo(new ServerAddress("localhost", MongoPort)))
    MongoDB.defineDb(RogueMongo, mongo.get, "newdb")
  }

  def disconnectFromMongo = {
    mongo.foreach(_.close)
    MongoDB.close
    mongo = None
  }
  
  def connection = {
    MongoDB.getDb(RogueMongo).get
  }
}

/**
 * User Address - used in User as embedded doc
 */
class Address private () extends BsonRecord[Address] {
  def meta = Address

  object street extends StringField(this, 255)
  object city extends StringField(this, 127)
}
object Address extends Address with BsonMetaRecord[Address]

/**
 * User - main class, uses Address as embedded doc.
 * Uses BsonRecord for embedded doc. Another alternative is JsonObject.
 * For more details see:
 * https://www.assembla.com/spaces/liftweb/wiki/Mongo_Record_Embedded_Objects
 */
class User extends MongoRecord[User] with MongoId[User] with IndexedRecord[User] { // Bummer: ObjectIdPk not compatible with HasMongoForeignObjectId
  def meta = User
  object username extends StringField(this, 255)
  object password extends StringField(this, 255)
  object points extends LongField(this)
  object email extends StringField(this, 255)
  object homeAddress extends BsonRecordField(this, Address)
  object otherAddresses extends BsonRecordListField(this, Address) // saved as JSON array
}
object User extends User with MongoMetaRecord[User] {
  override def collectionName = "users"

  trait FK[T <: FK[T]] extends MongoRecord[T] {
    self: T =>
      object userId extends ObjectIdField[T](this) with HasMongoForeignObjectId[User] {}
  }
}

/**
 * User posts
 */
class Post extends MongoRecord[Post] with ObjectIdPk[Post] with IndexedRecord[Post] with User.FK[Post] {
  def meta = Post
  object title extends StringField(this, 127)
  object body extends StringField(this, 1023)
}
object Post extends Post with MongoMetaRecord[Post] {
  override def collectionName = "posts"
}