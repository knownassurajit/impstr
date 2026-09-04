package com.knownassurajit.app.game.impstr.data

/**
 * Bundled, offline regional word packs.
 *
 * Country is an ISO 3166-1 alpha-2 code from coarse location or device locale.
 * Packs stay family-friendly and biased toward words people at that place
 * actually talk about.
 */
object RegionalWordPacks {
    fun overlayFor(countryCode: String?): WordOverlay {
        val code = countryCode?.uppercase()?.take(2).orEmpty()
        if (code.isBlank()) return WordOverlay.Empty
        val pack = packs[code] ?: groupedPack(code) ?: return WordOverlay.Empty
        return pack.toOverlay(code)
    }

    fun labelFor(countryCode: String?): String? {
        val code = countryCode?.uppercase()?.take(2).orEmpty()
        if (code.isBlank()) return null
        return packs[code]?.label ?: groupedPack(code)?.label
    }

    private fun groupedPack(code: String): RegionPack? =
        when (code) {
            "IE" -> packs["GB"]
            "NZ" -> packs["AU"]
            "AT", "CH" -> packs["DE"]
            "BE" -> packs["FR"]
            "SA", "QA", "KW", "BH", "OM" -> packs["AE"]
            "GH", "KE" -> packs["NG"]
            "MY" -> packs["SG"]
            else -> null
        }

