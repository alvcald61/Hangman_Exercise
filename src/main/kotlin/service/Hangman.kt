import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class Ahorcado() {
    private lateinit var quote : Quote
    private lateinit var unrevealedQuote : CharArray
    private val characters : MutableSet<Char> = mutableSetOf()
    private val categories = mutableListOf<String>()
    private var attempts : Int = 0
    private var maxAttempts : Int = 5
    private var won : Boolean = false

    init {
        categories.addAll(
            listOf("age",
                    "alone",
                    "amazing",
                    "anger",
                    "architecture",
                    "art",
                    "attitude",
                    "beauty",
                    "best",
                    "birthday",
                    "business",
                    "car",
                    "change",
                    "communications",
                    "computers",
                    "cool",
                    "courage",
                    "dad",
                    "dating",
                    "death",
                    "design",
                    "dreams",
                    "education",
                    "environmental",
                    "equality",
                    "experience",
                    "failure",
                    "faith",
                    "family",
                    "famous",
                    "fear",
                    "fitness",
                    "food",
                    "forgiveness",
                    "freedom",
                    "friendship",
                    "funny",
                    "future",
                    "god",
                    "good",
                    "government",
                    "graduation",
                    "great",
                    "happiness",
                    "health",
                    "history",
                    "home",
                    "hope",
                    "humor",
                    "imagination",
                    "inspirational",
                    "intelligence",
                    "jealousy",
                    "knowledge",
                    "leadership",
                    "learning",
                    "legal",
                    "life",
                    "love",
                    "marriage",
                    "medical",
                    "men",
                    "mom",
                    "money",
                    "morning",
                    "movies",
                    "success")
        )
    }

    fun run(){
        val category = getCategory()
        var revealedCharacters: Int
        var character: Char
        initRandomQuote(category)
        println("Welcome to Hangman! You have $maxAttempts attempts to guess the quote. Good luck!")
//        printHuman()
        while(attempts < maxAttempts && !won){
            try {
                println(unrevealedQuote)
                showSelectedCharacters()
                character = getCharacter()
                checkValidCharacter(character)
                revealedCharacters = revealCharacter(character)
                showRevealSummary(character, revealedCharacters)
                checkWin()
            } catch (e: Exception) {
                println(e.message)
            }
        }
        println("The quote was: ${quote.quote}")
    }

    private fun showSelectedCharacters() {
        print("Selected characters: ")
        characters.forEach { print("$it ") }
        println()
    }

    private fun checkWin() {
        if(!unrevealedQuote.contains('_')){
            won = true
            println("You won!")
        }
    }

    private fun revealCharacter(character: Char) : Int {
        var numCharReveled = 0
        for(i in quote.quote.indices){
            if(quote.quote[i].lowercaseChar() == character){
                unrevealedQuote[i] = quote.quote[i]
                numCharReveled++
            }
        }
        return numCharReveled
    }

    private fun showRevealSummary(character: Char, numCharReveled: Int) {
        if (numCharReveled == 0){
            attempts++
            println("The character $character is not in the quote")
            println("You have ${maxAttempts - attempts} attempts left")
        }
        else{
            println("The character $character is in the quote $numCharReveled times")
        }
        println()
    }

    private fun checkValidCharacter(character: Char) {
        if(!character.isLetter()){
            throw Exception("The character $character is not a letter\n")
        }
        if (characters.contains(character)){
            throw RepeatedCharacterException("The character $character has already been selected\n")
        }
        characters.add(character)
    }

    private fun getCharacter(): Char {
        println("Select a character")
        val character = readlnOrNull() ?: throw Exception("Invalid character")
        return character[0]
    }
    
    private fun getCategory() : String{
        val randomNum = kotlin.random.Random.nextInt(0, categories.size)
        return categories[randomNum]
    }
    
    private fun initRandomQuote(category: String){
        val quotes = fetchQuotes(category)
        quote = quotes[0]
        unrevealedQuote = quote.quote.map { if(it.isLetter()) '_' else it }.toCharArray()
    }

    private fun fetchQuotes(category: String): List<Quote> {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.api-ninjas.com/v1/quotes?category=$category")
            .get()
            .addHeader("X-Api-Key", "1tBudgd95h7zzXfgij+JhA==QL9AllIybY9GsDTi")
            .build()

        return client.newCall(request).execute().use(this::handleResponse)
    }

    private fun handleResponse(response: Response): List<Quote> {
        val gson = Gson()
        return gson.fromJson(response.body!!.string(), Array<Quote>::class.java).toList()
    }

}