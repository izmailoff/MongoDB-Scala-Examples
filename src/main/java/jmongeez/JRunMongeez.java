package jmongeez;

import java.net.UnknownHostException;

import org.mongeez.Mongeez;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.mongodb.Mongo;

public class JRunMongeez {
	public static void main(String[] args) throws UnknownHostException {
		Mongeez mongeez = new Mongeez();
		Resource mainFile = new ClassPathResource("mongeez.xml");
		mongeez.setFile(mainFile);
		String host = "localhost";
		int port = 27017;
		mongeez.setMongo(new Mongo(host, port));
		mongeez.setDbName("newdb");
		mongeez.process();
	}
}
