package com.example.evently.data

import com.example.evently.model.Event

object DummyData {
    val categories = listOf(
        "Concerts",
        "Comedy",
        "Sports",
        "Workshops",
        "Festivals",
        "Nightlife",
        "Theatre",
        "Food Events"
    )

    val events = listOf(
        Event(
            id = "e1",
            name = "Arijit Singh Live Concert",
            description = "Experience the soulful voice of India's favorite singer, Arijit Singh, live in concert. Get ready for an evening filled with romance, melodies, and emotional hits that will touch your heart. This is a once-in-a-lifetime musical extravaganza featuring an orchestra and state-of-the-art stage visual design.",
            imageUrl = "https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=800&q=80",
            date = "Oct 24, 2026",
            time = "06:30 PM",
            venue = "DY Patil Stadium, Mumbai",
            price = 999,
            rating = 4.9,
            category = "Concerts",
            organizer = "Live Nation India",
            highlights = listOf("Free Parking", "Food Available", "Family Friendly", "Wheelchair Accessible"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e2",
            name = "Stand-Up Comedy Night",
            description = "Prepare for side-splitting laughter as the country's top comedians hit the stage. From observational humor to witty quick-fires, this show is guaranteed to tickle your funny bone. Come with friends, grab a drink, and enjoy a night of absolute laughter therapy.",
            imageUrl = "https://images.unsplash.com/photo-1585699324551-f6c309eed262?auto=format&fit=crop&w=800&q=80",
            date = "Jun 28, 2026",
            time = "08:00 PM",
            venue = "The Habitat, Mumbai",
            price = 499,
            rating = 4.7,
            category = "Comedy",
            organizer = "Comedy Cellar India",
            highlights = listOf("Food Available", "Air Conditioned", "Couples Entry Allowed"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1527224857830-43a7acc85260?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e3",
            name = "IPL Fan Fest 2026",
            description = "Catch the grand finale of the IPL on the giant screens with stadium-like atmosphere, food stalls, DJ music, and merchandise stores. Bring your family and cheer for your favorite team in this high-octane cricketing carnival.",
            imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
            date = "Jun 29, 2026",
            time = "07:00 PM",
            venue = "Jio Gardens, Bandra, Mumbai",
            price = 299,
            rating = 4.8,
            category = "Sports",
            organizer = "BCCI Fan Engagement",
            highlights = listOf("Free Parking", "Food & Alcohol", "Family Friendly", "Fan Merchandise"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1540747737956-37872f7671f3?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e4",
            name = "Startup Networking Meetup",
            description = "Connect with over 500+ startup founders, venture capitalists, angel investors, and tech enthusiasts. Pitch your ideas, discuss strategies, find potential co-founders, and build long-lasting professional relationships over coffee.",
            imageUrl = "https://images.unsplash.com/photo-1515187029135-18ee286d815b?auto=format&fit=crop&w=800&q=80",
            date = "Jul 15, 2026",
            time = "04:00 PM",
            venue = "WeWork Galaxy, Bengaluru",
            price = 599,
            rating = 4.5,
            category = "Workshops",
            organizer = "Startup India Network",
            highlights = listOf("Free Beverages", "WiFi Access", "Air Conditioned", "Speaker Session"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e5",
            name = "EDM Music Festival",
            description = "Get ready to dance to the beats of world-renowned DJs at the biggest electronic dance music festival of the year. Featuring stunning laser shows, neon theme zones, multiple food courts, and an electric atmosphere that keeps you buzzing.",
            imageUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?auto=format&fit=crop&w=800&q=80",
            date = "Nov 12, 2026",
            time = "04:00 PM",
            venue = "Vagator Beach, Goa",
            price = 1499,
            rating = 4.9,
            category = "Festivals",
            organizer = "Sunburn Festival",
            highlights = listOf("Food & Alcohol", "Beachside Venue", "VIP Lounges", "Security Escort"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e6",
            name = "Food Carnival 2026",
            description = "Satiate your taste buds with culinary delights from over 100+ stalls featuring street food, gourmet items, international cuisines, and innovative desserts. Also features live cooking demonstrations by celebrity chefs and a live music arena.",
            imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&w=800&q=80",
            date = "Aug 05, 2026",
            time = "12:00 PM",
            venue = "MMRDA Grounds, Mumbai",
            price = 199,
            rating = 4.6,
            category = "Food Events",
            organizer = "Foodies Global",
            highlights = listOf("Family Friendly", "Wheelchair Accessible", "Food Contests", "Live Cooking"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e7",
            name = "Tech Conference 2026",
            description = "Explore the future of Artificial Intelligence, Blockchain, and Quantum Computing. Learn from industry leaders at Google, Meta, and Microsoft as they share insights on tomorrow's tech landscape. Perfect for programmers and tech managers.",
            imageUrl = "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?auto=format&fit=crop&w=800&q=80",
            date = "Dec 10, 2026",
            time = "09:00 AM",
            venue = "NESCO Centre, Mumbai",
            price = 1999,
            rating = 4.7,
            category = "Workshops",
            organizer = "Tech Forum Group",
            highlights = listOf("Free Beverages", "WiFi Access", "Air Conditioned", "Certificate Included"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1591115765373-5a923243176d?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1582192732961-2294e3078a87?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e8",
            name = "Photography Masterclass",
            description = "Enhance your photography skills in this hands-on workshop led by National Geographic photographer John Doe. Learn about composition, manual settings, lighting, and post-processing techniques using professional editing software.",
            imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80",
            date = "Jul 20, 2026",
            time = "10:00 AM",
            venue = "Cubbon Park, Bengaluru",
            price = 899,
            rating = 4.4,
            category = "Workshops",
            organizer = "Shutter Academy",
            highlights = listOf("Outdoor Shoot", "Hands-on Training", "Certificate Included", "Equipment Provided"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1452780212940-6f5c0d14d848?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1542038784456-1ea8e935640e?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e9",
            name = "Shakespeare's Hamlet",
            description = "A dramatic adaptation of William Shakespeare's timeless tragedy 'Hamlet'. Experience powerful performances, artistic stage direction, and exquisite costume design that brings 16th-century Denmark to life.",
            imageUrl = "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=800&q=80",
            date = "Oct 18, 2026",
            time = "07:30 PM",
            venue = "NCPA Tata Theatre, Mumbai",
            price = 699,
            rating = 4.8,
            category = "Theatre",
            organizer = "Royal Theatre Co.",
            highlights = listOf("Air Conditioned", "Family Friendly", "Wheelchair Accessible", "Dramatics"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1503095391755-141d05677fd4?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e10",
            name = "Evently Midnight Marathon",
            description = "Join thousands of runners in the annual city marathon under the neon night lights. Support a charity while testing your physical endurance. Includes marathon kits, hydration stations, medical checkpoints, and a finisher medal.",
            imageUrl = "https://images.unsplash.com/photo-1502224562085-639556652f33?auto=format&fit=crop&w=800&q=80",
            date = "Sep 15, 2026",
            time = "11:00 PM",
            venue = "Marine Drive, Mumbai",
            price = 350,
            rating = 4.6,
            category = "Sports",
            organizer = "Fit India Org",
            highlights = listOf("Free Marathon Kit", "Hydration Stations", "Medical Support", "Charity Runner"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1486218119243-13883505764c?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e11",
            name = "Sunburn Club Night",
            description = "Bring on the energy and high-bass EDM at the hottest nightclub in the city. Featuring international guest DJ setups, signature cocktails, laser-lit dancefloors, and premium club experiences that go on until dawn.",
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80",
            date = "Jul 11, 2026",
            time = "09:30 PM",
            venue = "Kitty Su, Mumbai",
            price = 1200,
            rating = 4.5,
            category = "Nightlife",
            organizer = "Kitty Su Clubbing",
            highlights = listOf("Food & Alcohol", "Air Conditioned", "Couples Entry", "18+ Only"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1574391884720-bbc3740c59d1?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1545128485-c400e7702796?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e12",
            name = "Broadway Musical: Aladdin",
            description = "An award-winning theatrical spectacle containing magic, comedy, stunning costumes, and the classic songs you know and love. A mystical experience that will enchant kids and adults alike.",
            imageUrl = "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=800&q=80",
            date = "Nov 05, 2026",
            time = "06:00 PM",
            venue = "St. Andrews Auditorium, Mumbai",
            price = 799,
            rating = 4.9,
            category = "Theatre",
            organizer = "Disney Theatre Group",
            highlights = listOf("Air Conditioned", "Family Friendly", "Wheelchair Accessible", "Stunning Costumes"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1516307364728-22f12d5e7e6e?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e13",
            name = "Jazz & Wine Tasting Festival",
            description = "Indulge in a premium wine-tasting experience featuring fine vintage wines paired with gourmet cheeses, all while listening to classical, smooth jazz performed live by the city's finest ensembles.",
            imageUrl = "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?auto=format&fit=crop&w=800&q=80",
            date = "Sep 22, 2026",
            time = "05:00 PM",
            venue = "Sula Vineyards, Nashik",
            price = 1499,
            rating = 4.8,
            category = "Food Events",
            organizer = "Sula Wines & Arts",
            highlights = listOf("Free Parking", "Gourmet Cheese", "18+ Only", "Scenic Location"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1528826722302-d6084efee6e6?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e14",
            name = "Rock Arena Live",
            description = "Get ready to headbang with the legends of classic and heavy rock music. Featuring high-gain amplifiers, explosive drum solos, and roaring vocals that will send shivers down your spine.",
            imageUrl = "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?auto=format&fit=crop&w=800&q=80",
            date = "Oct 31, 2026",
            time = "07:00 PM",
            venue = "Palace Grounds, Bengaluru",
            price = 899,
            rating = 4.7,
            category = "Concerts",
            organizer = "RockNation India",
            highlights = listOf("Free Parking", "Food & Alcohol", "Standing Arena"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1524368535928-5b5e00ddc76b?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=400&q=80"
            )
        ),
        Event(
            id = "e15",
            name = "Laughter Riot featuring Zakir",
            description = "Zakir Khan returns to the stage with his brand new solo standup show. Filled with nostalgia, heart-warming stories, and hilarious punchlines about his growing up years, this is a show you don't want to miss.",
            imageUrl = "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?auto=format&fit=crop&w=800&q=80",
            date = "Jul 05, 2026",
            time = "08:30 PM",
            venue = "Shanmukhananda Hall, Mumbai",
            price = 600,
            rating = 4.9,
            category = "Comedy",
            organizer = "OML Comedy",
            highlights = listOf("Air Conditioned", "Family Friendly", "Wheelchair Accessible"),
            gallery = listOf(
                "https://images.unsplash.com/photo-1585699324551-f6c309eed262?auto=format&fit=crop&w=400&q=80",
                "https://images.unsplash.com/photo-1527224857830-43a7acc85260?auto=format&fit=crop&w=400&q=80"
            )
        )
    )
}
