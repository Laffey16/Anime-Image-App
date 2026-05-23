# AnimeApp

An Android app for browsing **SFW** images from booru-style imageboards, written in Kotlin as a learning exercise towards Android development.

> **Status:** Early work-in-progress. Expect rough edges and limited features.

## What's a booru?

"Booru" is the common name for imageboards that organise anime/illustration art with a tag-based search system (Danbooru, Safebooru, Gelbooru, Konachan, etc.). This app provides a clean mobile front-end for browsing them.

## Features

- [x] Browse posts from one or more booru sites
- [x] Save images to device
- [x] Full-screen image viewer with pinch-to-zoom
- [ ] Tag-based search
- [ ] Favourites / history
- [ ] Multi-site support




## Tech Stack

- **Language:** Kotlin
- **Build:** Gradle (wrapper included)
- **Min SDK / Target SDK:** 
- **Networking:** Retrofit
- **Image loading:** Glide

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17+
- An Android device or emulator running API *(min SDK)* or higher



## Project Structure
```
Anime-Image-App/
├── app/                # Android app module (source, resources, manifest)
├── gradle/wrapper/     # Gradle wrapper
├── build.gradle        # Root Gradle build script
├── settings.gradle
└── gradle.properties
```
