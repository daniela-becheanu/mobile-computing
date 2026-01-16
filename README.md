# Digital Billboard Booking (Android)

An Android app that lets users browse digital billboards in **Bucharest**, view available time slots with prices, and **simulate** uploading an image to be displayed during a chosen interval.

This project is built with **Kotlin + Jetpack Compose** and communicates with a **mock REST API**.  
You can run it using either:

## Features

- User authentication (mock login)
- List of billboards in Bucharest
- Billboard details:
    - address + info
    - available time slots + pricing
    - existing scheduled images/campaigns
- Simulated image upload to a billboard

---

## Tech Stack

- **Kotlin**
- **Jetpack Compose** (UI)
- **Navigation Compose**
- **Retrofit + OkHttp**
- **Coil** (image loading)
- **DataStore** (simple token storage)
- **MockInterceptor** (offline API mocking)

---

## REST API Endpoints (Mock)

These are the endpoints used by the app:

- `GET /billboards`  
  Retrieve the list of billboards.

- `GET /billboards/{id}`  
  Retrieve details for a specific billboard.

- `GET /billboards/{id}/images`  
  Retrieve images/campaigns associated with a billboard.

- `POST /billboards/{id}/images`  
  Simulate uploading an image to a billboard.

---

## Project Setup

### Requirements
- Android Studio (latest stable recommended)
- Android SDK (API 34+ recommended)
- Emulator or physical device
- Internet permission enabled in manifest

Make sure your `AndroidManifest.xml` contains:

```xml
<uses-permission android:name="android.permission.INTERNET" />
