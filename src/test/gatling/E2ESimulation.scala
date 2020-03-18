import java.io.{File, FileInputStream}
import java.util.{Base64, Random, UUID}

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.util.parsing.json.JSON


class E2ESimulation extends Simulation {
  val GUESSES = Array(
    "good.json"
  )

  val BAD_GUESS = "bad.json"

  val host: String = sys.env("SOCKET_ADDRESS")
  val numUsers: Int = sys.env("USERS").toInt
  val numGuesses: Int = sys.env("GUESSES").toInt
  val percentBadGuesses: Int = sys.env("PERCENT_BAD_GUESSES").toInt 

  val protocol: HttpProtocolBuilder = http
    .baseUrl("http://" + host)
    .acceptHeader("*/*")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling")
    .wsBaseUrl("ws://" + host)
    .wsReconnect
    .wsMaxReconnects(100)

  val scn: ScenarioBuilder = scenario("Connection-Scenario")
    .exec(Connection.connect)
    .pause(1)
    .exec(Guess.guess)
    .pause(1)
    .exec(ws("Close").close)

  setUp(
    scn.inject(rampUsers(numUsers) during (1 seconds)).protocols(protocol)
  )


  object Connection {
    val checkConfiguration = ws.checkTextMessage("checkConfiguration")
      .matching(
        jsonPath("$.type").is("player-configuration")
      )
      .check(
        jsonPath("$.player.id").saveAs("playerId"),
        jsonPath("$.game.id").saveAs("gameId")
      )
    val connect: ChainBuilder =
        exec(ws("Connect to /socket").connect("")
          .onConnected(
            doIfOrElse(session => session("playerId").asOption[String].forall(_.isEmpty)) {
              exec(ws("Connect")
                .sendText("""{"type": "init"}""")
                .await(1 seconds)(checkConfiguration))  
            } {
              exec(ws("Reconnect")
                .sendText("""{"type": "init", "gameId": "${gameId}", "playerId": "${playerId}"}""")
                .await(1 seconds)(checkConfiguration))
            }
          ))
  }


  object Guess {

    private val random = new Random()

    val checkGuess = ws.checkTextMessage("checkGuess")
      .matching(
        jsonPath("$.type").is("player-configuration")
      )
      .check(jsonPath("$").saveAs("guess"))
      .check(jsonPath("$.player").exists)
      .check(jsonPath("$.game").exists)


    val guess: ChainBuilder =
      repeat(numGuesses, "attempts") {
        exec(session => {
          val uuid = UUID.randomUUID().toString
          var guess = GUESSES(random.nextInt(GUESSES.length))

          // Check if we should instead do a bad guess
          if (random.nextInt(100) < percentBadGuesses) {
            guess = BAD_GUESS
          }
          println("Performing guess " + guess)

          session
            .set("guess", guess)

        })
          .exec(ws("Guess")
            .sendText(ElFileBody("${guess}"))
            .await(1 seconds)(checkGuess)
          )
          .exec(session => {
            val guess = session("guess").as[String]
            println(guess)
            session
          })
          .pause(1 + random.nextInt(3))
      }
  }

}
