package albin.techship

import albin.techship.model.Article
import albin.techship.model.Attributes
import albin.techship.model.Category
import albin.techship.model.Price
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val articles: ArrayList<Article> by lazy {
        ArrayList<Article>()
    }
    private val categories: ArrayList<Category>  by lazy {
        ArrayList<Category>()
    }
    private val attributes: ArrayList<Attributes>  by lazy {
        ArrayList<Attributes>()
    }
    private val prices: ArrayList<Price>  by lazy {
        ArrayList<Price>()
    }

    var teknik: String = ""

    private lateinit var allArticles: Array<Array<Any>>
    private lateinit var allCategories: Array<Array<Any>>
    private lateinit var allAttributes: Array<Array<Any>>
    private lateinit var allPrices: Array<Array<Any>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        createAllModels()
        createProducts()
        val spinner: Spinner = spinner
        ArrayAdapter.createFromResource(this, R.array.tekniker, android.R.layout.simple_spinner_item).also {
            arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = this
        }
        search_btn.setOnClickListener {
            val maxTemp = maxTemp.text.toString()
            val minTemp = minTemp.text.toString()
            val pris = pris.text.toString()
            searchProduct(teknik, Integer.parseInt(maxTemp), Integer.parseInt(minTemp), Integer.parseInt(pris)) }
    }

    /** Simulerar get funktionerna som retunerar array med arrays*/
    private fun createAllModels() {
        allArticles = arrayOf(
            arrayOf(15, "Ericsson F3307", 3, "A very flexible and good module", 94),
            arrayOf(20, "Huawei C5457", 2, "A very flexible and good module", 74),
            arrayOf(10, "Sierra MC7455", 3, "Sierra Wireless MC7455 is the first Cat 6 module in the Sierra Wireless MC-series", 80)
        )
        allCategories = arrayOf(
            arrayOf(3, "Cellular modules", "cellular-modules", "Embedded wireless modules and modems"),
            arrayOf(2, "Wifi modules", "wifi-modules", "Embedded wireless modules and modems")
        )
        allAttributes = arrayOf(
            arrayOf(2, 15, "temperature", "celcius", "-20", "60"),
            arrayOf(3, 20, "temperature", "celcius", "-5", "40"),
            arrayOf(1, 10, "temperature", "celcius", "-40", "85")
        )
        allPrices = arrayOf(
            arrayOf(4,15,50,"USD"),
            arrayOf(2,20,30,"USD"),
            arrayOf(1,10,110,"USD")
        )
    }

    private fun createProducts() {
        allArticles.forEach {
            val article = Article(it[0] as Int, it[1] as String, it[2] as Int, it[3] as String, it[4] as Int)
            articles.add(article)
        }
        allCategories.forEach {
            val category = Category(it[0] as Int, it[1] as String, it[2] as String, it[3] as String)
            categories.add(category)
        }
        allAttributes.forEach {
            val attribute = Attributes(it[0] as Int, it[1] as Int, it[2] as String, it[3] as String, it[4] as String, it[5] as String)
            attributes.add(attribute)
        }
        allPrices.forEach {
            val price = Price(it[0] as Int, it[1] as Int, it[2] as Int, it[3] as String)
            prices.add(price)
        }
    }

    private fun get_article_category(category_id: Int): Category? {
        //hitta och retunera en kategori object som matchar @category_id
        return categories.find { it.id == category_id }
    }

    private fun get_article_attributes(article_id: Int): Attributes? {
        //hitta och retunera en attribut object som matchar @article_id
        return attributes.find { it.article_id == article_id }
    }

    private fun get_price(article_id: Int): Price? {
        //hitta och retunera ett pris object som matchar @article_id
        return prices.find { it.article_id == article_id }
    }

    /** Söker igenom alla artiklar och kollar om de givna parametrarna stämmer med produkten */
    private fun searchProduct(neededTech: String, maxTemp: Int, minTemp: Int, pris: Int) {
        resultat.text = ""
        val foundProducts = arrayListOf<Article>()
        articles.forEach {
            //kolla kategorin
            if(checkCategory(get_article_category(it.category_id)?.name, neededTech)) {
                //kategorin matchar för denna produkt
                val productMaxTemp = Integer.parseInt(get_article_attributes(it.id)!!.max_value)
                val productMinTemp = Integer.parseInt(get_article_attributes(it.id)!!.min_value)
                if(checkTemperature(maxTemp, minTemp, productMaxTemp, productMinTemp)) {
                    //tempen är i rätt intervall
                    if(checkPrice(get_price(it.id)?.price, pris)) {
                        //priset är inom kundens prisklass, produkt hittad
                        foundProducts.add(it)
                    }
                }
                else { return@forEach }
            }
        }
        if(foundProducts.size > 0) {
            Toast.makeText(this, "Produkter hittade: " + foundProducts.size, Toast.LENGTH_LONG).show()
            foundProducts.forEach { product -> Log.d("TechShip", product.name) }
            displayResult(foundProducts)
        } else { Toast.makeText(this, "Ingen produkt hittades" , Toast.LENGTH_LONG).show() }
    }

    /** Kolla om kategorierna är samma */
    private fun checkCategory(categoryOne: String?, categoryTwo: String?): Boolean = categoryOne == categoryTwo


    /** Kolla om max temperaturen för produkten är högre än den givna max tempen, samt om min tempen är större än den givna min tempen*/
    private fun checkTemperature(maxTemp: Int, minTemp: Int, productMaxTemp: Int, productMinTemp: Int): Boolean = productMaxTemp > maxTemp && productMinTemp < minTemp


    /**  Kolla om max priset är mindre än priset för produkten */
    private fun checkPrice(produktPris: Int?, maxPris: Int): Boolean = maxPris >= produktPris!!


    /** Visar namnet på produkterna som hittades i en textView */
    private fun displayResult(products: ArrayList<Article>) {
        var resultString: String = ""
        products.forEach {
            resultString += it.name + "\n"
        }
        resultat.text = resultString
    }



    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        Toast.makeText(this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show()
        teknik = parent.getItemAtPosition(pos).toString()
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }
}
