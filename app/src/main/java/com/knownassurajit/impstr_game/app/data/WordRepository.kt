package com.knownassurajit.impstr_game.app.data

/**
 * In-memory, offline-first repository of theme categories and their words.
 *
 * The repository is intentionally immutable: there is no network refresh path,
 * and game logic should treat it as a pure data source. Stealth mode is
 * built around picking *two* distinct words from the *same* category so the
 * imposter receives a contextually similar decoy rather than a wildcard.
 */
object WordRepository {
    /** Special category key used to mean "pick from any real category". */
    const val RANDOM_CATEGORY: String = "Random Words"

    val categories: Map<String, List<String>> =
        mapOf(
            RANDOM_CATEGORY to
                listOf(
                    "Lion", "Pizza", "France", "Soccer", "Titanic", "Guitar",
                    "Einstein", "Coffee", "Python", "Bitcoin", "Sherlock", "Mars",
                    "Sushi", "Yoga", "Instagram", "Piano", "Ferrari", "Vampire",
                    "Ninja", "Pirate", "Superman", "Pokemon", "Minecraft", "Helicopter",
                    "Diamond", "Robot", "Castle", "Pyramid", "Telescope", "Compass",
                    "Dragon", "Wizard", "Unicorn", "Mermaid", "Ghost", "Zombie",
                    "Alien", "Dinosaur", "Rocket", "Submarine", "Parachute", "Balloon",
                    "Skateboard", "Backpack", "Crown", "Treasure", "Lighthouse", "Windmill",
                    "Fountain", "Bridge", "Tower", "Rainbow", "Thunder", "Hurricane",
                    "Eclipse", "Meteor", "Galaxy", "Museum", "Library", "Theater",
                    "Stadium", "Circus", "Carnival", "Passport", "Luggage", "Map",
                    "Camera", "Binoculars", "Mirror", "Candle", "Umbrella", "Anchor",
                    "Battery", "Microphone", "Trophy", "Medal", "Flag", "Sword", "Shield",
                    "Invisibility", "Teleportation", "Monopoly", "Catan", "Hamlet", "Odyssey"
                ),
            "Animals" to
                listOf(
                    "Lion", "Elephant", "Giraffe", "Penguin", "Dolphin", "Tiger",
                    "Kangaroo", "Panda", "Zebra", "Gorilla", "Wolf", "Bear",
                    "Eagle", "Shark", "Whale", "Octopus", "Snake", "Cheetah",
                    "Rhino", "Hippo", "Crocodile", "Koala", "Leopard", "Flamingo",
                    "Owl", "Fox", "Deer", "Rabbit", "Turtle", "Jellyfish",
                    "Parrot", "Peacock", "Buffalo", "Monkey", "Camel", "Seal",
                    "Bat", "Squirrel", "Hedgehog", "Swan", "Hawk", "Otter",
                    "Raccoon", "Platypus", "Sloth", "Lemur", "Chameleon", "Iguana",
                ),
            "Food & Drinks" to
                listOf(
                    "Pizza", "Burger", "Sushi", "Pasta", "Tacos", "Ice Cream",
                    "Chocolate", "Steak", "Salad", "Sandwich", "Curry", "Pancake",
                    "Waffle", "Donut", "Cookie", "Cake", "Ramen", "Burrito",
                    "Croissant", "Lasagna", "Nachos", "Cheesecake", "Brownie", "Popcorn",
                    "Hot Dog", "Pretzel", "Muffin", "Baguette", "Quesadilla", "Dumpling",
                    "Spaghetti", "Noodles", "French Fries", "Chicken Wings", "BBQ Ribs", "Kebab",
                    "Coffee", "Tea", "Smoothie", "Milkshake", "Lemonade", "Orange Juice",
                    "Matcha", "Bubble Tea", "Espresso", "Mojito", "Gelato", "Tiramisu",
                ),
            "Countries" to
                listOf(
                    "France", "Japan", "Brazil", "Egypt", "Italy", "Germany",
                    "Canada", "Australia", "India", "China", "Russia", "Mexico",
                    "Spain", "Argentina", "South Korea", "Greece", "Turkey", "Thailand",
                    "Portugal", "Netherlands", "Switzerland", "Poland", "Sweden", "Norway",
                    "Austria", "Belgium", "Ireland", "Denmark", "Finland", "Iceland",
                    "New Zealand", "Vietnam", "Chile", "Peru", "Colombia", "Morocco",
                    "Kenya", "Nigeria", "South Africa", "Indonesia", "Philippines", "Malaysia",
                ),
            "Sports & Activities" to
                listOf(
                    "Soccer", "Basketball", "Tennis", "Baseball", "Golf", "Volleyball",
                    "Cricket", "Rugby", "Hockey", "Boxing", "Swimming", "Cycling",
                    "Running", "Skiing", "Surfing", "Skateboarding", "Wrestling", "Karate",
                    "Archery", "Fencing", "Gymnastics", "Badminton", "Table Tennis", "Bowling",
                    "Martial Arts", "Rock Climbing", "Diving", "Snowboarding", "Ice Skating",
                    "Hiking", "Camping", "Parkour", "Pickleball", "Padel", "MMA",
                ),
            "Movies & TV Shows" to
                listOf(
                    "Titanic", "Avatar", "Inception", "Matrix", "Joker", "Frozen",
                    "Lion King", "Avengers", "Star Wars", "Harry Potter", "Jurassic Park",
                    "Spider-Man", "Batman", "Shrek", "Toy Story", "Finding Nemo",
                    "Godfather", "Gladiator", "Interstellar", "Iron Man", "Black Panther",
                    "Deadpool", "Wonder Woman", "Thor", "Aladdin", "Breaking Bad",
                    "Game of Thrones", "Stranger Things", "Friends", "The Office",
                    "Succession", "The Bear", "Wednesday", "House of the Dragon",
                    "The Last of Us", "Squid Game", "Oppenheimer", "Barbie", "Dune",
                ),
            "Professions" to
                listOf(
                    "Doctor", "Teacher", "Engineer", "Artist", "Chef", "Pilot",
                    "Police Officer", "Firefighter", "Lawyer", "Scientist", "Writer",
                    "Actor", "Singer", "Dancer", "Farmer", "Nurse", "Photographer",
                    "Architect", "Mechanic", "Plumber", "Electrician", "Dentist",
                    "Veterinarian", "Barber", "Librarian", "Astronaut", "Designer", "DJ",
                    "Magician", "Athlete", "Journalist", "Programmer", "Data Scientist",
                    "Streamer", "Influencer", "Therapist", "Paramedic", "Translator",
                ),
            "Celebrities & Icons" to
                listOf(
                    "Taylor Swift", "Beyonce", "Cristiano Ronaldo", "Lionel Messi",
                    "LeBron James", "Ariana Grande", "Dwayne Johnson", "Tom Cruise",
                    "Leonardo DiCaprio", "Brad Pitt", "Jennifer Lawrence", "Chris Hemsworth",
                    "Rihanna", "Drake", "Eminem", "Justin Bieber", "Selena Gomez",
                    "Kim Kardashian", "Will Smith", "Johnny Depp", "Scarlett Johansson",
                    "Robert Downey Jr", "Emma Watson", "Zendaya", "Billie Eilish",
                    "Ed Sheeran", "Adele", "Lady Gaga", "Kanye West", "Shakira",
                    "Timothee Chalamet", "Sabrina Carpenter", "Pedro Pascal",
                    "Margot Robbie", "Bad Bunny", "BTS", "Olivia Rodrigo",
                ),
            "Historical Figures" to
                listOf(
                    "Albert Einstein", "Napoleon", "Cleopatra", "Shakespeare",
                    "Leonardo da Vinci", "Abraham Lincoln", "Martin Luther King Jr",
                    "Gandhi", "Julius Caesar", "Mozart", "Beethoven", "Picasso",
                    "Marie Curie", "Isaac Newton", "Nikola Tesla", "George Washington",
                    "Queen Elizabeth I", "Winston Churchill", "Nelson Mandela",
                    "Aristotle", "Socrates", "Galileo", "Darwin", "Joan of Arc",
                    "Alexander the Great", "Genghis Khan", "Confucius", "Plato",
                    "Frida Kahlo", "Ada Lovelace", "Rosa Parks", "Anne Frank",
                ),
            "World Cities" to
                listOf(
                    "Paris", "London", "New York", "Tokyo", "Rome", "Barcelona",
                    "Dubai", "Los Angeles", "Sydney", "Amsterdam", "Berlin", "Moscow",
                    "Istanbul", "Hong Kong", "Singapore", "Miami", "Las Vegas",
                    "Rio de Janeiro", "Cairo", "Athens", "Vienna", "Prague",
                    "Mumbai", "Bangkok", "Toronto", "Venice", "Florence", "Lisbon",
                    "Copenhagen", "Stockholm", "Jerusalem", "Seoul", "Reykjavik",
                    "Marrakech", "Buenos Aires", "Cape Town", "Kyoto", "Edinburgh",
                ),
            "Famous Characters" to
                listOf(
                    "Sherlock Holmes", "Superman", "Batman", "Spider-Man", "Harry Potter",
                    "Elsa", "Mickey Mouse", "Pikachu", "Mario", "Sonic", "Iron Man",
                    "Wonder Woman", "Captain America", "Hulk", "Thor", "Darth Vader",
                    "Luke Skywalker", "Yoda", "Shrek", "Buzz Lightyear", "Simba",
                    "Aladdin", "Goku", "Naruto", "SpongeBob", "Homer Simpson",
                    "Bugs Bunny", "Tom and Jerry", "Scooby-Doo", "Dora", "Kratos",
                    "Master Chief", "Link", "Lara Croft", "Geralt", "Ellie",
                ),
            "Famous Brands" to
                listOf(
                    "Apple", "Nike", "McDonald's", "Coca-Cola", "Google", "Amazon",
                    "Samsung", "Adidas", "Ferrari", "BMW", "Mercedes", "Starbucks",
                    "Tesla", "PlayStation", "Xbox", "Netflix", "Disney", "Pepsi",
                    "KFC", "Subway", "Honda", "Toyota", "Microsoft", "Meta",
                    "Instagram", "YouTube", "Spotify", "Twitter", "TikTok", "Uber",
                    "Airbnb", "OpenAI", "NVIDIA", "Lego", "IKEA", "Sony",
                ),
            "Musical Instruments" to
                listOf(
                    "Guitar", "Piano", "Drums", "Violin", "Flute", "Saxophone",
                    "Trumpet", "Cello", "Harp", "Ukulele", "Accordion", "Harmonica",
                    "Banjo", "Clarinet", "Trombone", "Xylophone", "Bass Guitar",
                    "Keyboard", "Tambourine", "Bagpipes", "Bongo", "Maracas",
                    "Electric Guitar", "Synthesizer", "Sitar", "Mandolin", "Oboe",
                ),
            "Technology & Gadgets" to
                listOf(
                    "iPhone", "Computer", "Internet", "Robot", "Drone", "Laptop",
                    "Tablet", "Smartphone", "Camera", "Television", "Headphones",
                    "Bluetooth", "WiFi", "Virtual Reality", "3D Printer", "Smartwatch",
                    "Gaming Console", "GPS", "Smart Speaker", "Wireless Earbuds",
                    "Fitness Tracker", "E-reader", "Projector", "VR Headset",
                    "Foldable Phone", "AI Assistant", "Smart Glasses", "Electric Car",
                    "Power Bank", "Webcam", "SSD", "Router", "MicroSD", "Thermostat"
                ),
            "Nature & Landscapes" to
                listOf(
                    "Mountain", "Ocean", "Forest", "Desert", "River", "Waterfall",
                    "Volcano", "Rainbow", "Lightning", "Tornado", "Earthquake",
                    "Avalanche", "Glacier", "Canyon", "Valley", "Island", "Beach",
                    "Cave", "Jungle", "Lake", "Aurora", "Sunset", "Sunrise",
                    "Cliff", "Meadow", "Savanna", "Swamp", "Coral Reef", "Geyser",
                    "Fjord", "Dune", "Tundra",
                ),
            "Mythology & Fantasy" to
                listOf(
                    "Dragon", "Wizard", "Unicorn", "Mermaid", "Phoenix", "Griffin",
                    "Centaur", "Elf", "Dwarf", "Fairy", "Goblin", "Troll",
                    "Minotaur", "Pegasus", "Medusa", "Sphinx", "Hydra", "Cyclops",
                    "Kraken", "Werewolf", "Angel", "Demon", "Banshee", "Valkyrie",
                    "Chimera", "Basilisk", "Nymph",
                ),
            "Space & Astronomy" to
                listOf(
                    "Sun", "Moon", "Mars", "Jupiter", "Saturn", "Earth", "Venus",
                    "Neptune", "Asteroid", "Comet", "Meteor", "Galaxy", "Black Hole",
                    "Supernova", "Solar System", "Milky Way", "Constellation", "Nebula",
                    "Satellite", "Space Station", "Pulsar", "Quasar", "Dark Matter",
                    "Event Horizon", "Mercury", "Uranus", "Pluto",
                ),
            "Vehicles & Transportation" to
                listOf(
                    "Car", "Bicycle", "Motorcycle", "Bus", "Train", "Airplane",
                    "Helicopter", "Boat", "Ship", "Submarine", "Rocket", "Hot Air Balloon",
                    "Scooter", "Skateboard", "Jet", "Yacht", "Cruise Ship", "Ambulance",
                    "Fire Truck", "Tank", "Tram", "Monorail", "Hovercraft", "Tuk Tuk",
                ),
            "School & Education" to
                listOf(
                    "Blackboard", "Chalk", "Backpack", "Calculator", "Diploma",
                    "Library", "Homework", "Exam", "Recess", "Cafeteria", "Principal",
                    "Teacher", "Student", "Bus", "Notebook", "Pencil", "Eraser",
                    "Globe", "Microscope", "Chemistry", "History", "Locker",
                    "Whiteboard", "Graduation", "Field Trip",
                ),
            "At the Beach" to
                listOf(
                    "Sandcastle", "Sunscreen", "Surfboard", "Towel", "Umbrella",
                    "Seagull", "Crab", "Starfish", "Volleyball", "Lifeguard",
                    "Sunglasses", "Ice Cream", "Flip Flops", "Ocean", "Shell",
                    "Waves", "Bikini", "Tanning", "Palm Tree", "Cooler",
                    "Snorkel", "Jet Ski", "Hammock", "Pier",
                ),
            "Halloween & Horror" to
                listOf(
                    "Ghost", "Vampire", "Witch", "Zombie", "Skeleton", "Pumpkin",
                    "Bat", "Spider", "Haunted House", "Mummy", "Werewolf", "Cauldron",
                    "Broomstick", "Candy Corn", "Costume", "Mask", "Fog", "Graveyard",
                    "Coffin", "Scream", "Jack-o'-Lantern", "Tombstone", "Cobweb", "Ouija",
                ),
            "Christmas & Holidays" to
                listOf(
                    "Santa Claus", "Reindeer", "Snowman", "Christmas Tree", "Present",
                    "Ornament", "Elf", "Stocking", "Gingerbread", "Candy Cane",
                    "Sleigh", "Mistletoe", "Wreath", "Turkey", "Fireworks", "Champagne",
                    "Easter Egg", "Bunny", "Cupid", "Leprechaun", "Menorah", "Diwali Lamp",
                ),
            "Superheroes & Villains" to
                listOf(
                    "Superman", "Batman", "Spider-Man", "Wonder Woman", "Iron Man",
                    "Captain America", "Thor", "Hulk", "Black Widow", "Joker",
                    "Thanos", "Loki", "Venom", "Deadpool", "Wolverine", "Flash",
                    "Aquaman", "Green Lantern", "Lex Luthor", "Magneto", "Doctor Strange",
                    "Black Panther", "Scarlet Witch", "Harley Quinn",
                ),
            "Retro Nostalgia" to
                listOf(
                    "Game Boy", "VHS Tape", "Walkman", "Tamagotchi", "Beanie Babies",
                    "Pokemon", "Fanny Pack", "Slap Bracelet", "Dial-up Internet",
                    "Floppy Disk", "Blockbuster", "Spice Girls", "Nirvana", "Friends",
                    "Seinfeld", "Titanic", "Lion King", "Matrix", "Pager", "Polaroid",
                    "Discman", "MySpace", "Cassette",
                ),
            "Science & Lab" to
                listOf(
                    "Microscope", "Telescope", "Beaker", "Test Tube", "Bunsen Burner",
                    "Atom", "Molecule", "DNA", "Bacteria", "Virus", "Robot", "Laser",
                    "Magnet", "Gravity", "Evolution", "Big Bang", "Black Hole", "Orbit",
                    "Experiment", "Formula", "Hypothesis", "Petri Dish", "Centrifuge",
                ),
            "Trending & Viral" to
                listOf(
                    "TikTok", "AI", "ChatGPT", "Metaverse", "NFT", "Crypto",
                    "Vibe Check", "Rizz", "Ghosting", "Stan", "Meme", "Viral",
                    "Influencer", "Podcast", "Streaming", "Binge", "Hashtag", "Selfie",
                    "Emoji", "Cringe", "Gig Economy", "Cancel Culture", "Doomscroll",
                    "Side Quest", "Soft Launch", "Beige Flag", "Delulu", "NPC",
                ),
            "Video Games" to
                listOf(
                    "Minecraft", "Fortnite", "Roblox", "Zelda", "Mario Kart",
                    "Call of Duty", "Pokemon", "Among Us", "League of Legends",
                    "Valorant", "Overwatch", "Elden Ring", "GTA", "FIFA", "Tetris",
                    "Sims", "Animal Crossing", "Stardew Valley", "Hollow Knight",
                    "Baldur's Gate", "Cyberpunk", "Halo", "Counter-Strike", "Dota",
                    "Genshin Impact", "Fall Guys", "Apex Legends", "Pac-Man", "Doom",
                    "Resident Evil", "Skyrim", "Final Fantasy", "Dark Souls"
                ),
            "Superpowers" to
                listOf(
                    "Flight", "Invisibility", "Super Strength", "Telepathy", "Teleportation",
                    "Pyrokinesis", "Time Travel", "Shape-shifting", "Elasticity", "Healing Factor",
                    "Laser Vision", "Invulnerability", "X-ray Vision", "Mind Control", "Force Fields"
                ),
            "Board Games" to
                listOf(
                    "Monopoly", "Chess", "Scrabble", "Catan", "Risk", "Clue",
                    "Pandemic", "Ticket to Ride", "Checkers", "Backgammon",
                    "Battleship", "Jenga", "Operation", "Pictionary", "Trivial Pursuit"
                ),
            "Literature" to
                listOf(
                    "Hamlet", "Moby Dick", "Great Gatsby", "Odyssey", "War and Peace",
                    "Don Quixote", "Pride and Prejudice", "Frankenstein", "Dracula", "Macbeth",
                    "Iliad", "Jane Eyre", "Wuthering Heights", "Catch-22", "Beloved"
                )
        )