    private val packs: Map<String, RegionPack> =
        listOf(
            RegionPack(
                codes = setOf("IN"),
                label = "India",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities", "Movies & TV Shows"),
                localWords =
                    listOf(
                        "Biryani", "Samosa", "Dosa", "Idli", "Vada Pav", "Pani Puri", "Chai",
                        "Lassi", "Gulab Jamun", "Jalebi", "Butter Chicken", "Naan", "Paratha",
                        "Masala Dosa", "Pav Bhaji", "Kulfi", "Filter Coffee", "Thali", "Sambar",
                        "Mango", "Cricket", "IPL", "Auto Rickshaw", "Diwali", "Holi", "Rangoli",
                        "Taj Mahal", "Mumbai", "Delhi", "Bengaluru", "Kolkata", "Chennai",
                        "Hyderabad", "Goa", "Jaipur", "Bollywood", "Kohli", "Dhoni", "Peacock",
                        "Lotus", "Kurta", "Sari", "Autorickshaw", "Train", "Monsoon",
                    ),
                extras =
                    mapOf(
                        "Food & Drinks" to listOf("Biryani", "Samosa", "Dosa", "Chai", "Gulab Jamun", "Naan"),
                        "Sports & Activities" to listOf("Cricket", "Kabaddi", "Hockey"),
                        "World Cities" to listOf("Mumbai", "Delhi", "Bengaluru", "Kolkata", "Chennai"),
                    ),
            ),
            RegionPack(
                codes = setOf("PK"),
                label = "Pakistan",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities"),
                localWords =
                    listOf(
                        "Biryani", "Nihari", "Chapli Kebab", "Chai", "Lassi", "Jalebi",
                        "Cricket", "Lahore", "Karachi", "Islamabad", "Hunza", "Truck Art",
                        "Shalwar Kameez", "Eid", "Hockey", "Mango",
                    ),
            ),
            RegionPack(
                codes = setOf("BD"),
                label = "Bangladesh",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities"),
                localWords =
                    listOf(
                        "Hilsa", "Biryani", "Rasgulla", "Chai", "Rickshaw", "Dhaka",
                        "Sundarbans", "Cricket", "Eid", "Monsoon", "Lungis", "Tea Garden",
                    ),
            ),
            RegionPack(
                codes = setOf("US"),
                label = "United States",
                suggested = listOf("Around You", "Sports & Activities", "Movies & TV Shows", "Food & Drinks"),
                localWords =
                    listOf(
                        "Burger", "Hot Dog", "BBQ", "Apple Pie", "Donut", "Buffalo Wings",
                        "Baseball", "Super Bowl", "Basketball", "Hollywood", "Broadway",
                        "Thanksgiving", "Fourth of July", "Yellow Bus", "Diner", "New York",
                        "Los Angeles", "Chicago", "Miami", "Las Vegas", "Grand Canyon",
                        "Statue of Liberty", "Cheerleading", "Prom", "Pickup Truck",
                    ),
                extras =
                    mapOf(
                        "Sports & Activities" to listOf("Baseball", "American Football", "Basketball"),
                        "Food & Drinks" to listOf("Burger", "BBQ", "Apple Pie", "Hot Dog"),
                    ),
            ),
            RegionPack(
                codes = setOf("GB"),
                label = "United Kingdom",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities"),
                localWords =
                    listOf(
                        "Tea", "Fish and Chips", "Full English", "Scone", "Biscuit",
                        "Football", "Premier League", "Cricket", "Wimbledon", "London",
                        "Edinburgh", "Double Decker", "Red Phone Box", "Big Ben",
                        "Sunday Roast", "Pub", "Queue", "Umbrella", "Rain",
                    ),
            ),
            RegionPack(
                codes = setOf("CA"),
                label = "Canada",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities", "Nature & Landscapes"),
                localWords =
                    listOf(
                        "Maple Syrup", "Poutine", "Hockey", "Tim Hortons", "Moose",
                        "Beaver", "Toronto", "Vancouver", "Montreal", "Niagara Falls",
                        "Canoe", "Snow", "Ice Hockey", "Mountie",
                    ),
            ),
            RegionPack(
                codes = setOf("AU"),
                label = "Australia",
                suggested = listOf("Around You", "Animals", "Sports & Activities", "Food & Drinks"),
                localWords =
                    listOf(
                        "Kangaroo", "Koala", "Vegemite", "Barbecue", "Surfboard",
                        "Sydney", "Melbourne", "Great Barrier Reef", "Boomerang",
                        "Cricket", "AFL", "Ute", "Tim Tam", "Lamington",
                    ),
            ),
            RegionPack(
                codes = setOf("JP"),
                label = "Japan",
                suggested = listOf("Around You", "Food & Drinks", "Movies & TV Shows", "Technology & Gadgets"),
                localWords =
                    listOf(
                        "Sushi", "Ramen", "Onigiri", "Matcha", "Tempura", "Tokyo",
                        "Kyoto", "Mount Fuji", "Shinkansen", "Cherry Blossom",
                        "Anime", "Karaoke", "Nintendo", "Bullet Train", "Torii",
                        "Origami", "Kimono", "Onsen",
                    ),
            ),
            RegionPack(
                codes = setOf("KR"),
                label = "South Korea",
                suggested = listOf("Around You", "Food & Drinks", "Movies & TV Shows"),
                localWords =
                    listOf(
                        "Kimchi", "K-Pop", "Seoul", "Bibimbap", "Korean BBQ",
                        "Hanbok", "BTS", "Palaces", "Subway", "Ramyeon",
                        "Tteokbokki", "PC Bang", "K-Drama",
                    ),
            ),
            RegionPack(
                codes = setOf("BR"),
                label = "Brazil",
                suggested = listOf("Around You", "Sports & Activities", "Food & Drinks"),
                localWords =
                    listOf(
                        "Football", "Carnival", "Samba", "Rio", "Amazon", "Copacabana",
                        "Feijoada", "Açaí", "Christ the Redeemer", "Pelé", "Beach",
                        "Caipirinha", "Churrasco",
                    ),
            ),
            RegionPack(
                codes = setOf("MX"),
                label = "Mexico",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities"),
                localWords =
                    listOf(
                        "Tacos", "Guacamole", "Mariachi", "Piñata", "Mexico City",
                        "Chichen Itza", "Football", "Churros", "Salsa", "Sombrero",
                        "Day of the Dead", "Tamales", "Quesadilla",
                    ),
            ),
            RegionPack(
                codes = setOf("DE"),
                label = "Germany",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities"),
                localWords =
                    listOf(
                        "Football", "Berlin", "Oktoberfest", "Pretzel", "Bratwurst",
                        "Autobahn", "Castle", "Black Forest", "Christmas Market",
                        "Beethoven", "Neuschwanstein", "Currywurst",
                    ),
            ),
            RegionPack(
                codes = setOf("FR"),
                label = "France",
                suggested = listOf("Around You", "Food & Drinks", "World Cities"),
                localWords =
                    listOf(
                        "Baguette", "Croissant", "Eiffel Tower", "Paris", "Cheese",
                        "Louvre", "Tour de France", "Macaron", "Perfume", "Cannes",
                        "Provence", "Crepe",
                    ),
            ),
            RegionPack(
                codes = setOf("ES"),
                label = "Spain",
                suggested = listOf("Around You", "Food & Drinks", "Sports & Activities"),
                localWords =
                    listOf(
                        "Paella", "Tapas", "Flamenco", "Madrid", "Barcelona",
                        "Football", "Siesta", "Churros", "Gaudi", "Ibiza",
                    ),
            ),
            RegionPack(
                codes = setOf("IT"),
                label = "Italy",
                suggested = listOf("Around You", "Food & Drinks", "World Cities"),
                localWords =
                    listOf(
                        "Pizza", "Pasta", "Rome", "Venice", "Gelato", "Colosseum",
                        "Ferrari", "Espresso", "Gondola", "Leaning Tower", "Opera",
                    ),
            ),
            RegionPack(
                codes = setOf("AE"),
                label = "Gulf",
                suggested = listOf("Around You", "World Cities", "Food & Drinks"),
                localWords =
                    listOf(
                        "Dubai", "Burj Khalifa", "Dates", "Desert", "Falcon",
                        "Mosque", "Souk", "Camel", "Arabic Coffee", "Mall",
                        "Fountain", "Dhow",
                    ),
            ),
            RegionPack(
                codes = setOf("NG"),
                label = "Nigeria",
                suggested = listOf("Around You", "Food & Drinks", "Movies & TV Shows"),
                localWords =
                    listOf(
                        "Jollof Rice", "Lagos", "Afrobeats", "Football", "Nollywood",
                        "Ankara", "Suya", "Abuja", "Drum",
                    ),
            ),
            RegionPack(
                codes = setOf("PH"),
                label = "Philippines",
                suggested = listOf("Around You", "Food & Drinks"),
                localWords =
                    listOf(
                        "Adobo", "Jeepney", "Manila", "Halo-Halo", "Basketball",
                        "Karaoke", "Mango", "Tricycle", "Fiesta",
                    ),
            ),
            RegionPack(
                codes = setOf("ID"),
                label = "Indonesia",
                suggested = listOf("Around You", "Food & Drinks", "Nature & Landscapes"),
                localWords =
                    listOf(
                        "Nasi Goreng", "Bali", "Jakarta", "Komodo", "Batik",
                        "Satay", "Volcano", "Tempeh", "Becak",
                    ),
            ),
            RegionPack(
                codes = setOf("TR"),
                label = "Turkey",
                suggested = listOf("Around You", "Food & Drinks", "World Cities"),
                localWords =
                    listOf(
                        "Istanbul", "Kebab", "Baklava", "Tea", "Bazaar",
                        "Hot Air Balloon", "Simit", "Bosphorus", "Lokum",
                    ),
            ),
            RegionPack(
                codes = setOf("SG"),
                label = "Singapore",
                suggested = listOf("Around You", "Food & Drinks", "World Cities"),
                localWords =
                    listOf(
                        "Chili Crab", "Laksa", "Hawker Centre", "Marina Bay",
                        "Merlion", "MRT", "Hainanese Chicken", "Orchid",
                    ),
            ),
            RegionPack(
                codes = setOf("ZA"),
                label = "South Africa",
                suggested = listOf("Around You", "Animals", "Sports & Activities"),
                localWords =
                    listOf(
                        "Safari", "Braai", "Rugby", "Cape Town", "Lion",
                        "Table Mountain", "Biltong", "Springbok", "Kruger",
                    ),
            ),
            RegionPack(
                codes = setOf("EG"),
                label = "Egypt",
                suggested = listOf("Around You", "World Cities", "Nature & Landscapes"),
                localWords =
                    listOf(
                        "Pyramid", "Sphinx", "Cairo", "Nile", "Pharaoh",
                        "Camel", "Desert", "Felucca",
                    ),
            ),
            RegionPack(
                codes = setOf("NL"),
                label = "Netherlands",
                suggested = listOf("Around You", "Food & Drinks", "World Cities"),
                localWords =
                    listOf(
                        "Windmill", "Tulip", "Bicycle", "Amsterdam", "Cheese",
                        "Canal", "Stroopwafel", "Clogs",
                    ),
            ),
            RegionPack(
                codes = setOf("SE"),
                label = "Sweden",
                suggested = listOf("Around You", "Food & Drinks", "Nature & Landscapes"),
                localWords =
                    listOf(
                        "Meatballs", "IKEA", "Northern Lights", "Fika",
                        "Stockholm", "Moose", "Cinnamon Bun",
                    ),
            ),
            RegionPack(
                codes = setOf("PL"),
                label = "Poland",
                suggested = listOf("Around You", "Food & Drinks"),
                localWords =
                    listOf(
                        "Pierogi", "Warsaw", "Bagel", "Castle", "Forest",
                        "Dumpling", "Vistula",
                    ),
            ),
        ).flatMap { pack -> pack.codes.map { it to pack } }
            .toMap()
}

private data class RegionPack(
    val codes: Set<String>,
    val label: String,
    val suggested: List<String>,
    val localWords: List<String>,
    val extras: Map<String, List<String>> = emptyMap(),
) {
    fun toOverlay(countryCode: String): WordOverlay =
        WordOverlay(
            version = 1,
            regionCode = countryCode,
            regionLabel = label,
            suggestedCategories = suggested,
            extraCategories = mapOf(CatalogSnapshot.LOCAL_CATEGORY to localWords),
            extraWordsByCategory = extras,
            bonusEasyWords = localWords,
        )
}
