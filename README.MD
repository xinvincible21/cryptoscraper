# cryptoscraper

Program to scrape crypto data from gemini.com

1. Install Mongo
Open iTerm2 (or terminal if iTerm2 is not installed)
Run the following commands
   brew install mongodb-community@6.0
   brew services start mongodb-community@6.0

2. Test the Mongo DB using mongo shell
   cd /usr/local/Cellar/mongosh/1.6.2/bin
   sh mongosh

Helpful mongo shell commands
show dbs
use cryptoscraper
show collections
db.crypto_2023_02_09.deleteMany({})
db.crypto_2023_02_09.insert({name: "Ether", symbol: "ETHUSD", price: 1646.17})
db.crypto_2023_02_09.find().pretty()

Note IntelliJ can be used to directly run Scala objects below instead of using a FAT JAR. Make sure the scala plugin is 
installed if using IntelliJ

3. Create a FAT JAR
Assuming the project is in ~/cryptoscraper
Run the following commands in iTerm2
   cd ~/cryptoscraper
   sbt clean compile assembly
   
   make sure the FAR JAR is executable by running the following command
   chmod 755 ~/cryptoscraper/target/scala_2.12/cryptoscraper-assembly-0.1.0-SNAPSHOT.jar

This produces cryptoscraper FAT JAR at
~/cryptoscraper/cryptoscraper/target/scala_2.12/cryptoscraper-assembly-0.1.0-SNAPSHOT.jar

4. Run test class that retrieves cryptos from Mongo DB by running the following command
   java -cp ~/cryptoscraper/target/scala-2.12/cryptoscraper-assembly-0.1.0-SNAPSHOT.jar com.invincible.TestCode

5. Run the scraper by running the following command

   java -cp ~/cryptoscraper/target/scala-2.12/cryptoscraper-assembly-0.1.0-SNAPSHOT.jar com.invincible.FindGeminiPrices

   FindGeminiPrices.scala scrapes the crypto name, symbol and price from https://gemini.com/prices and is reliable 
   because it uses XPaths. The challenge with scrapping this page is that the page changes often, currently lists
   134 cryptos and requires scrolling to scrape properly.

6. Run the GraphQL server by running the following command

   java -cp ~/cryptoscraper/target/scala-2.12/cryptoscraper-assembly-0.1.0-SNAPSHOT.jar com.invincible.CryptoApp

7. Test the GraphQL API by using GraphiQL

GraphiQL
Open a browser and go to the following URL
http://localhost:8088/graphiql

Example queries
   List all cryptos

   query{ cryptos() { name,price,symbol } }

   Query by symbol

   query{ crypto(symbol: "BTCUSD") { name,price,symbol } }

   query{ crypto(symbol: "ETHUSD") { name,price,symbol } }

8. Run the program daily at 00:00 using crontab

make sure run.sh and run_server.sh are executable

chmod 755 ~/cryptoscraper/src/main/resources/run.sh

chmod 755 ~/cryptoscraper/src/main/resources/run_server.sh

Currently the graphQL api loads the data from mongodb once at startup. As a workaround to reload the data, the graphQL 
server is restarted after successful completion of FindGeminiPrices by run.sh
As a future improvement I'm looking into dynamically reloading the graphQL api data without restarting the server.

crontab -e

Insert the following

0 0 * * * ~/cryptoscraper/src/main/resources/run.sh > /tmp/crypto.log
