# Evently - Event Booking App

**Intern ID:** CITS2358  
**Name:** Ashu Meena  
**Duration:** 6 Weeks  
**Project:** Event Booking Mobile App

---

## What is this?

Evently is an Android app I built during my internship at CODTECH. The idea is simple — let users browse events happening around them, pick tickets, and get a digital ticket on their phone. No complicated setup, just a clean booking experience.

I wanted it to feel like a real consumer app, not just a demo project, so I put a lot of effort into animations, proper navigation flow, and making the UI actually look good on a real phone.

---

## What the app does

When you first open the app you get a splash screen (I added floating particles and a spring animation on the logo just to make it feel premium). First-time users go through a short onboarding, then reach the login screen.

**Login** — You enter a 10-digit phone number and get an OTP. There's also a Google sign-in button (simulated for now, but the UI is fully wired up).

**Home screen** — This is the main dashboard. There's a featured event carousel at the top that auto-scrolls, then category filter pills (each category has its own color gradient and emoji), then a "Happening Today" horizontal row, and below that the trending events and a recommended list. I added a small "🔥 42 viewing" badge on cards because it makes events feel more alive.

**Search** — Tap the search bar and you get a full search page with a filter dialog. You can filter by category, max price, and city. Results show in a 2-column grid.

**Event Details** — Full event page with a big hero image, description, highlights, photo gallery, and a sticky "Book Now" bar at the bottom. I also added a seat availability bar that animates and changes color (green → orange → red) based on how full the event is. There's also a "🔥 N viewing now" count pulled from a seeded random based on the event ID so it stays stable.

**Booking flow** — Ticket selection screen → checkout with a payment form → success screen. The success screen is probably my favorite part: there's a confetti animation using Canvas, a 3-ring pulsing check animation, and then the digital ticket which has a gradient header matching the event category, a shimmer sweep animation, and a dashed perforated divider line between the ticket info and the QR code section (like a real physical ticket). There's also an "Add to Calendar" button that actually opens Google Calendar with all the details pre-filled.

**My Tickets** — Shows all your bookings split into Upcoming and Completed tabs. Each booking shows as a premium ticket card with the same gradient + shimmer design.

**Wishlist** — Saved events. Persists between sessions using Room.

**Profile** — Edit your name/email/phone. Links to Settings (dark/light mode toggle), Payment Methods, and Help & Support screens.

---

## Tech stack

- Kotlin
- Jetpack Compose for all UI
- MVVM architecture with ViewModel + StateFlow
- Room database (for bookings, wishlist, user session)
- Navigation Compose for screen routing
- Coil for image loading
- Lottie + Compose animation APIs for animations

Libraries are in `gradle/libs.versions.toml` if you want exact version numbers.

---

## Folder structure

```
evently/
├── data/
│   ├── local/          → Room database, DAOs, entities
│   ├── repository/     → EventlyRepository interface + implementation
│   └── DummyData.kt    → Sample events used for the demo
├── model/              → Data classes (Event, Booking, User, etc.)
├── navigation/
│   └── Screen.kt       → All route definitions
├── theme/
│   ├── Color.kt        → Colors + per-category gradient map + emoji map
│   ├── Theme.kt        → Light/dark MaterialTheme setup
│   └── Type.kt         → Font styles
├── ui/
│   └── screens/        → One file per screen (16 total)
├── viewmodel/
│   └── EventlyViewModel.kt  → Single ViewModel holding all app state
└── MainActivity.kt     → Entry point, bottom nav, NavHost
```

---

## How to run it

You just need Android Studio (Koala or newer) and JDK 17.

1. Open the project folder in Android Studio
2. Wait for Gradle to sync (it downloads everything automatically)
3. Plug in your phone or start an emulator
4. Hit the Run button, or from terminal:

```bash
.\gradlew installDebug
```

That's it. The app uses local Room data so there's no backend or API keys to configure.

---

## Things worth testing

- **Full booking flow** — Pick any event, tap Book Now, choose tickets, fill checkout, and see the confetti + animated ticket on success
- **Add to Calendar** — On the booking success page, tap the calendar button and check your Google Calendar opens with the right details
- **Seat pressure bar** — Open a few different events and notice the availability bar changes color (some will show green, some orange, some red)
- **Category filters** — On the home screen, tap different category pills and see the cards update
- **Wishlist persistence** — Heart an event, close the app, reopen it — the heart should still be filled
- **Dark mode** — Profile → Settings → toggle dark mode

---

## Deliverables

- Full source code (all 16 screens implemented and building cleanly)
- Animated digital ticket feature (shimmer, confetti, perforated design)
- Add-to-Calendar and Share integrations
- Room database for local persistence
- This README

---

*Built during CODTECH internship, 2026 — Ashu Meena*