    /**
     * Returns a random word for the given [category].
     *
     * If [category] is [RANDOM_CATEGORY], a real sub-category is chosen first and
     * the word is drawn from it. This keeps the gameplay coherent when paired
     * with [getRandomWordPair] in stealth mode.
     */
    fun getRandomWord(category: String): String {
        val words = wordsForCategory(category)
        return words.randomOrNull() ?: "Imposter"
    }

    /**
     * Returns two distinct words from the *same* category.
     *
     * Used in stealth mode: `first` is the crewmate word and `second` is the
     * imposter's decoy. Both come from the same theme so the imposter's reveal
     * blends in with crewmate descriptions instead of being trivially obvious.
     *
     * If [category] is [RANDOM_CATEGORY], a real sub-category is chosen first
     * and both words are pulled from that single category.
     */
    fun getRandomWordPair(category: String): Pair<String, String> {
        val words = wordsForCategory(category)
        if (words.size < 2) {
            return Pair(words.firstOrNull() ?: "Imposter", "Decoy")
        }
        val shuffled = words.shuffled()
        return Pair(shuffled[0], shuffled[1])
    }

    /**
     * Resolves the effective word list for [category].
     *
     * For [RANDOM_CATEGORY] this picks one real sub-category at random and
     * returns its words, ensuring callers that need related words (stealth
     * pairs, single picks) get a thematically consistent set.
     */
    private fun wordsForCategory(category: String): List<String> {
        if (category == RANDOM_CATEGORY) {
            val realCategories = categories.filterKeys { it != RANDOM_CATEGORY }
            val pick = realCategories.values.randomOrNull() ?: return emptyList()
            return pick
        }
        return categories[category] ?: emptyList()
    }
}
