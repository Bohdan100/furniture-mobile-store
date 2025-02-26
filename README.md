# Furniture Store Application (mobile)
The **Furniture Store** is an Android application developed in Kotlin, utilizing an SQLite database, and designed to provide users with a seamless interface for purchasing furniture items. The application features user authentication, a product catalog, a personalized shopping cart, and integrated payment processing via Stripe. All user and product data are securely stored in an SQLite database.

## Features
- **User Authentication**: Secure registration and login with data validation.
- **Product Catalog**: Browse through a list of furniture items with detailed descriptions and images.
- **Shopping Cart**: Add items to a personalized cart and manage them before checkout.
- **Payment Integration**: Secure payment processing using **Stripe** for seamless transactions.
- **SQLite Database**: Efficient local storage for user data, products, and cart items.
- **User-Friendly UI**: Intuitive navigation between activities for a smooth user experience.

## Requirements
The application is built using the following technologies:
- **Android SDK**: 34
- **Kotlin**: 1.9.25
- **SQLite**: Local database for data storage.
- **Stripe**: Payment processing integration.
- **Retrofit**: For network requests (if extended for backend communication).
- **Gradle**: For building the application.

## Code Overview
The application is structured into several key components:
- **MainActivity**: Handles user registration (username, email, and password).
- **LoginActivity**: Handles user authentication (email and password).
- **ItemsActivity**: Displays the list of available furniture items.
- **ItemActivity**: Shows detailed information about a selected item and allows users to add it to the cart or proceed to payment.
- **CartActivity**: Manages the user's shopping cart, allowing them to view and remove items.
- **DbHelper**: Manages the SQLite database, including user data, products, and cart items.

## Getting Started
Follow these steps to set up and run the **Furniture Mobile Store** application on your local machine.

### Prerequisites
* **Android Studio**: Ensure you have Android Studio installed (preferably the latest version).
* **Java Development Kit (JDK)**: Version 21.
* **Git**: To clone the repository.

### Steps to Run the Application
1. **Clone the repository:**
    ```bash
    git clone https://github.com/Bohdan100/furniture-mobile-store
    ```

2. **Open the project in Android Studio:**
   * Launch Android Studio.
   * Select **Open an Existing Project** and navigate to the cloned repository folder.
   * Wait for the project to sync and download dependencies.

3. **Build the application:**
   * You can build the project directly in Android Studio by clicking **Build > Make Project**.
   * Alternatively, use the terminal to build the project with Gradle:
       ```bash
       ./gradlew assembleDebug         # For Linux
       .\gradlew assembleDebug     # For Windows
       ```

4. **Run the application on an emulator or physical device:**
   * Connect an Android device or start an emulator from Android Studio.
   * In Android Studio, click **Run > Run 'app'**.
   * Alternatively, use the terminal to install and run the APK on a connected device:
       ```bash
       ./gradlew installDebug       # For Linux
       .\gradlew installDebug   # For Windows
       ```

5. **Use the application:**
   * After the app is installed, open it on your device or emulator.
   * Register a new account or log in with existing credentials.
   * Browse the product catalog, add items to your cart, and proceed to checkout.
