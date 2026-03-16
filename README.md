To test the ContentProvider in QuizAppHvl, I wrote:
```
PS C:\Users\feder\AppData\Local\Android\Sdk\platform-tools> .\adb.exe shell content query --uri content://com.example.quizapphvl.provider/image
```
inside the AndroidStudio terminal.

When the app starts, before doing update to the items, we can see this output:
```
Row: 0 name=Nauru, imageUri=android.resource://com.example.quizapphvl/2131165419
Row: 1 name=Spain, imageUri=android.resource://com.example.quizapphvl/2131165319
Row: 2 name=Norway, imageUri=android.resource://com.example.quizapphvl/2131165405
Row: 3 name=French, imageUri=android.resource://com.example.quizapphvl/2131165320
Row: 4 name=Mexico, imageUri=android.resource://com.example.quizapphvl/2131165403

```
If, for example, I remove the Nauru flag, so I will see:
PS C:\Users\feder\AppData\Local\Android\Sdk\platform-tools> .\adb.exe shell content query --uri content://com.example.quizapphvl.provider/image
```
Row: 0 name=Spain, imageUri=android.resource://com.example.quizapphvl/2131165319
Row: 1 name=Norway, imageUri=android.resource://com.example.quizapphvl/2131165405
Row: 2 name=French, imageUri=android.resource://com.example.quizapphvl/2131165320
Row: 3 name=Mexico, imageUri=android.resource://com.example.quizapphvl/2131165403
```

And, if I add a new image from my gallery, I will see:
```
PS C:\Users\feder\AppData\Local\Android\Sdk\platform-tools> .\adb.exe shell content query --uri content://com.example.quizapphvl.provider/image
Row: 0 name=Spain, imageUri=android.resource://com.example.quizapphvl/2131165319
Row: 1 name=Norway, imageUri=android.resource://com.example.quizapphvl/2131165405
Row: 2 name=French, imageUri=android.resource://com.example.quizapphvl/2131165320
Row: 3 name=Mexico, imageUri=android.resource://com.example.quizapphvl/2131165403
Row: 4 name=Pizza, imageUri=content://media/picker/0/com.android.providers.media.photopicker/media/1000081048
```

**TEST JAVA ESPRESSO**

***Clicking a button in the main-menu takes ou to the right sub-activty.***

I chose the Gallery Activity.
To make the test working, I started a new Rule that says that the test has to start from the MainActivity, then, it search for a button with this id: `buttonGallery`, so it tries to press the button and, if after pressing the button, it sees that exist a node where it's written "GALLERY OF WORLD'S FLAGS", so this means that the code works correctly and the test pass.

***Is the score update correctly in the quiz (the test submits at least one right/wrong answer each and you check if the score is correct afterwards***

To test this I had to modify a little the code of my QuizActivity.
I added differents tags that checked for the button linked to the correct answer and for one button linked to the wrong answer.
After this, I started a new Rule that says that the test has to start from the GalleryActivity, for the case of the correct button, the test behaves in this way: it search for the button linked to the "correctButton" tag, then, it press the button. At this point, it search the node linked to the "textWithScore" button, and, if its text is equal to "your actual score is: 1/1", it means that the test pass.
For the case of the wrong button, the test behaves in the same way, but it search for a different text: "your actual score is: 0/1".

***A test that checks that the number of registered pictures/persons is correct after adding/deleting an entry.***

This type of test was a little more difficult to implement.
Firstly, I created the two rules, one for the composeActivity, the other one for the intent.
But after that, I understood that this could create problem because it's wrong having two same rules for the same test (in this particular case, the test works, but it's a lot slower)
So, I used the @Before and the @After tag.
With @Before, the program does what is written inside everytime before a test is starting.
In this way, the test will be ready to intercept every request of intent (like the request to opening an others app/screen)
@After is the opposite of @Before, it does what it's written inside it everytime a test is ended.
In this case, I used it to clean the cache memory from the test-image.
For the adding button, the test behaves in this way: it uses a stubbling intent to simulate the selection of the image, for this reason, I stored a Uri from Resources in it.
The test creates a fake Intent that contains the URI of the image, then, it puts this data into the ActivityResult, that with the RESULT_OK tag confirms the chose.
At this point, if the test tries to open an intent like "open the gallery", the  intending(anyIntent()).respondWith(result) block it and return to it the fake image.
Then, the test tries to click the button that add the image (thanks to the tag image), the Stubbling intent tricks it and pass to it the fake image, the test confirm the chose in the dialog window and performs to writing the name of the new image ("Test).
If the screen show that there are 6 images, this means that the code works correctly.

For the deleting of the images, I added a testTag in the first "delate" button of the Gallery Activity, and then the test performs to click the button, if the text that counts the images is 4 like we want, so it means that the Test is passed.
