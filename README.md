To test the ContentProvider in QuizAppHvl, I wrote:
```
PS C:\Users\feder\AppData\Local\Android\Sdk\platform-tools> .\adb.exe shell content query --uri content://com.example.quizapphvl.provider/image
```
inside the AndroidStudio terminal.

When the app starts, before doing update to the items, we can see this output:
```
Row: 0 name=Nauru, idImage=2131165419, URI=NULL
Row: 1 name=Norway, idImage=2131165405, URI=NULL
Row: 2 name=French, idImage=2131165320, URI=NULL
Row: 3 name=Mexico, idImage=2131165403, URI=NULL
Row: 4 name=Spain, idImage=2131165319, URI=NULL
```
If, for example, I remove the Nauru flag, so I will see:
PS C:\Users\feder\AppData\Local\Android\Sdk\platform-tools> .\adb.exe shell content query --uri content://com.example.quizapphvl.provider/image
```
Row: 0 name=Norway, idImage=2131165405, URI=NULL
Row: 1 name=French, idImage=2131165320, URI=NULL
Row: 2 name=Mexico, idImage=2131165403, URI=NULL
Row: 3 name=Spain, idImage=2131165319, URI=NULL
```

And, if I add a new image from my gallery, I will see:
```
PS C:\Users\feder\AppData\Local\Android\Sdk\platform-tools> .\adb.exe shell content query --uri content://com.example.quizapphvl.provider/image
Row: 0 name=Norway, idImage=2131165405, URI=NULL
Row: 1 name=French, idImage=2131165320, URI=NULL
Row: 2 name=Mexico, idImage=2131165403, URI=NULL
Row: 3 name=Spain, idImage=2131165319, URI=NULL
Row: 4 name=Swan, idImage=-1, URI=content://media/picker/0/com.android.providers.media.photopicker/media/1000080388
```
TODO: it would be much more better if I removed the idImage and I used the same URI for every image and not only for the images added from the Gallery.
