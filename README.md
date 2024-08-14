
## ***VetAppointment*** ##

Overview
**VetAppointment** is an Android application designed to streamline the process of booking and managing veterinary appointments. This application provides a user-friendly platform for both pet owners and veterinarians, leveraging Firebase for secure data handling, authentication, and real-time updates.

## Features

- **Firebase Integration:** All data, including user profiles, appointments, and messages, are securely stored and managed in real-time using Firebase Realtime Database and Firebase Firestore.
- **Dual Account Types:** The app supports two distinct user roles—Pet Owners and Veterinarians—each with tailored functionalities.
- **Material Design:** The app follows Google's Material Design principles, offering a clean, intuitive, and responsive user interface.


### For Pet Owners
- **Multiple Login Options:** Users can sign in using email and password, SMS verification, or Google account.
- **Home Page:** The home page displays upcoming appointments, And a number of messages that are still waiting to be answered by a veterinarian,ensuring pet owners have all the necessary information at a glance.
- **Appointment Booking:** Pet owners can view available appointment slots, select a time that suits them, and book appointments directly through the app.
- **Appointment Management:** Users can view, reschedule, or cancel their appointments with ease.
- **Add new message:** user can add message with the added functionality of uploading a photo to give context to the message.
- **No Double Booking:** The system ensures that pet owners cannot book more than one appointment in the same time slot, avoiding scheduling conflicts.
- **Review and Rating:** Pet owners can leave reviews and rate the services provided by veterinarians, helping other users make informed decisions.
- **Navigation and Communication:** The app includes features for navigating to the clinic using Google Maps or directly calling the veterinarian's office.

### For Veterinarians
- **Appointment Management:** Veterinarians can view all past and future appointments and filter appointments by date.
- **Availability Management:** Veterinarians can block out days or specific times when they are unavailable, provided no appointments are already scheduled for those times.
- **Respond to Messages:** Veterinarians can directly communicate with pet owners through the app, responding to inquiries.
- **Appointment Status Tracking:** Veterinarians can update the status of appointments (e.g., completed, pending, or canceled) in real time, providing transparency and clarity to pet owners.
- **Review Management:** Veterinarians can view the reviews and ratings given by pet owners and respond to feedback.

### Additional Features
- **Data Encryption:** Sensitive user and pet data is encrypted to ensure privacy and security.
- **User Analytics:** Built-in analytics provide insights into user behavior, helping to improve the app’s functionality over time.

## Technology Stack

### Frontend:
- **Android Studio:** The app is developed using Android Studio, ensuring a robust and scalable codebase.
- **XML:** Layouts and UI elements are designed using XML, adhering to Android’s design guidelines.
- **MaterialDateTimePicker:** An external library used to provide a user-friendly date and time picker, enhancing the UI/UX for scheduling appointments.

### Backend:
- **Firebase Realtime Database:** Manages real-time data synchronization and storage for appointments, user profiles, and messages.
- **Firebase Firestore:** Used for more complex queries and data storage needs, especially for managing pet profiles and reviews.
- **Firebase Authentication:** Handles user authentication, supporting email/password login, SMS verification, and Google authentication.


## Application Flow

### For Pet Owners
1. **Registration/Login:** Pet owners register using their preferred authentication method.
2. **Home Page:** The home page displays relevant information, including upcoming appointments,, and recent reviews.
3. **Booking Appointments:** Users select the 'Book Appointment' option to view available slots and services, booking their preferred time.
4. **Managing Appointments:** Users can view, reschedule, or cancel their appointments through the 'My Appointments' section.
5. **Leaving Reviews:** After an appointment, users can leave a review and rating for the veterinarian.

### For Veterinarians
1. **View Appointments:** Veterinarians can view all upcoming and past appointments, accessing detailed client and pet information.
2. **Manage Availability:** Veterinarians can block out unavailable times to prevent bookings during those periods.
3. **Respond to Messages:** Veterinarians can communicate directly with pet owners through the app, answering questions.

## Configuration
1. **Firebase Setup:** Ensure you have a Firebase account, and the project is linked to Firebase with appropriate configurations for Realtime Database, Firestore, Authentication, and Cloud Storage.
2. **App Configuration:** Adjust the app settings to suit the specific needs of your clinic, including working hours, available services, and staff profiles.

## Future Improvements
- **Time Selector Enhancements:** Improve the time selector to handle edge cases, ensuring appointments are scheduled within clinic hours and avoiding overlaps.
- **Multi-Veterinarian Support:** Expand the app to support multiple veterinarians, allowing pet owners to choose their preferred vet and view specific availability.
- **Enhanced Push Notifications:** Introduce more granular notifications, such as reminders for follow-up appointments, vaccine schedules, or health check-ups.
- **Telemedicine Integration:** Add a feature for virtual consultations, allowing pet owners to connect with veterinarians through video calls directly within the app.

