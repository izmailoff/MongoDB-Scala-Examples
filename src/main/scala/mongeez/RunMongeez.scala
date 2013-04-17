package mongeez

import org.mongeez.Mongeez
import com.mongodb.Mongo
import org.springframework.core.io.ClassPathResource // EEEWW, not Scala sexy
// log4j dependencies and apache commons are not clearly defined for SBT

/**
 * This is a test app to see how mongeez works. Usually db migration would be
 * done as part of application startup/init.
 */
object RunMongeez extends App {
  val host = "localhost"
  val port = 27017
  val mongeez = new Mongeez
  val mainFile = new ClassPathResource("mongeez.xml")
  mongeez.setFile(mainFile)
  mongeez.setMongo(new Mongo(host, port))
  mongeez.setDbName("newdb")
  mongeez.process()
}
