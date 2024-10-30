# RoadBuddy 🚗

RoadBuddy is an Android application designed to facilitate safer and easier journeys by connecting users in need with helpers willing to assist. The app provides two distinct user interfaces: one for users needing help and one for helpers offering assistance. 

## Features

- **User App ( Roadbuddy )**:
  - Easily create requests for assistance, which are saved in the Firestore database.
  - Chat with a chatbot to discuss specific needs or provide feedback.
  - Track helper locations and view estimated distances.

- **Helper App ( HelperBuddy )**:
  - View requests from users within proximity.
  - Access real-time locations to locate users quickly.
  - Track and communicate with users in need.
  - Accept or reject requests, with updates automatically stored in Firestore.

## Tech Stack

- **Languages**: Kotlin, XML
- **Frameworks/Libraries**:
  - Firebase Authentication and Firestore for user data and request storage.
  - Google Maps API for location services.
  - Android Location Services for live tracking and distance calculation.
  
## Project Structure

- RoadBuddy
  - User App
    - Authentication
    - Chatbot Interface
    - Request Creation & Tracking
  - Helper App
    - Request List
    - Real-time User Location Tracking
    - Request Acceptance/Management

## Getting Started

1. **Clone the Repository**: 
   ```bash
   git clone https://github.com/yourusername/RoadBuddy.git

2. **Firebase Setup**:
 - Set up Firebase Authentication and Firestore in the Firebase Console.
 - Add your google-services.json file in both app folders (User and Helper).

3. **Google Maps API**:

 - Enable the Google Maps SDK for Android and generate an API key.
  - Add the API key to AndroidManifest.xml.

## Usage
 1. Register as a User or Helper.
 2. User App: Create a request specifying your needs, and track nearby helpers.
 3. Helper App: View user requests, get directions, and provide real-time assistance.

# THANK YOU !!