## Contributing
Contributions are welcome! Feel free to fork the repository and submit pull requests.
## Screenshots and Explanations



### Login Page
The Login Page allows users to sign in using their preferred authentication method, whether it be email/password, SMS verification, or Google account.

![login](https://github.com/user-attachments/assets/abbdca15-1904-4e8a-ba34-df07e8af29f6)







### Details Page
The Details Page provides in-depth information about a specific appointment, pet, or service, depending on the context within the app.

![Filling in personal details](https://github.com/user-attachments/assets/ee8b2313-7598-48db-8cfe-099761f5ee78)









### Home Page
The Home Page provides an overview of upcoming appointments, notifications for pending messages, and the overall rating of the veterinarian.

![home page](https://github.com/user-attachments/assets/d79e5a7d-7970-446e-81b8-cb794c0f3d90)








### Menu Page
The Menu Page provides easy navigation to different sections of the app, including booking appointments, viewing messages, managing profiles, and more.


## for Veterinarian ##
![menu](https://github.com/user-attachments/assets/777af7fc-9626-4bc3-96c8-30c22ab0023b)   
## for pet owner ## 
![menu2](https://github.com/user-attachments/assets/44563ff8-0c16-4f9c-b0e7-52f42c4be1e2)





### Appointment Booking Page
The Appointment Booking Page allows pet owners to view available time slots and book an appointment at their convenience.

![Added a new appointment](https://github.com/user-attachments/assets/c862fc4d-525d-4d2f-b0fe-49e6a26b0d48)







### Appointment Management Page
On this page, users can view all their appointments, reschedule or cancel them easily.

![all the appointment](https://github.com/user-attachments/assets/9c509e79-fa8e-4876-ae0d-7cd113270eab)




### new messages with image 
The "Add New Message" page is designed to help pet owners easily initiate communication with their veterinarian. This page provides an intuitive interface for composing new messages, with the added functionality of uploading a photo to give context to the message.

![Added a new message to the vet](https://github.com/user-attachments/assets/5cfd05db-40fb-4291-a203-8727b1d9b367)








### View All Messages Page
The "View All Messages" page allows users to keep track of their communication with the veterinary clinic. It is designed to provide a clear overview of all messages, their statuses, and detailed content for easy management and follow-up.

For Pet Owners:
Message Overview: Pet owners can view all their messages in one place, with clear indicators for the status of each message (e.g., Pending, Responded, Completed).
Message Details: Clicking on a message will display the full content, including any responses from the veterinarian.
Status Tracking: Pet owners can see which messages have been responded to by the veterinarian and which are still awaiting a reply.
For Veterinarians:
Message Management: Veterinarians can view all incoming messages from pet owners.
 Veterinarians can see all incoming messages from pet owners that are still awaiting a response.
Respond to Messages: Veterinarians can reply to any message directly from this page. Once a response is sent, the message's status is updated to "Completed."

![All messages](https://github.com/user-attachments/assets/5cb827be-1ebc-4607-a586-e10a3e42ceef)




## for Veterinarians ##
![The veterinarian's response to the message](https://github.com/user-attachments/assets/f574e948-2987-40de-a45a-c2b1f33092f8)







### Veterinarian's Setting Page
The "Veterinarian's Setting" page allows veterinarians to manage their availability. They can view and block unavailable times, add new block times, and update the clinic's start and end times to ensure accurate appointment scheduling.

![settings](https://github.com/user-attachments/assets/b7c404b8-b47b-4420-964f-35ed4b4d8787)







### Reviews and Ratings Page
Pet owners can leave reviews and rate the services provided by veterinarians, helping others make informed decisions.

![Writing a review](https://github.com/user-attachments/assets/1afd4d7b-d21b-4596-8e4c-0920ae8d9dba)







## View All Reviews Page
The "View All Reviews" page displays customer feedback and ratings for the veterinary services. Pet owners can browse through all the reviews left by other users, helping them make informed decisions. Veterinarians can also monitor reviews to improve their services.

![All reviews](https://github.com/user-attachments/assets/e91bddd8-cb07-45d3-9a63-fa7a11fef3bb)









### About Us Page
The About Us Page offers details about the clinic, including contact information, location, and a brief overview of the services offered.

![about us](https://github.com/user-attachments/assets/a1400a6e-b8ad-41f6-bdee-ef81dd10b700)






This README gives a comprehensive overview of the VetAppointment project, covering its features, technology stack, application flow, and future enhancements.
