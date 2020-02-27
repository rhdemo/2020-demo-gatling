import java.io.{File, FileInputStream}
import java.util.{Base64, Random, UUID}

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.util.parsing.json.JSON


class E2ESimulation extends Simulation {

  val DEV = "game-frontend.apps.summit-demo2.openshift.redhatkeynote.com/socket"
  val LIVE = "game-frontend.apps.summit-demo2.openshift.redhatkeynote.com/socket"

  val GUESSES = Array(
    "good.json"
  )

  val BAD_GUESS = "bad.json"

  def getHost(arg: String): String = arg match {
    case "DEV" => DEV
    case "LIVE" => LIVE
    case _ => arg
  }

  val host: String = getHost(System.getProperty("host", LIVE))
  val numUsers: Int = Integer.getInteger("users", 1).toInt
  val numGuesses: Int = Integer.getInteger("guesses", 1).toInt
  val percentBadGuesses: Int = Integer.getInteger("percentBadGuesses", 100).toInt

  val protocol: HttpProtocolBuilder = http
    .baseUrl("http://" + host)
    .acceptHeader("*/*")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling")
    .wsBaseUrl("ws://" + host)
    .wsReconnect
    .wsMaxReconnects(1)

  val scn: ScenarioBuilder = scenario("Connection-Scenario")
    .exec(Connection.connect)
    .pause(1)
    .exec(Guess.guess)
    .pause(1)
    .exec(ws("Close").close)

  setUp(
    scn.inject(rampUsers(numUsers) during (5 seconds)).protocols(protocol)
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
                .await(5 seconds)(checkConfiguration))
            } {
              exec(ws("Reconnect")
                .sendText("""{"type": "init", "gameId": "${gameId}", "playerId": "${playerId}"}""")
                .await(5 seconds)(checkConfiguration))
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
            .await(7 seconds)(checkGuess)
          )
          .exec(session => {
            val guess = session("guess").as[String]
//            println(guess)
            session
          })
          .pause(4 + random.nextInt(3))
      }
  }

}
